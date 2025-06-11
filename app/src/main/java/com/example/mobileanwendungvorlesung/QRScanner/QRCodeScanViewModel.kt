package com.example.mobileanwendungvorlesung.QRScanner

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import com.google.common.util.concurrent.ListenableFuture // <--- STELL SICHER, DASS DIESER IMPORT VORHANDEN IST!
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobileanwendungvorlesung.data.Contact
import com.example.mobileanwendungvorlesung.data.ContactRepository
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class QRScanViewModel(
    private val contactRepository: ContactRepository? = null
) : ViewModel() {

    // --- Klassenvariablen (Zugriff von überall im ViewModel) ---
    private val _hasCameraPermission = MutableStateFlow(false)
    val hasCameraPermission: StateFlow<Boolean> = _hasCameraPermission.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _scannedContactEvent = Channel<Contact>()
    val scannedContactEvent = _scannedContactEvent.receiveAsFlow()

    private val gson = Gson()
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC
        )
        .build()
    private val scanner = BarcodeScanning.getClient(options)

    // NEU: Speichere die resolved ProcessCameraProvider Instanz hier
    private var cameraProviderInstance: ProcessCameraProvider? = null // <--- WICHTIGE KLASSENVARIABLE

    // --- Funktionen, die vom Composable aufgerufen werden ---

    fun onPermissionResult(granted: Boolean) {
        _hasCameraPermission.value = granted
    }

    @OptIn(ExperimentalGetImage::class)
    fun bindCameraUseCases(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context) // Dies ist eine LOKALE Variable
        // für DIESE Funktion

        cameraProviderFuture.addListener({
            val currentCameraProvider = cameraProviderFuture.get() // HOLT die INSTANZ aus der Future
            this.cameraProviderInstance = currentCameraProvider // <--- SPEICHERE die INSTANZ in der KLASSENVARIABLEN

            if (currentCameraProvider == null) {
                Log.e("QRScanViewModel", "CameraProvider ist null nach get(). Bindung abgebrochen.")
                return@addListener
            }

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                        if (_isProcessing.value) {
                            imageProxy.close()
                            return@setAnalyzer
                        }
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            _isProcessing.value = true
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )
                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        val raw = barcode.rawValue ?: barcode.rawBytes?.toString(Charsets.UTF_8)
                                        if (raw != null) {
                                            try {
                                                val contact = gson.fromJson(raw, Contact::class.java)
                                                viewModelScope.launch {
                                                    _scannedContactEvent.send(contact)
                                                }
                                            } catch (e: Exception) {
                                                Log.e("QRScannerViewModel", "Fehler beim Parsen des QR-Codes als Kontakt: ${e.message}", e)
                                            }
                                        } else {
                                            Log.e("QRScannerViewModel", "Kein decodierbarer QR-Code-Inhalt gefunden.")
                                        }
                                    }
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                    _isProcessing.value = false
                                }
                        } else {
                            imageProxy.close()
                        }
                    }
                }

            try {
                // Immer alle bestehenden Bindungen aufheben, bevor neue gebunden werden
                currentCameraProvider.unbindAll() // <--- Verwendet die aktuelle Provider-Instanz
                currentCameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("QRScanViewModel", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // AUFRÄUM-METHODE: WIRD AUTOMATISCH AUFGERUFEN, WENN DAS VIEWMODEL NICHT MEHR BENÖTIGT WIRD
    override fun onCleared() {
        super.onCleared()
        // Jetzt greifen wir auf die Klassenvariable zu, die die ProcessCameraProvider-Instanz speichert
        cameraProviderInstance?.unbindAll() // <--- HIER WIRD unbindAll() AUFGERUFEN!
        Log.d("QRScanViewModel", "Kamera-Use-Cases in onCleared() entbunden.")
        cameraProviderInstance = null // Referenz löschen
    }

    class Factory(private val contactRepository: ContactRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QRScanViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return QRScanViewModel(contactRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
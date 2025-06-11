package com.example.mobileanwendungvorlesung.QRScanner

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.mobileanwendungvorlesung.data.Contact
import com.example.mobileanwendungvorlesung.data.toContact
import com.example.mobileanwendungvorlesung.network.RandomUserResponse
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

@ExperimentalGetImage
@Composable
fun QRCodeScannerScreen(
    onScanSuccess: (Contact) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isProcessing = false

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        Box(modifier = Modifier.fillMaxSize()) {

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    val scanner = BarcodeScanning.getClient()
                    val imageAnalyzer = ImageAnalysis.Builder()
                        .build()
                        .also {
                            it.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                if (isProcessing){
                                    imageProxy.close()
                                    return@setAnalyzer
                                }
                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    isProcessing = true
                                    val image = InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees
                                    )
                                    scanner.process(image)
                                        .addOnSuccessListener { barcodes ->
                                            for (barcode in barcodes) {
                                                barcode.rawValue?.let { raw ->
                                                    try {
                                                        val contact = Gson().fromJson(
                                                            raw,
                                                            Contact::class.java
                                                        )
                                                        onScanSuccess(contact)
                                                    } catch (e: Exception) {
                                                        // Fehler beim Parsen
                                                        Log.e("QRScanner", "Fehler beim Parsen des QR-Codes als Kontakt: ${e.message}", e)
                                                    }
                                                }
                                            }
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                            isProcessing = false
                                        }
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalyzer
                            )
                        } catch (_: Exception) {
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )
            Button(
                onClick = {
                    val dummyJson = """
                    {
          "results": [
            {
              "gender": "male",
              "name": {
                "title": "Mr",
                "first": "Max",
                "last": "Mustermann"
              },
              "location": {
                "street": {
                  "number": 10,
                  "name": "Musterstraße"
                },
                "city": "Musterstadt",
                "state": "Irgendwo",
                "country": "Deutschland",
                "postcode": "12345"
              },
              "email": "max@example.com",
              "phone": "+49123456789",
              "cell": "+49123456789",
              "picture": {
                "large": "https://randomuser.me/api/portraits/men/1.jpg",
                "medium": "",
                "thumbnail": ""
              }
            }
          ],
          "info": {
            "seed": "abc",
            "results": 1,
            "page": 1,
            "version": "1.4"
          }
        }
                """.trimIndent()
                    try {
                        val response = Json.decodeFromString<RandomUserResponse>(dummyJson)
                        val result = response.results.firstOrNull()
                        result?.let {
                            onScanSuccess(it.toContact())
                        }
                        Log.d("QRScannerTest", "Simulierter Scan erfolgreich. Kontakt: ")
                    } catch (e: Exception) {
                        Log.e("QRScannerTest", "Fehler beim Parsen des simulierten JSON: ${e.message}")
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Simuliere QR-Code Scan")
            }


            /*Button(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Zurück")
            }*/
        }
    } else {
        Text("Kamerazugriff benötigt.")
    }
}


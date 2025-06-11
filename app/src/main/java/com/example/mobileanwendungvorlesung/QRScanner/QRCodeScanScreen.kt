package com.example.mobileanwendungvorlesung.QRScanner

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ExperimentalGetImage
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel // Wichtig: Für viewModel()

import com.example.mobileanwendungvorlesung.data.Contact // Nur noch als Typ
// KEIN Gson-Import mehr hier!

@ExperimentalGetImage
@Composable
fun QRCodeScannerScreen(
    onScanSuccess: (Contact) -> Unit,
    onBack: () -> Unit,
    // ViewModel wird jetzt hier instanziiert oder injiziert
    viewModel: QRScanViewModel = viewModel() // Füge dies hinzu!
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Beobachte den State vom ViewModel
    val hasCameraPermission by viewModel.hasCameraPermission.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState() // Kann optional sein, wenn du es nicht in der UI anzeigst

    // LaunchedEffect, um auf Events vom ViewModel zu reagieren
    LaunchedEffect(Unit) {
        viewModel.scannedContactEvent.collect { contact ->
            onScanSuccess(contact)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            viewModel.onPermissionResult(granted) // ViewModel über Ergebnis informieren
        }
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
                    // Binde die Kamera-Use-Cases über das ViewModel
                    viewModel.bindCameraUseCases(ctx, lifecycleOwner, previewView)
                    previewView
                }
            )

            // Dein Zurück-Button
            Button(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Zurück zum Hauptmenü")
                Text(" Zurück")
            }

            // Optional: Ein Lade-Indikator, wenn isProcessing true ist
            if (isProcessing) {
                // Hier könntest du einen Lade-Spinner oder ähnliches anzeigen
                // Beispiel: CircularProgressIndicator()
            }
        }
    } else {
        Text("Kamerazugriff benötigt.")
    }
}
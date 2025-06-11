package com.example.mobileanwendungvorlesung.Details

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mobileanwendungvorlesung.QRScanner.generateQrCodeBitmap
import com.google.gson.Gson

@Composable
fun ContactDetailScreen(
    contactDetailViewModel: ContactDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by contactDetailViewModel.uiState.collectAsStateWithLifecycle()
    val contact = uiState.contact

    // Zustandsvariablen für die Anzeige des QR-Code-Dialogs
    var showQrCodeDialog by remember { mutableStateOf(false) }
    var qrCodeBitmap: Bitmap? by remember { mutableStateOf(null) }
    var serializedContactData: String? by remember { mutableStateOf(null) } // Zum Speichern der JSON-Daten

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (contact != null) {
            /*if (contact.imageRes != 0) {
                Spacer(Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = contact.imageRes),
                    contentDescription = "Kontaktbild",
                    modifier = Modifier.size(100.dp) // Beispielgröße
                )
            }*/
            AsyncImage(
                model = contact.imageRes, // Die URL aus deinem Contact-Objekt
                contentDescription = "Profilbild von ${contact.name}",
                modifier = Modifier
                    .size(200.dp) // Beispielgröße
                    .clip(CircleShape), // Stelle sicher, dass du CircleShape importiert hast
                contentScale = ContentScale.Crop // Bild zuschneiden, um Kreis zu füllen
            )

            Text(
                text = "Details für: ${contact.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Telefon: ${contact.phone}",
                fontSize = 18.sp
            )
            Text(
                text = "E-Mail: ${contact.email}",
                fontSize = 18.sp
            )
            Text(
                text = "Strasse: ${contact.street}",
                fontSize = 18.sp
            )
            Text(
                text = "HausNr.: ${contact.houseNr}",
                fontSize = 18.sp
            )
            Text(
                text = "PLZ.: ${contact.postcode}",
                fontSize = 18.sp
            )
            Text(
                text = "Stadt.: ${contact.city}",
                fontSize = 18.sp
            )

            Spacer(Modifier.height(24.dp))

            // --- Buttons ---
            Button(onClick = onNavigateBack) {
                Text("Zurück zur Liste")
            }

            Spacer(Modifier.height(8.dp))

            // Button zum Generieren und Anzeigen des QR-Codes
            Button(
                onClick = {
                    if (contact != null) {
                        // 1. Kontaktobjekt in JSON-String serialisieren
                        // Gson ist bereits in deinem Projekt, daher hier verwendet.
                        serializedContactData = Gson().toJson(contact)
                        // 2. QR-Code-Bitmap generieren
                        qrCodeBitmap = serializedContactData?.let { generateQrCodeBitmap(it) }
                        // 3. Dialog anzeigen
                        showQrCodeDialog = true
                    }
                }
            ) {
                Text("QR-Code generieren")
            }

            // Optional: Button zum Löschen (falls noch nicht voll funktionsfähig)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    // Hier würde Logik zum Löschen aufgerufen, z.B. contactDetailViewModel.deleteContact(contact)
                    // Und dann zurück navigieren: onNavigateBack()
                }
            ) {
                Text("Kontakt löschen (WIP)")
            }

        } else {
            Text(text = "Kontakt nicht gefunden.")
        }
    }

    // --- QR-Code-Anzeige-Dialog ---
    if (showQrCodeDialog) {
        Dialog(onDismissRequest = { showQrCodeDialog = false }) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "QR-Code für ${contact?.name}", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(16.dp))
                    qrCodeBitmap?.let {
                        // Bitmap zu ImageBitmap konvertieren für die Compose Image Composable
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR Code für ${contact?.name}",
                            modifier = Modifier.size(250.dp) // Größe des QR-Codes anpassen
                        )
                    } ?: Text("QR-Code konnte nicht generiert werden.") // Fehlermeldung, falls Bitmap null ist
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { showQrCodeDialog = false }) {
                        Text("Schließen")
                    }
                    // Optional: Den rohen JSON-String für Debugging anzeigen
                    // serializedContactData?.let {
                    //     Spacer(Modifier.height(8.dp))
                    //     Text("Daten: $it", maxLines = 2, overflow = TextOverflow.Ellipsis)
                    // }
                }
            }
        }
    }
}




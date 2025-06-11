// src/main/java/com/example/mobileanwendungvorlesung/Settings/Settings.kt
package com.example.mobileanwendungvorlesung.Settings

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel // Wichtiger Import für ViewModel

@Composable
fun SettingsScreen( // <<< Geändert: Name von ShowSettings zu SettingsScreen
    settingsViewModel: SettingsViewModel = viewModel(), // Standardwert, wird von MainActivity überschrieben
    modifier: Modifier = Modifier
) {
    Column {
        Box()
        {
            Text(
                text = "Einstellungen",
                modifier = Modifier,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold

            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Mit diesem Button werden die Daten in der DB gelöscht.",
        )
        Button(
            onClick = {
                Log.d("SettingsScreen", "Button 'Datenbank leeren' geklickt.")
                settingsViewModel.clearAllContacts() // Aufruf der ViewModel-Methode
            }
        ) {
            Text("Datenbank Leeren")
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Mit diesem Button werden die Kontaktdaten neu aus der API geladen und in die DB gespeichert.",
        )
        Button(
            onClick = {
                settingsViewModel.refreshContactsFromApi() // Aufruf der ViewModel-Methode
            }
        ) {
            Text("Kontakte neu laden (API)")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Mit diesem Button werden die Kontaktdaten aus der Datenbank lokal auf dem PC gespeichert.",
        )
        Button(
            onClick = {
                Log.d("SettingsScreen", "Button 'Kontakte exportieren' geklickt.")
                settingsViewModel.exportContactsToDownloads()
            }
        ) {
            Text("Kontakte exportieren (Downloads)")
        }
    }
}
// src/main/java/com/example/mobileanwendungvorlesung/Screen.kt
package com.example.mobileanwendungvorlesung

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Add // Wichtig: Für den QR-Scanner Button
import androidx.compose.material.icons.filled.Settings
// Importiere Icons für Detail und Add, wenn sie in deinem originalen Screen.kt waren
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val titel: String, val icon: ImageVector) {
        object ContactList : Screen("contactList", "Kontakte", Icons.Default.List)
        // Objekt für die Detailansicht - wird nicht direkt in der Bottom Bar verwendet,
        // aber ist wichtig für die Navigation dorthin.
        object Detail : Screen("contactDetail", "Details", Icons.Default.AccountBox)
        // Objekt für "Kontakt hinzufügen" - wird nicht direkt in der Bottom Bar verwendet,
        // da du stattdessen den QR-Scanner dort haben möchtest.
        object AddContact : Screen("addContact", "Hinzufuegen", Icons.Default.Add)

        // NEU: Objekt für den QR-Scanner, der in der Bottom Bar erscheinen soll
        object QRScanner : Screen("qrScanner", "Scan", Icons.Default.Add)

        object Settings : Screen("settings", "Einstellungen",Icons.Default.Settings)
}
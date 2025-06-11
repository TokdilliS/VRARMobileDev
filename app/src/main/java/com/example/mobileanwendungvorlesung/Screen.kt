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

        // ÄNDERE DIESES OBJEKT: Passe den Route-String an
        object Detail : Screen("contact_detail", "Details", Icons.Default.AccountBox){ // <<< HIER GEÄNDERT!
                fun createRoute(contactId: Int) = "contact_detail/$contactId"
        }

        // Wenn du einen separaten "AddContact"-Screen hast
        object AddContact : Screen("addContact", "Hinzufuegen", Icons.Default.Add)

        object QRScanner : Screen("qrScanner", "Scan", Icons.Default.Add)

        object Settings : Screen("settings", "Einstellungen",Icons.Default.Settings)
}
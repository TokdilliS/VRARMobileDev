package com.example.mobileanwendungvorlesung.Contactlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mobileanwendungvorlesung.data.Contact


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    contactListViewModel: ContactListViewModel = viewModel(), // Standardwert, wird von MainActivity überschrieben
    onContactClick: (Contact) -> Unit
) {
    // uiState aus dem ViewModel sammeln
    val uiState by contactListViewModel.uiState.collectAsStateWithLifecycle()
    val contacts = uiState.contactList

    /* // Optional: Kontakte beim ersten Start der Liste aktualisieren
    LaunchedEffect(Unit) {
        contactListViewModel.refreshContacts()
    }*/

    // Für die Suchleiste
    var textFieldState by rememberSaveable(stateSaver = TextFieldState.Saver) {
        mutableStateOf(TextFieldState())
    }
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Dummy-Daten für die Suche (müsste später mit tatsächlichen Kontakten gefiltert werden)
    val searchResults = listOf("Kontakt 1", "Kontakt 2", "Kontakt 3") // Ersetze dies später durch echte Filterlogik

    Column(Modifier.fillMaxSize()) {
        // Suchleiste oben
        Box(
            Modifier
                .fillMaxWidth()
                .semantics { isTraversalGroup = true }
        ) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .semantics { traversalIndex = 0f },
                inputField = {
                    SearchBarDefaults.InputField(
                        query = textFieldState.text.toString(),
                        onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                        onSearch = {
                            // Hier müsste die eigentliche Suchlogik implementiert werden,
                            // die das ViewModel zum Filtern der Kontakte aufruft.
                            // contactListViewModel.searchContacts(textFieldState.text.toString())
                            expanded = false
                        },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = { Text("Search") }
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                // Anzeige der Suchergebnisse
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    searchResults.forEach { result ->
                        ListItem(
                            headlineContent = { Text(result) },
                            modifier = Modifier
                                .clickable {
                                    textFieldState.edit { replace(0, length, result) }
                                    expanded = false
                                }
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        // Trennlinie unter der Suchleiste
        HorizontalDivider(thickness = 2.dp)

        // Kontaktliste
        val sortedContacts = contacts.sortedBy { contact ->
            contact.name.split(" ").last().lowercase()
        }
        LazyColumn(Modifier.fillMaxSize()) { // Modifier.fillMaxSize() für die LazyColumn
            items(sortedContacts) { contact ->
                ContactListItem(contact = contact) {
                    onContactClick(contact)
                }
            }
        }
    }
}

// Separate Composable für ein einzelnes Kontaktlistenelement
@Composable
fun ContactListItem(contact: Contact, onContactClick: (Contact) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onContactClick(contact) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        /*Image(
            painter = painterResource(contact.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )*/
        AsyncImage(
            model = contact.imageRes, // Die URL aus deinem Contact-Objekt
            contentDescription = "Profilbild von ${contact.name}",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(contact.name, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.width(16.dp))
    }
    HorizontalDivider(thickness = 2.dp)
}
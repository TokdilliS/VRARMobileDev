// src/main/java/com/example/mobileanwendungvorlesung/Details/ContactDetailViewModel.kt
package com.example.mobileanwendungvorlesung.Details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobileanwendungvorlesung.data.Contact
import com.example.mobileanwendungvorlesung.data.ContactRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// UI State für die Kontaktdetails
data class ContactDetailUiState(val contact: Contact? = null)

class ContactDetailViewModel(
    private val contactRepository: ContactRepository,
    savedStateHandle: SavedStateHandle // Um die Kontakt-ID von der Navigation zu erhalten
) : ViewModel() {

    // Die ID des Kontakts, der angezeigt werden soll
    private val contactId: Int = checkNotNull(savedStateHandle["contactId"])

    // Der StateFlow, der die Details des Kontaks für die UI bereitstellt
    val uiState: StateFlow<ContactDetailUiState> =
        contactRepository.getContactStream(contactId) // <<< Geändert: Auf getContactStream
            .filterNotNull() // Stelle sicher, dass der Kontakt nicht null ist (wenn er existiert)
            .map { ContactDetailUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ContactDetailUiState()
            )

    // Funktion zum Löschen des angezeigten Kontakts
    fun deleteContact() {
        viewModelScope.launch {
            uiState.value.contact?.let { contactRepository.deleteContact(it) }
        }
    }

    // Funktion zum Aktualisieren des angezeigten Kontakts
    fun updateContact(updatedContact: Contact) {
        viewModelScope.launch {
            contactRepository.updateContact(updatedContact)
        }
    }

    // ViewModelFactory, um das ContactDetailViewModel mit dem Repository und SavedStateHandle zu instanziieren
    companion object {
        // Dies ist die korrigierte Factory
        fun create(contactRepository: ContactRepository, savedStateHandle: SavedStateHandle): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ContactDetailViewModel::class.java)) {
                        return ContactDetailViewModel(
                            contactRepository,
                            savedStateHandle // Wichtig: savedStateHandle muss hier übergeben werden
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
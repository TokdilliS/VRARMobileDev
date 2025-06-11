package com.example.mobileanwendungvorlesung.Contactlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobileanwendungvorlesung.data.Contact
import com.example.mobileanwendungvorlesung.data.ContactRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// UI State für die Kontaktliste
data class ContactListUiState(val contactList: List<Contact> = emptyList())

class ContactListViewModel(private val contactRepository: ContactRepository) : ViewModel() {

    // Der StateFlow, der die Liste der Kontakte für die UI bereitstellt
    val uiState: StateFlow<ContactListUiState> =
        contactRepository.getAllContactsStream()
            .map { ContactListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ContactListUiState()
            )

    // Funktion zum Hinzufügen eines Kontakts (falls von der Liste aus möglich, z.B. über Floating Action Button)
    fun addContact(contact: Contact) {
        viewModelScope.launch {
            contactRepository.insertContact(contact)
        }
    }

    // Funktion zum Löschen eines Kontakts
    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            contactRepository.deleteContact(contact)
        }
    }

    // Funktion zum Aktualisieren eines Kontakts
    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            contactRepository.updateContact(contact)
        }
    }

    // Funktion zum Initialisieren/Aktualisieren der Kontakte von der API (wird das Repository aufrufen)
    fun refreshContacts() {
        viewModelScope.launch {
            contactRepository.refreshContacts()
        }
    }

    // ViewModelFactory, um das ContactListViewModel mit dem Repository zu instanziieren
    companion object {
        // Updated Factory to accept ContactRepository
        fun create(contactRepository: ContactRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ContactListViewModel::class.java)) {
                        return ContactListViewModel(contactRepository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
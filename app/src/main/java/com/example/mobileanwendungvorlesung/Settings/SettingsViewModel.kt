// src/main/java/com/example/mobileanwendungvorlesung/Settings/SettingsViewModel.kt
package com.example.mobileanwendungvorlesung.Settings

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobileanwendungvorlesung.data.ContactRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val contactRepository: ContactRepository, application: Application) : ViewModel() {
    private val appContext = application.applicationContext
    // Funktion zum Löschen aller Kontakte in der Datenbank
    fun clearAllContacts() {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "deleteAllContacts() im ViewModel aufgerufen.")
            contactRepository.deleteAllContacts() // Aufruf der neuen Methode im Repository
        }
    }

    // Funktion zum Neuherunterladen der Kontakte von der API und Speichern in der Datenbank
    fun refreshContactsFromApi() {
        viewModelScope.launch {
            contactRepository.refreshContacts() // Aufruf der Methode im Repository
        }
    }

    fun exportContactsToDownloads() {
        viewModelScope.launch {
            val success = contactRepository.exportContactsToDownloads(appContext)
            if (success) {
                Toast.makeText(appContext, "Kontakte erfolgreich exportiert!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(appContext, "Fehler beim Export der Kontakte.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ViewModelFactory, um das SettingsViewModel mit dem Repository zu instanziieren
    companion object {
        // Die create-Methode muss jetzt auch die Application erhalten
        fun create(contactRepository: ContactRepository, application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        // Application an den Konstruktor übergeben
                        return SettingsViewModel(contactRepository, application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
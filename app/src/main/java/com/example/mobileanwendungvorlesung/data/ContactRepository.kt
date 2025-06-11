// src/main/java/com/example/mobileanwendungvorlesung/data/ContactRepository.kt
package com.example.mobileanwendungvorlesung.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.flow.Flow
import com.example.mobileanwendungvorlesung.network.KtorService
import com.example.mobileanwendungvorlesung.network.RandomUserResponse
import android.util.Log // Für einfaches Logging
import com.google.gson.Gson // Für JSON-Serialisierung
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ContactRepository(
    private val contactDao: ContactDao,
    private val ktorService: KtorService
) {
    fun getAllContactsStream(): Flow<List<Contact>> = contactDao.getAll()
    fun getContactStream(id: Int): Flow<Contact?> = contactDao.getContact(id)

    suspend fun insertContact(contact: Contact) = contactDao.insert(contact)
    suspend fun deleteContact(contact: Contact) = contactDao.delete(contact)
    suspend fun updateContact(contact: Contact) = contactDao.update(contact)
    suspend fun deleteAllContacts() = contactDao.deleteAllContacts()

    suspend fun refreshContacts() {
        try {
            Log.d("ContactRepository", "Starte API-Aufruf für Kontakte (RandomUser.me)...")
            val randomUserResponse: RandomUserResponse = ktorService.getRandomUsersFromApi()
            Log.d("ContactRepository", "API-Antwort erhalten, Anzahl: ${randomUserResponse.results.size}")

            val contacts = randomUserResponse.results.map { result ->
                // Hier mappen wir die Daten von RandomUserResult auf DEIN Contact-Objekt.
                // Achte darauf, dass alle Felder der Contact-Datenklasse befüllt werden.
                Contact(
                    // id wird von Room auto-generiert, daher keine explizite Zuweisung hier
                    name = "${result.name.first} ${result.name.last}",
                    phone = result.phone,
                    email = result.email,
                    birthday = "", // RandomUser.me liefert kein direktes Geburtsdatum, setze Standardwert
                    street = result.location.street.name,
                    houseNr = result.location.street.number.toString(), // Konvertiere Int zu String
                    postcode = result.location.postcode ?: "", // Kann von API null sein, handle dies
                    city = result.location.city,
                    imageRes = result.picture.thumbnail
                )
            }
            contactDao.deleteAllContacts() // Lösche alle alten Kontakte
            contactDao.insertAll(contacts) // Füge die neuen Kontakte hinzu
            Log.d("ContactRepository", "Kontakte in Datenbank aktualisiert.")
        } catch (e: Exception) {
            Log.e("ContactRepository", "Fehler beim Laden oder Mappen der Kontakte von der API", e)
            e.printStackTrace()
        }
    }

    suspend fun exportContactsToDownloads(context: Context): Boolean {
        val contacts = contactDao.getAllContactsList()
        if (contacts.isEmpty()) {
            Log.d("ContactRepository", "Keine Kontakte zum Exportieren gefunden.")
            return false
        }

        val jsonString = Gson().toJson(contacts)
        val fileName = "contacts_export_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.json"

        val resolver = context.contentResolver
        var outputStream: OutputStream? = null
        var uri: Uri? = null

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
            }

            uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            if (uri == null) {
                Log.e("ContactRepository", "Fehler: Konnte keinen neuen MediaStore-Eintrag für Downloads erstellen.")
                return false
            }

            outputStream = resolver.openOutputStream(uri)
            outputStream?.write(jsonString.toByteArray())
            outputStream?.flush()

            Log.d("ContactRepository", "Kontakte erfolgreich in Downloads als $fileName exportiert.")
            return true
        } catch (e: IOException) {
            Log.e("ContactRepository", "Fehler beim Exportieren der Kontakte: ${e.message}", e)
            uri?.let {
                try {
                    resolver.delete(it, null, null)
                    Log.d("ContactRepository", "Partiell erstellte Datei gelöscht.")
                } catch (deleteEx: Exception) {
                    Log.e("ContactRepository", "Fehler beim Löschen der partiellen Datei: ${deleteEx.message}", deleteEx)
                }
            }
            return false
        } finally {
            outputStream?.close()
        }
    }
}
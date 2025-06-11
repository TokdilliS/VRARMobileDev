// src/main/java/com/example/mobileanwendungvorlesung/data/ContactDao.kt
package com.example.mobileanwendungvorlesung.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAll(): Flow<List<Contact>> // Flow für reaktive Daten

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContact(id: Int): Flow<Contact?> // <<< Geändert: Kann null zurückgeben, wenn nicht gefunden

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    suspend fun getAllContactsList(): List<Contact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact) // <<< Geändert: Vereinfacht von insertContact

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<Contact>)

    @Update
    suspend fun update(contact: Contact) // <<< Geändert: Vereinfacht von updateContact

    @Delete
    suspend fun delete(contact: Contact) // <<< Geändert: Vereinfacht von deleteContact

    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts() // <<< Geändert: Name zur Konsistenz mit SettingsViewModel
}
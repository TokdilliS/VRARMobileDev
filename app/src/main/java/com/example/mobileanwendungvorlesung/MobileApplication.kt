package com.example.mobileanwendungvorlesung

import android.app.Application
import com.example.mobileanwendungvorlesung.data.AppDatabase
import com.example.mobileanwendungvorlesung.data.ContactRepository
import com.example.mobileanwendungvorlesung.network.KtorService


class MobileApplication : Application() {
    // Lazy initialisiert das AppDatabase Singleton
    val database: AppDatabase by lazy { AppDatabase.Companion.getDatabase(this) }

    // Lazy initialisiert den KtorService
    val ktorService by lazy { KtorService() }

    // Lazy initialisiert das ContactRepository mit der DAO und dem KtorService
    val contactRepository: ContactRepository by lazy {
        ContactRepository(database.contactDao(), ktorService)
    }
}
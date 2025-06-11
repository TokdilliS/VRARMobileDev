package com.example.mobileanwendungvorlesung.data

import android.content.Context
import com.example.mobileanwendungvorlesung.network.KtorService // Wichtig: Stelle sicher, dass dieser Import korrekt ist


class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    // Initialisiere den KtorService hier
    private val ktorService: KtorService by lazy {
        KtorService()
    }

    // Initialisiere das ContactRepository hier
    override val contactRepository: ContactRepository by lazy {
        ContactRepository(
            contactDao = AppDatabase.getDatabase(applicationContext).contactDao(), // Setzt voraus, dass AppDatabase existiert
            ktorService = ktorService // Übergabe des KtorService
        )
    }
}
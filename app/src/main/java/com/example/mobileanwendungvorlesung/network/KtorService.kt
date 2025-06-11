// src/main/java/com/example/mobileanwendungvorlesung/network/KtorService.kt
package com.example.mobileanwendungvorlesung.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.call.body // Für .body() Aufrufe
import io.ktor.client.request.get // Für GET-Anfragen
import com.example.mobileanwendungvorlesung.data.Contact // Wenn du Contacts von der API abrufen willst

// Dies muss eine Klasse sein!
class KtorService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true // Optional: Wenn JSON mal nicht strikt ist
            })
        }
        // Weitere Konfigurationen wie Logging können hier hinzugefügt werden
        // install(Logging) { logger = Logger.DEFAULT; level = LogLevel.ALL }
    }

    // Beispiel: Methode zum Abrufen von Kontakten
    suspend fun getRandomUsersFromApi(): RandomUserResponse {
        val randomUserUrl = "https://randomuser.me/api/?results=20"
        return client.get(randomUserUrl).body()
    }

    // Füge hier weitere API-Aufrufe hinzu (POST, PUT, DELETE etc.)
}
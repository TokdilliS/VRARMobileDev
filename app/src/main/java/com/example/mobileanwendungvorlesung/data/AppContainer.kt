package com.example.mobileanwendungvorlesung.data

import android.content.Context // Dieser Import ist für die Implementierung wichtig, auch wenn er im Interface nicht direkt genutzt wird

interface AppContainer {
    val contactRepository: ContactRepository
}
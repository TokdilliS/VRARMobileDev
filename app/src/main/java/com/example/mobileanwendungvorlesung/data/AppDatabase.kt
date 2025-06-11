package com.example.mobileanwendungvorlesung.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao // Hier wird deine DAO bereitgestellt

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "contact_database" // Name deiner Datenbankdatei
                )
                    .fallbackToDestructiveMigration() // <<< Hinzufügen für einfache Migrationen (nur für die Entwicklung)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
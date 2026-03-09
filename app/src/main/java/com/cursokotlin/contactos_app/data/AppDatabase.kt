package com.cursokotlin.contactos_app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cursokotlin.contactos_app.model.Contact

/**
 * Configuración de la base de datos Room.
 * Se ha actualizado a la versión 6 tras simplificar el modelo de Contactos.
 */
@Database(entities = [Contact::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "contact_database"
                )
                // Permite recrear la base de datos si el esquema cambia (limpieza automática)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

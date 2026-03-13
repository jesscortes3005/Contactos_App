package com.cursokotlin.contactos_app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cursokotlin.contactos_app.data.model.Contact
import com.cursokotlin.contactos_app.data.local.dao.ContactDao


 //Esta es la Bodega Central de datos (Room Database).
 //Es el archivo físico en el celular donde se guardan todos los contactos.

@Database(entities = [Contact::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Aquí conectamos las instrucciones (DAO) con la bodega real
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Esta función se asegura de que solo exista UNA bodega abierta a la vez
        // para no crear un lío de archivos duplicados.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "contact_database"
                )
                    // Si actualizamos la app y cambiamos cómo se guardan los datos,
                    // esto borra lo viejo y empieza limpio para no trabarse.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.cursokotlin.contactos_app.data.local.dao

import androidx.room.*
import com.cursokotlin.contactos_app.data.model.Contact
import kotlinx.coroutines.flow.Flow

/**
 * El "DAO" es como el manual de instrucciones de la base de datos.
 * Aquí le decimos a la app CÓMO hablar con la base de datos (pedir, guardar, borrar).
 */
@Dao
interface ContactDao {
    // Dame todos los contactos ordenados por nombre de la A a la Z
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    // Dame solo los contactos que el usuario marcó con estrella
    @Query("SELECT * FROM contacts WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteContacts(): Flow<List<Contact>>

    // Busca a una persona específica usando su número de ID
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): Contact?

    // Busca si alguien ya tiene este correo electrónico registrado
    @Query("SELECT * FROM contacts WHERE email = :email LIMIT 1")
    suspend fun getContactByEmail(email: String): Contact?

    // Guarda a una persona nueva (si el ID ya existe, lo actualiza)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    // Cambia los datos de alguien que ya teníamos guardado
    @Update
    suspend fun updateContact(contact: Contact)

    // Borra a un contacto para siempre de la agenda
    @Delete
    suspend fun deleteContact(contact: Contact)
}

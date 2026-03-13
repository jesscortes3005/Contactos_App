package com.cursokotlin.contactos_app.data.repository

import com.cursokotlin.contactos_app.data.local.dao.ContactDao
import com.cursokotlin.contactos_app.data.model.Contact
import kotlinx.coroutines.flow.Flow


 //El Repositorio es como el Administrador de la Bodega.
 //Su trabajo es ser el intermediario: el resto de la app le pide cosas a él,
 //y él sabe exactamente a qué parte de la base de datos ir a buscarlas.

class ContactRepository(private val contactDao: ContactDao) {

    // Una lista viva de todos los contactos se actualiza sola si alguien cambia algo
    val allContacts: Flow<List<Contact>> = contactDao.getAllContacts()

    // Agregamos la  lista viva de solo contactos  favoritos
    val favoriteContacts: Flow<List<Contact>> = contactDao.getFavoriteContacts()

    // Pedimos los datos de una persona por su ID
    suspend fun getContactById(id: Long): Contact? {
        return contactDao.getContactById(id)
    }

    // Buscamos si un correo ya existe
    suspend fun getContactByEmail(email: String): Contact? {
        return contactDao.getContactByEmail(email)
    }

    // Mandamos la orden de guardar a alguien nuevo
    suspend fun insert(contact: Contact) {
        contactDao.insertContact(contact)
    }

    // Mandamos la orden de actualizar los datos de alguien
    suspend fun update(contact: Contact) {
        contactDao.updateContact(contact)
    }

    // Mandamos la orden de borrar a alguien de la lista
    suspend fun delete(contact: Contact) {
        contactDao.deleteContact(contact)
    }
}

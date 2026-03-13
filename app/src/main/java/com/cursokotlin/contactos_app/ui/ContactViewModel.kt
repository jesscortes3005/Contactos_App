package com.cursokotlin.contactos_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cursokotlin.contactos_app.data.model.Contact
import com.cursokotlin.contactos_app.data.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


 // Aqui el ViewModel es nuestro  Cerebro de la pantalla.
 //lo cual su trabajo es mantener toda la información lista para mostrarse y
 // para reaccionar cuando el usuario hace clic en algo.
 // Se comunica con el Repositorio para pedir o guardar datos.

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

    // Esta es la lista completa de contactos que siempre está viva
    val allContacts: StateFlow<List<Contact>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Esta lista solo contiene a tus contactos favoritos
    val favoriteContacts: StateFlow<List<Contact>> = repository.favoriteContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Aquí guardamos el contacto que el usuario seleccionó para ver o editar
    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()


     // Aqui busca a un contacto por su ID y lo guarda en "_selectedContact"

    fun fetchContactById(id: Long) {
        viewModelScope.launch {
            _selectedContact.value = withContext(Dispatchers.IO) {
                repository.getContactById(id)
            }
        }
    }


    // Aqui pedimos  los detalles de un contacto usando su ID
    suspend fun getContactById(id: Long): Contact? = withContext(Dispatchers.IO) {
        repository.getContactById(id)
    }


     //Aqui buscamos si ya existe alguien con este correo electrónico

    suspend fun getContactByEmail(email: String): Contact? = withContext(Dispatchers.IO) {
        repository.getContactByEmail(email)
    }


    //Aqui mandamos  la orden de guardar un nuevo contacto

    fun insert(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(contact)
    }


     // Actualiza los datos de un contacto que ya existía

    fun update(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(contact)
    }


     //Borra definitivamente un contacto de la lista
    fun delete(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(contact)
    }
    

    // Pone o quita la estrellita de favorito a un contacto

    fun toggleFavorite(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(contact.copy(isFavorite = !contact.isFavorite))
    }

     //Limpia el contacto seleccionado lo cual se usa al cerrar pantallas

    fun clearSelectedContact() {
        _selectedContact.value = null
    }
}


class ContactViewModelFactory(private val repository: ContactRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

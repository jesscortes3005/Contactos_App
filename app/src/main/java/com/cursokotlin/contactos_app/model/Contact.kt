package com.cursokotlin.contactos_app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa la entidad de un contacto en la base de datos local (Room).
 * He simplificado esta clase para que solo contenga los datos esenciales 
 * que realmente estamos usando en la interfaz de usuario.
 */
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Identificador único generado automáticamente
    val name: String, // Nombre del contacto
    val surname: String = "", // Apellido del contacto
    val phone: String, // Número de teléfono (10 dígitos)
    val email: String, // Correo electrónico validado
    
    // NOTA: He mantenido estos dos campos porque son funcionales para la app:
    val photoUri: String? = null, // Ruta de la imagen de perfil
    val isFavorite: Boolean = false // Estado para la pantalla de Favoritos
)

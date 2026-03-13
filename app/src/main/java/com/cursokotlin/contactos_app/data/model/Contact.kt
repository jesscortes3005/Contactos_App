package com.cursokotlin.contactos_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


 //Esta es la "Ficha del Contacto".
 //Es como una tarjeta de presentación que define qué datos guardamos de cada persona.
 //Al ponerle "@Entity", le decimos a la base de datos que cree una tabla para estas fichas.

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,             // Un número único que el celular le asigna a cada persona
    val name: String,             // El nombre
    val surname: String = "",     // El apellido
    val phone: String,            // El teléfono (esos 10 numeritos)
    val email: String,            // El correo electrónico
    val photoUri: String? = null, // La ruta de la foto en la galería lo cual si tiene
    val isFavorite: Boolean = false // ¿Tiene estrellita de favorito? (Sí o No)
)

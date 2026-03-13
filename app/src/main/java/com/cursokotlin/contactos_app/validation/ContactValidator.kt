package com.cursokotlin.contactos_app.validation

import android.util.Patterns
import com.cursokotlin.contactos_app.ui.ContactViewModel


//Este es el  apartado  de la Validación
//Es como un semáforo:
//Si es Success (Éxito), todo está verde y podemos continuar.
//Si es Errors, nos dice exactamente qué campos están en rojo y por qué.

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Errors(val fieldErrors: Map<String, String>) : ValidationResult()
}


//El Validador de Contactos es la señora amable de la app.
//Su trabajo es revisar que el usuario no haya escrito cosas raras o dejado campos vacíos.

object ContactValidator {

    // Utilizamos un Patrón oficial de Android para saber si un correo está bien escrito (ej: que tenga @ y .)
    private val EMAIL_PATTERN = Patterns.EMAIL_ADDRESS

    suspend fun validateContact(
        name: String,
        email: String,
        phone: String,
        contactId: Long?,
        viewModel: ContactViewModel
    ): ValidationResult {
        // Aquí vamos guardando los mensajitos de error que encontremos
        val errors = mutableMapOf<String, String>()

        // Revisamos el nombre
        if (name.isBlank()) {
            errors["name"] = "El nombre es obligatorio"
        }

        // Revisamos el teléfono
        if (phone.isBlank()) {
            errors["phone"] = "El teléfono es requerido"
        } else if (phone.length != 10) {

            // Queremos que los números sean de 10 dígitos exactamente
            errors["phone"] = "Debe tener exactamente 10 dígitos"
        }

        // Si escribió un correo, revisamos que tenga formato de correo de verdad
        if (email.isNotBlank() && !EMAIL_PATTERN.matcher(email).matches()) {
            errors["email"] = "El formato del email no es válido"
        }

        // Revisamos que no existan dos personas con el mismo correo en nuestra lista
        if (email.isNotBlank()) {
            val existing = viewModel.getContactByEmail(email)

            // Si encontramos a alguien con ese email y no es la misma persona que estamos editando...
            if (existing != null && (contactId == null || existing.id != contactId)) {
                errors["email"] = "Este email ya está registrado"
            }
        }

        // Si el mapa de errores está vacío, significa que todo salió bien
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Errors(errors)
        }
    }
}

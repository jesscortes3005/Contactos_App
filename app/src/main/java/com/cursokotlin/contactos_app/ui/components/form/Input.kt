package com.cursokotlin.contactos_app.ui.components.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

//Este es nuestro "Campo de Texto" personalizado.
//Es el cuadrito elegante donde el usuario escribe.
//Lo creamos así para no tener que repetir el mismo diseño (bordes, colores, errores)
//en cada parte de la aplicación.

@Composable
fun Input(
    value: String,                    // Lo que está escrito actualmente
    onValueChange: (String) -> Unit,  // Qué hacer cuando el usuario escribe una letra
    modifier: Modifier = Modifier,
    label: String,                    // El nombre del campo (ej: "Nombre" o "Correo")
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,         // ¿Este campo tiene un error?
    errorMessage: String? = null      // El mensaje de error que aparecerá abajo en rojo
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            visualTransformation = visualTransformation,
            singleLine = true,        // Que todo sea en un solo renglón
            isError = isError,
            shape = MaterialTheme.shapes.medium, // Bordes redondeados elegantes
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(64.dp),       // Altura fija para que todos los campos se vean iguales
            colors = TextFieldDefaults.colors(

                // Si hay error el borde es rojo, si no, es azul cuando lo tocas
                focusedIndicatorColor = if (isError) Color.Red else Color.Blue,
                unfocusedIndicatorColor = if (isError) Color.Red else Color(0xFFDBDBDB),
                disabledIndicatorColor = Color(0xFFEEEEEE),
                errorIndicatorColor = Color.Red,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )
        
        // Si hay un error, mostramos el mensajito en la parte inferior derecha del campo
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 32.dp, bottom = 4.dp)
            )
        }
    }
}

// Esto sirve para visualizar el diseño sin abrir la app
@Preview(showBackground = true)
@Composable
fun InputFieldPreview() {
    Input(
        value = "",
        onValueChange = {},
        label = "Correo electronico",
        isError = true,
        errorMessage = "El correo es requerido"
    )
}

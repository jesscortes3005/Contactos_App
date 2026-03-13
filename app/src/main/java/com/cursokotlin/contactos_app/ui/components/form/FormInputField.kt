package com.cursokotlin.contactos_app.ui.components.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


 //Este es otro tipo de Campo de Texto, un poco más sencillo.
 //Incluye un espacio para un icono al lado izquierdo (como una personita o un sobre).

@Composable
fun FormInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    icon: ImageVector? = null,        // Implementamos el dibujito que sale a la izquierda
    keyboardType: KeyboardType = KeyboardType.Text,
    primaryColor: Color
) {
    Row(modifier = Modifier .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        // Contenedor para el icono
        Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    // Si hay error el icono brilla en rojo
                    tint = if (error != null) Color.Red else Color(0xFF6B7280)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))

        // El campo de texto real
        Column(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedLabelColor = primaryColor
                ),
                singleLine = true
            )
            // Si hay error, el texto sale justo debajo del borde
            if (error != null) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

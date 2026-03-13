package com.cursokotlin.contactos_app.ui.components.form

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


//Este componente es el nuestro Selector Inteligente de Teléfono.
 //Básicamente, es el cuadro donde el usuario escribe su número,
 //pero incluye una banderita y el código de país (como +52 para México).
@Composable
fun SmartPhoneSelector(
    phone: String,           // El número de teléfono que se está escribiendo
    onPhoneChange: (String) -> Unit, // Qué hacer cuando el usuario escribe algo nuevo
    error: String?,          // Si hay un error (ej: faltan dígitos), aquí nos llega el mensaje
    primaryColor: Color      // El color principal de la app para que todo se vea bien
) {
    // Aquí guardamos la lista de países, su código y su emoji de bandera
    val countries = listOf(
        "MX +52" to "🇲🇽", "CO +57" to "🇨🇴", "EC +593" to "🇪🇨", "CL +56" to "🇨🇱",
        "VE +58" to "🇻🇪", "CR +506" to "🇨🇷", "ES +34" to "🇪🇸", "US +1" to "🇺🇸", "BR +55" to "🇧🇷"
    )

    // "expanded" nos dice si el menú de países está abierto o cerrado
    var expanded by remember { mutableStateOf(false) }

    // "selectedCountry" guarda cuál país eligió el usuario (por defecto México)
    var selectedCountry by remember { mutableStateOf(countries[0]) }

    // Usamos una columna para organizar el icono y los campos
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Icono de un telefonito al principio
            Icon(
                Icons.Rounded.Phone,
                contentDescription = null,
                // Si hay error se pone rojo, si no, un gris suave
                tint = if (error != null) Color.Red else Color(0xFF6B7280),
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )

            // Este es el cuadrito de la bandera y el código (+52)
            Box(modifier = Modifier.width(100.dp)) {
                Surface(
                    modifier = Modifier
                        .height(64.dp)
                        .border(
                            1.dp,
                            if (error != null) Color.Red else Color(0xFFE5E7EB),
                            MaterialTheme.shapes.medium
                        )
                        // Al hacer clic, se abre la lista de países
                        .clickable { expanded = true },
                    shape = MaterialTheme.shapes.medium,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(selectedCountry.second, fontSize = 18.sp) // La bandera
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedCountry.first.split(" ").first(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Rounded.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }

                // La lista desplegable que sale al tocar la bandera
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text("${country.second} ${country.first}") },
                            onClick = {
                                selectedCountry = country
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Aquí es donde el usuario realmente escribe los números
            // Lo envolvemos en un Box para poner el error adentro, igual que el nombre
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    placeholder = {
                        Text(
                            "Teléfono (10 dígitos)",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = if (error != null) Color.Red else Color.Blue,
                        unfocusedIndicatorColor = if (error != null) Color.Red else Color(0xFFDBDBDB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        errorContainerColor = Color.White,
                    ),
                    singleLine = true
                )

                // Muestra el error del campo dentro del cuadro de texto
                if (error != null) {
                    Text(
                        text = error,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 12.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }
}

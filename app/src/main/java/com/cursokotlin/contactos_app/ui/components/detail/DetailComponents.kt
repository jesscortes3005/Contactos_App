package com.cursokotlin.contactos_app.ui.components.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


 //Este es un Botón de Acción circular.
  //Son los botones blancos que aparecen en el detalle de nuestro contacto que son el de (Llamar, Correo).

@Composable
fun ActionButton(icon: ImageVector, label: String, primaryColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .size(54.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    spotColor = Color.Black.copy(alpha = 0.4f)
                ),
            shape = CircleShape,
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = primaryColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // El nombre de la acción (Llamar, etc) en gris
        Text(text = label, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}


 //Agregamos nuestro  botón especial para marcar como favorito.
 //Tiene una animación de salto cada vez que lo tocas para que se sienta algo  divertido.

@Composable
fun FavoriteButton(isFavorite: Boolean, primaryColor: Color, onClick: () -> Unit) {
    // Esta animación hace que la estrella crezca un poquito cuando se activa o le pica el usuario
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "starScale"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .size(54.dp)
                .scale(scale)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    spotColor = Color.Black.copy(alpha = 0.4f)
                ),
            shape = CircleShape,
            // Si es favorito se pone amarillo brillante, si no, es blanco
            color = if (isFavorite) Color(0xFFFFD700) else Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) Color.White else primaryColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Favorito",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


 //Un renglón de información (ej: Correo: juan@gmail.com).
 //Muestra un icono a la izquierda y el dato en letras negritas a la derecha.

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, primaryColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = primaryColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(18.dp))
        Column {
            Text(text = label, fontSize = 13.sp, color = Color.Gray) // El nombre del dato
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            ) // El dato real
        }
    }
}


//Este es el apartado del dialogo de borrado
//Es la ventanita  que sube desde abajo y te pregunta: "¿Seguro que quieres borrar?".
@Composable
fun ProfessionalDeleteDialog(
    contactName: String, onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)) // Fondo oscuro semitransparente
                .clickable { onDismiss() }, // Si tocas fuera, se cierra
            contentAlignment = Alignment.BottomCenter
        ) {
            var animateIn by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { animateIn = true }

            // Animación para que la tarjeta suba desde abajo suavemente
            AnimatedVisibility(
                visible = animateIn,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(),
                modifier = Modifier.clickable(enabled = false) { }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(20.dp, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .padding(bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Un pequeño adorno gris arriba (como una manija)
                        Box(
                            modifier = Modifier
                                .size(40.dp, 4.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "¿Estás seguro?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "¿Deseas eliminar este contacto permanentemente? Esta acción no se puede deshacer.",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Botón de cancelar
                            Button(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF3F4F6)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    "No, cancelar",
                                    color = Color(0xFF4B5563),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            // Botón de eliminar (Azul fuerte)
                            Button(
                                onClick = onConfirm,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3F51B5)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    "Sí, eliminar",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

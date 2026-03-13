package com.cursokotlin.contactos_app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContactPage
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


 //En esta seccion esta la Pantalla de bienvenida que vera el usuario al momento de abrirla lo cual
 //su  funcion  es mostrar el logo con una animación bonita y luego llevarnos a la lista de los contactos que ya tengamos  //

@Composable
fun PantallaDeBienvenida(onAnimationFinished: () -> Unit) {
    // Primero definimos los colores que usaremos que sera el azul
    val primaryBrand = Color(0xFF1A56DB)
    val secondaryBrand = Color(0xFF1E40AF)
    
    // Esta funcion de  interruptor nos dice cuándo deben empezar a moverse las cosas
    var startAnimation by remember { mutableStateOf(false) }
    
    // Esta animación hace que el logo aparezca suavemente de invisible a visible
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000), // Tarda 1 segundo
        label = "alpha"
    )

    // Creamos la animación  para que el logo crezca un poquito con un efecto de rebote
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, // Que rebote un poco
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Este bloque lo que hara es que  se ejecutara una sola vez en cuanto entramos a la pantalla
    LaunchedEffect(key1 = true) {
        startAnimation = true // Se Encienden  las animaciones
        delay(2500) // Esperamos 2 segundos y medio para que el usuario aprecie el logo
        onAnimationFinished() // Le avisamos al usuario que terminamos para ir a la siguiente pantalla
    }

    // Este es el  contenedor principal que ocupa toda la pantalla con un fondo azul degradado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(primaryBrand, secondaryBrand)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Ponemos el logo y los textos uno debajo del otro
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scaleAnim.value) // Le aplicamos el crecimiento
                .alpha(alphaAnim.value) // Le aplicamos la transparencia
        ) {
            // El circulito blanco translúcido donde vive el icono
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                // El icono de la app
                Icon(
                    imageVector = Icons.Rounded.ContactPage,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Nombre de la app en letras grandes y gruesas
            Text(
                text = "Mis Contactos",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            
            // Un pequeño eslogan debajo para que acompañe a la app
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tu agenda profesional",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

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

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    // Definimos los colores corporativos de nuestra aplicación para mantener la identidad visual
    val primaryBrand = Color(0xFF1A56DB) // Azul principal
    val secondaryBrand = Color(0xFF1E40AF) // Azul más oscuro para el degradado

    // Estado local para controlar el inicio de las animaciones al entrar a la pantalla
    var startAnimation by remember { mutableStateOf(false) }
    
    // Animación de Opacidad: Cambia de 0 (invisible) a 1 (visible) en 1 segundo
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )
    
    // Animación de Escala: Crea un efecto de crecimiento con un rebote elástico (Bouncy)
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // LaunchedEffect se dispara una sola vez al cargar el componente
    LaunchedEffect(key1 = true) {
        startAnimation = true // Disparamos las animaciones de escala y opacidad
        delay(2500) // Mantenemos la pantalla de portada por 2.5 segundos para una mejor experiencia
        onAnimationFinished() // Ejecutamos la navegación hacia la pantalla principal (ContactList)
    }

    // Contenedor principal que ocupa toda la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // Aplicamos un degradado vertical profesional usando nuestros colores de marca
                Brush.verticalGradient(
                    colors = listOf(primaryBrand, secondaryBrand)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Columna que agrupa el logo y los textos informativos
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scaleAnim.value) // Aplicamos la animación de escala aquí
                .alpha(alphaAnim.value) // Aplicamos la animación de transparencia aquí
        ) {
            // Contenedor circular decorativo para el icono principal
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)), // Blanco translúcido para resaltar el icono
                contentAlignment = Alignment.Center
            ) {
                // Icono representativo de "Página de Contactos"
                Icon(
                    imageVector = Icons.Rounded.ContactPage,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Título principal de la aplicación con peso extra negrita para impacto visual
            Text(
                text = "Mis Contactos",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            
            // Subtítulo o Slogan de la aplicación con opacidad reducida para jerarquía visual
            Text(
                text = "Tu agenda profesional",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

package com.cursokotlin.contactos_app.ui.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


 //Esta es la Pantalla de Carga.
 //Es ese circulito azul que da vueltas cuando la app está pensando o buscando algo.
 //Ocupa toda la pantalla y pone el circulito justo en el centro.

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Todo al centro
    ) {
        // El circulito que da vueltas infinito
        CircularProgressIndicator(color = Color(0xFF1A56DB))
    }
}

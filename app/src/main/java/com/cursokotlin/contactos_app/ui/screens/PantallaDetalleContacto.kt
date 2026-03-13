package com.cursokotlin.contactos_app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cursokotlin.contactos_app.data.model.Contact
import com.cursokotlin.contactos_app.ui.ContactViewModel
import com.cursokotlin.contactos_app.ui.components.detail.ActionButton
import com.cursokotlin.contactos_app.ui.components.detail.FavoriteButton
import com.cursokotlin.contactos_app.ui.components.detail.InfoRow
import com.cursokotlin.contactos_app.ui.components.detail.ProfessionalDeleteDialog
import kotlinx.coroutines.delay


 //En esta parte es donde esta es la Pantalla de Detalle.
 //Su función es mostrar toda la información de un contacto específico: su foto grande,
 //nombre completo y opciones para llamarlo, mandarle correo o marcarlo como favorito.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleContacto(
    viewModel: ContactViewModel,
    contactId: Long,              // El ID único del contacto que queremos ver
    onBack: () -> Unit,           // Volver a la lista
    onEdit: (Long) -> Unit        // Ir a la pantalla de edición
) {
    // Buscamos el contacto en nuestra lista usando su ID
    val contactState by viewModel.allContacts.collectAsState()
    val contact = contactState.find { it.id == contactId }
    val context = LocalContext.current
    val primaryColor = Color(0xFF3F51B5)

    // Estados para controlar el diálogo de borrar y la animación de salida
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    // Al entrar, le pedimos al ViewModel que nos traiga la info fresca de este contacto
    LaunchedEffect(contactId) {
        viewModel.fetchContactById(contactId)
    }

    // Animaciones para cuando borramos al contacto (se hace pequeño y se desvanece)
    val animatedScale by animateFloatAsState(
        targetValue = if (isDeleting) 0.7f else 1f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "scale"
    )
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(durationMillis = 500),
        label = "alpha"
    )

    // Si confirmamos que queremos borrar, esperamos a que termine la animación y volvemos atrás
    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            delay(650)
            contact?.let { viewModel.delete(it) }
            onBack()
        }
    }

    Scaffold(
        topBar = {
            // Barra superior azul con botón de volver, editar y borrar
            TopAppBar(
                title = { Text("", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { contact?.let { onEdit(it.id) } }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                    }
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            contact?.let { c ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.White)
                        .graphicsLayer(
                            scaleX = animatedScale, // Aplicamos la animación de tamaño
                            scaleY = animatedScale,
                            alpha = animatedAlpha   // Aplicamos la transparencia al borrar
                        )
                ) {
                    // Cabecera azul con la foto del contacto
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Círculo blanco que contiene la foto
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .shadow(12.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (c.photoUri != null) {
                                    AsyncImage(
                                        model = Uri.parse(c.photoUri),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // Si no hay foto, mostramos la inicial en grande
                                    val initials = c.name.firstOrNull()?.uppercase() ?: ""
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color(0xFFE8EAF6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(initials, fontSize = 56.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            // Nombre completo del contacto
                            Text(
                                text = "${c.name} ${c.surname}",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Fila de botones rápidos: Llamar, Mensaje y Favorito
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-35).dp) // Hace que los botones floten sobre la división
                    ) {
                        ActionButton(icon = Icons.Default.Phone, label = "Llamar", primaryColor = primaryColor) {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${c.phone}"))
                            context.startActivity(intent)
                        }
                        
                        Spacer(modifier = Modifier.width(24.dp))

                        ActionButton(icon = Icons.Default.Email, label = "Correo", primaryColor = primaryColor) {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${c.email}"))
                            context.startActivity(intent)
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        FavoriteButton(
                            isFavorite = c.isFavorite,
                            primaryColor = primaryColor,
                            onClick = { viewModel.toggleFavorite(c) }
                        )
                    }

                    // Tarjeta blanca con la información de contacto detallada
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .offset(y = (-15).dp)
                            .fillMaxWidth()
                            .shadow(
                                12.dp,
                                RoundedCornerShape(24.dp),
                                spotColor = Color.Black.copy(alpha = 0.1f)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            InfoRow(icon = Icons.Default.Email, label = "Correo Electrónico", value = c.email, primaryColor = primaryColor)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 18.dp), color = Color(0xFFF5F5F5))
                            InfoRow(icon = Icons.Default.Phone, label = "Teléfono", value = c.phone, primaryColor = primaryColor)
                        }
                    }
                }

                // Si pulsamos el icono de basura, sale un aviso
                if (showDeleteConfirmation) {
                    ProfessionalDeleteDialog(
                        contactName = c.name,
                        onConfirm = {
                            showDeleteConfirmation = false
                            isDeleting = true // Iniciamos la animación de despedida
                        },
                        onDismiss = { showDeleteConfirmation = false }
                    )
                }
            }
        }
    }
}

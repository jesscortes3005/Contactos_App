package com.cursokotlin.contactos_app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.cursokotlin.contactos_app.model.Contact
import com.cursokotlin.contactos_app.ui.ContactViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    viewModel: ContactViewModel,
    contactId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val contactState by viewModel.allContacts.collectAsState()
    val contact = contactState.find { it.id == contactId }
    val context = LocalContext.current
    val primaryColor = Color(0xFF3F51B5)
    
    // Estados para controlar la animación de borrado
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    LaunchedEffect(contactId) {
        viewModel.fetchContactById(contactId)
    }

    // Definimos las propiedades de la animación de salida
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

    // Lógica para ejecutar el borrado real después de que termine la animación
    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            delay(650) // Esperamos a que la animación visual termine
            contact?.let { viewModel.delete(it) }
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { contact?.let { onEdit(it.id) } }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                    }
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            contact?.let { c ->
                // Aplicamos las animaciones de escala y transparencia a todo el contenido
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.White)
                        .graphicsLayer(
                            scaleX = animatedScale,
                            scaleY = animatedScale,
                            alpha = animatedAlpha
                        )
                ) {
                    // Header section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    val initials = c.name.firstOrNull()?.uppercase() ?: ""
                                    Box(
                                        modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFFE8EAF6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(initials, fontSize = 56.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "${c.name} ${c.surname}",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Botones de acción
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-35).dp)
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

                    // Card de información
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .offset(y = (-15).dp)
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.1f)),
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

                // Diálogo de confirmación espectacular
                if (showDeleteConfirmation) {
                    ProfessionalDeleteDialog(
                        contactName = c.name,
                        onConfirm = {
                            showDeleteConfirmation = false
                            isDeleting = true // Disparamos la animación de salida
                        },
                        onDismiss = { showDeleteConfirmation = false }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfessionalDeleteDialog(
    contactName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.BottomCenter
        ) {
            var animateIn by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { animateIn = true }

            AnimatedVisibility(
                visible = animateIn,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
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
                        Box(modifier = Modifier.size(40.dp, 4.dp).clip(CircleShape).background(Color.LightGray))
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "¿Estás seguro?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "¿Deseas eliminar este contacto permanentemente? Esta acción no se puede deshacer.",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(onClick = onDismiss, modifier = Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6)), shape = RoundedCornerShape(14.dp)) {
                                Text("No, cancelar", color = Color(0xFF4B5563), fontWeight = FontWeight.SemiBold)
                            }
                            Button(onClick = onConfirm, modifier = Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)), shape = RoundedCornerShape(14.dp)) {
                                Text("Sí, eliminar", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteButton(isFavorite: Boolean, primaryColor: Color, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "starScale"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(54.dp).scale(scale).shadow(elevation = 10.dp, shape = CircleShape, spotColor = Color.Black.copy(alpha = 0.4f)),
            shape = CircleShape,
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
        Text(text = "Favorito", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, primaryColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(54.dp).shadow(elevation = 10.dp, shape = CircleShape, spotColor = Color.Black.copy(alpha = 0.4f)),
            shape = CircleShape,
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = primaryColor, modifier = Modifier.size(26.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, primaryColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = primaryColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(18.dp))
        Column {
            Text(text = label, fontSize = 13.sp, color = Color.Gray)
            Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        }
    }
}

package com.cursokotlin.contactos_app.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.cursokotlin.contactos_app.model.Contact
import com.cursokotlin.contactos_app.ui.ContactViewModel

@Composable
fun FavoritesScreen(
    viewModel: ContactViewModel,
    onContactClick: (Long) -> Unit,
    onBack: () -> Unit,
    onContactsClick: () -> Unit
) {
    val favoriteContacts by viewModel.favoriteContacts.collectAsState()
    val primaryBrand = Color(0xFF1A56DB)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp,
                modifier = Modifier.shadow(24.dp)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onContactsClick,
                    icon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                    label = { Text("Contactos") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFF9CA3AF),
                        unselectedTextColor = Color(0xFF9CA3AF)
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Star, contentDescription = null) },
                    label = { Text("Favoritos", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryBrand,
                        selectedTextColor = primaryBrand,
                        indicatorColor = primaryBrand.copy(alpha = 0.1f)
                    )
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFF))
        ) {
            item {
                FavoritesHeader(onBack = onBack, primaryColor = primaryBrand)
            }

            if (favoriteContacts.isNotEmpty()) {
                item {
                    Text(
                        text = "DESTACADOS",
                        modifier = Modifier.padding(start = 24.dp, top = 28.dp, bottom = 12.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF9CA3AF),
                        letterSpacing = 1.5.sp
                    )
                    TopFavoritesRow(favoriteContacts.take(5), onContactClick, primaryBrand)
                }

                item {
                    Text(
                        text = "LISTA DE FAVORITOS",
                        modifier = Modifier.padding(start = 24.dp, top = 28.dp, bottom = 12.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF9CA3AF),
                        letterSpacing = 1.5.sp
                    )
                }

                items(favoriteContacts, key = { it.id }) { contact ->
                    ProfessionalFavoriteItem(contact, primaryColor = primaryBrand, onClick = { onContactClick(contact.id) })
                    Spacer(modifier = Modifier.height(14.dp))
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.StarOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFFE5E7EB))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No tienes contactos favoritos aún", color = Color(0xFF9CA3AF), fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun FavoritesHeader(onBack: () -> Unit, primaryColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(primaryColor, Color(0xFF3B82F6))
                ),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color.White.copy(alpha = 0.2f),
                onClick = onBack
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBackIos,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp).padding(start = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Favoritos",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Tus contactos más importantes",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun TopFavoritesRow(contacts: List<Contact>, onContactClick: (Long) -> Unit, primaryColor: Color) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(contacts, key = { it.id }) { contact ->
            TopProfessionalItem(contact, primaryColor, onClick = { onContactClick(contact.id) })
        }
    }
}

@Composable
fun TopProfessionalItem(contact: Contact, primaryColor: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            ContactAvatarProfessional(contact, size = 72.dp, primaryColor = primaryColor)
            Surface(
                modifier = Modifier.size(24.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = contact.name.split(" ").firstOrNull() ?: "",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color(0xFF374151)
        )
    }
}

@Composable
fun ProfessionalFavoriteItem(contact: Contact, primaryColor: Color, onClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable(onClick = onClick)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = primaryColor.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatarProfessional(contact, size = 56.dp, primaryColor = primaryColor)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF111827)
                )
                Text(
                    text = contact.phone,
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFFF3F4F6),
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, "tel:${contact.phone}".toUri())
                        context.startActivity(intent)
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Call, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ContactAvatarProfessional(contact: Contact, size: androidx.compose.ui.unit.Dp, primaryColor: Color) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.8f), primaryColor)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (contact.photoUri != null) {
            AsyncImage(
                model = contact.photoUri.toUri(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            val initials = contact.name.split(" ").filter { it.isNotBlank() }.mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("")
            Text(
                text = initials,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

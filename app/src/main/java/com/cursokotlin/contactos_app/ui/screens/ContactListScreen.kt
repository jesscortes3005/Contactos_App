package com.cursokotlin.contactos_app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cursokotlin.contactos_app.model.Contact
import com.cursokotlin.contactos_app.ui.ContactViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(
    viewModel: ContactViewModel,
    onContactClick: (Long) -> Unit,
    onAddContactClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    val contacts by viewModel.allContacts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }
    val deletingIds = remember { mutableStateListOf<Long>() }

    val filteredContacts = remember(contacts, searchQuery) {
        contacts.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery)
        }.sortedBy { it.name.lowercase() }
    }

    val grouped = remember(filteredContacts) {
        filteredContacts.groupBy { it.name.firstOrNull()?.uppercaseChar() ?: '#' }
    }

    val primaryBrand = Color(0xFF1A56DB) 
    val successColor = Color(0xFF10B981)

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Mis Contactos",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF111827),
                        textAlign = TextAlign.Center
                    )
                }

                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar contactos...", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth().height(56.dp).shadow(4.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = primaryBrand) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB),
                            focusedBorderColor = primaryBrand.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        singleLine = true
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp,
                modifier = Modifier.shadow(24.dp)
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                    label = { Text("Contactos", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryBrand,
                        selectedTextColor = primaryBrand,
                        indicatorColor = primaryBrand.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onFavoritesClick,
                    icon = { Icon(Icons.Rounded.Star, contentDescription = null) },
                    label = { Text("Favoritos") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddContactClick,
                containerColor = primaryBrand,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(64.dp).offset(y = 8.dp).shadow(12.dp, CircleShape, spotColor = primaryBrand)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar", modifier = Modifier.size(32.dp))
            }
        }
    ) { padding ->
        Row(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).background(Color(0xFFF8FAFF)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                grouped.forEach { (initial, contactsInGroup) ->
                    stickyHeader(key = initial) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFF).copy(alpha = 0.9f))
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = initial.toString(),
                                fontWeight = FontWeight.Black,
                                color = primaryBrand,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                    
                    itemsIndexed(contactsInGroup, key = { _, it -> it.id }) { index, contact ->
                        val isDeleting = deletingIds.contains(contact.id)
                        
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    contactToDelete = contact
                                    false 
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color by animateColorAsState(
                                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Color(0xFFEF4444) else Color.Transparent,
                                    label = "bg"
                                )
                                Box(
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(28.dp)).background(color).padding(horizontal = 24.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(Icons.Rounded.Delete, contentDescription = null, tint = Color.White)
                                }
                            },
                            modifier = Modifier.animateItem() 
                        ) {
                            // ANIMACIÓN DE ENTRADA ESCALONADA PROFESIONAL
                            AnimatedEntranceItem(index = index) {
                                SpectacularExitItem(
                                    isDeleting = isDeleting,
                                    onAnimationFinished = { 
                                        viewModel.delete(contact)
                                        deletingIds.remove(contact.id)
                                    }
                                ) {
                                    ProfessionalContactItem(
                                        contact = contact,
                                        searchQuery = searchQuery,
                                        primaryColor = primaryBrand,
                                        highlightColor = successColor,
                                        onClick = { onContactClick(contact.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            AlphabetScroller(
                initials = remember(grouped) { grouped.keys.toList() },
                onLetterClick = { char ->
                    val index = filteredContacts.indexOfFirst { it.name.startsWith(char, ignoreCase = true) }
                    if (index != -1) {
                        scope.launch { listState.animateScrollToItem(index) }
                    }
                },
                primaryColor = primaryBrand
            )
        }

        contactToDelete?.let { contact ->
            ProfessionalDeleteDialog(
                contactName = contact.name,
                onConfirm = {
                    deletingIds.add(contact.id)
                    contactToDelete = null
                },
                onDismiss = { contactToDelete = null }
            )
        }
    }
}

@Composable
fun AnimatedEntranceItem(
    index: Int,
    content: @Composable () -> Unit
) {
    val state = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 40L) // Cascada elegante
        state.value = true
    }

    AnimatedVisibility(
        visible = state.value,
        enter = slideInVertically(
            initialOffsetY = { 40 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = tween(400)),
        exit = fadeOut()
    ) {
        content()
    }
}

@Composable
fun SpectacularExitItem(
    isDeleting: Boolean,
    onAnimationFinished: () -> Unit,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(targetState = isDeleting, label = "spectacularExit")
    
    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 500, easing = FastOutSlowInEasing) },
        label = "scale"
    ) { if (it) 0.4f else 1f }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 400) },
        label = "alpha"
    ) { if (it) 0f else 1f }

    val translationX by transition.animateFloat(
        transitionSpec = { 
            tween(durationMillis = 600, easing = CubicBezierEasing(0.36f, 0f, 0.66f, -0.56f)) 
        },
        label = "flyOut"
    ) { if (it) 1200f else 0f }

    val rotation by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) },
        label = "rotate"
    ) { if (it) 15f else 0f }

    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            delay(600)
            onAnimationFinished()
        }
    }

    Box(
        modifier = Modifier.graphicsLayer(
            scaleX = scale,
            scaleY = scale,
            alpha = alpha,
            translationX = translationX,
            rotationZ = rotation
        )
    ) {
        content()
    }
}

@Composable
fun ProfessionalContactItem(
    contact: Contact, 
    searchQuery: String, 
    primaryColor: Color, 
    highlightColor: Color, 
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val initials = remember(contact.name) {
        contact.name.split(" ").filter { it.isNotBlank() }.mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("")
    }
    val annotatedName = remember(contact.name, searchQuery) {
        buildAnnotatedString {
            val name = contact.name
            if (searchQuery.isEmpty()) append(name)
            else {
                val index = name.lowercase().indexOf(searchQuery.lowercase())
                if (index == -1) append(name)
                else {
                    append(name.substring(0, index))
                    withStyle(SpanStyle(color = highlightColor, fontWeight = FontWeight.ExtraBold)) { append(name.substring(index, index + searchQuery.length)) }
                    append(name.substring(index + searchQuery.length))
                }
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().shadow(10.dp, RoundedCornerShape(28.dp), spotColor = primaryColor.copy(alpha = 0.25f)).clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(colors = listOf(primaryColor.copy(alpha = 0.8f), primaryColor))), contentAlignment = Alignment.Center) {
                if (contact.photoUri != null) { AsyncImage(model = Uri.parse(contact.photoUri), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
                else { Text(text = initials, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            }
            Spacer(modifier = Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = annotatedName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF111827))
                Text(text = contact.phone, color = Color(0xFF6B7280), fontSize = 15.sp, modifier = Modifier.padding(top = 2.dp))
            }
            Surface(modifier = Modifier.size(42.dp), shape = CircleShape, color = Color(0xFFF3F4F6), onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))) }) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Call, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp)) }
            }
        }
    }
}

@Composable
fun AlphabetScroller(initials: List<Char>, onLetterClick: (Char) -> Unit, primaryColor: Color) {
    Column(modifier = Modifier.fillMaxHeight().padding(end = 8.dp, top = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        initials.forEach { char ->
            Text(text = char.toString(), modifier = Modifier.clickable { onLetterClick(char) }.padding(vertical = 2.dp, horizontal = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor.copy(alpha = 0.7f))
        }
    }
}

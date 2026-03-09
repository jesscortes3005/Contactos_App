package com.cursokotlin.contactos_app.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cursokotlin.contactos_app.model.Contact
import com.cursokotlin.contactos_app.ui.ContactViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFormScreen(
    viewModel: ContactViewModel,
    contactId: Long? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val primaryColor = Color(0xFF1A56DB)
    
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showContent by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    fun filterInput(input: String): String {
        return input.filter { it.isLetter() || it.isWhitespace() }.take(50)
    }

    val galleryLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                photoUri = it
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    LaunchedEffect(contactId) {
        if (contactId != null && contactId != -1L) {
            val contact = viewModel.getContactById(contactId)
            contact?.let {
                name = it.name
                surname = it.surname
                phone = it.phone
                email = it.email
                photoUri = it.photoUri?.let { uriString -> Uri.parse(uriString) }
            }
        }
        isLoading = false
        showContent = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (contactId == null) "Registrar Contacto" else "Editar Contacto", color = primaryColor, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = primaryColor) } },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val validationResult = validateFormProfessionalExtended(name, email, phone, contactId, viewModel)
                            nameError = validationResult.nameError
                            emailError = validationResult.emailError
                            phoneError = validationResult.phoneError

                            if (validationResult.isValid) {
                                val contact = Contact(id = contactId ?: 0L, name = name.trim(), surname = surname.trim(), phone = phone, email = email, photoUri = photoUri?.toString())
                                try {
                                    if (contactId == null) viewModel.insert(contact) else viewModel.update(contact)
                                    Toast.makeText(context, "Guardado con éxito", Toast.LENGTH_SHORT).show()
                                    onBack()
                                } catch (e: Exception) { Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show() }
                            }
                        }
                    }) { Icon(Icons.Rounded.Check, contentDescription = "Guardar", tint = primaryColor) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (isLoading) { FullScreenLoading() } else {
            AnimatedVisibility(visible = showContent, enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { 50 })) {
                Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color.White).verticalScroll(rememberScrollState())) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(modifier = Modifier.size(110.dp), shape = CircleShape, color = Color(0xFFF3F4F6), onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                                if (photoUri != null) { AsyncImage(model = photoUri, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop) }
                                else { Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.AddAPhoto, contentDescription = null, modifier = Modifier.size(44.dp), tint = primaryColor) } }
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormInputField(value = name, onValueChange = { name = filterInput(it); nameError = null }, label = "Nombre", error = nameError, icon = Icons.Rounded.Person, primaryColor = primaryColor)
                        FormInputField(value = surname, onValueChange = { surname = filterInput(it) }, label = "Apellidos (Opcional)", icon = null, primaryColor = primaryColor)
                        
                        SmartPhoneSelector(phone = phone, onPhoneChange = { input ->
                            val digits = input.filter { it.isDigit() }.take(10)
                            phone = digits
                            phoneError = null
                        }, error = phoneError, primaryColor = primaryColor)
                        
                        FormInputField(value = email, onValueChange = { email = it; emailError = null }, label = "Correo electrónico", error = emailError, icon = Icons.Rounded.Email, keyboardType = KeyboardType.Email, primaryColor = primaryColor)
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartPhoneSelector(phone: String, onPhoneChange: (String) -> Unit, error: String?, primaryColor: Color) {
    val countries = listOf(
        "MX +52" to "🇲🇽", "CO +57" to "🇨🇴", "EC +593" to "🇪🇨", "CL +56" to "🇨🇱",
        "VE +58" to "🇻🇪", "CR +506" to "🇨🇷", "ES +34" to "🇪🇸", "US +1" to "🇺🇸", "BR +55" to "🇧🇷"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf(countries[0]) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Phone, contentDescription = null, tint = if (error != null) Color.Red else Color(0xFF6B7280), modifier = Modifier.size(24.dp).padding(end = 8.dp))
            
            Box(modifier = Modifier.width(120.dp)) {
                Surface(
                    modifier = Modifier.height(56.dp).border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp)).clickable { expanded = true },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF9FAFB)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedCountry.second, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = selectedCountry.first.split(" ").first(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Rounded.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                    }
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text("${country.second} ${country.first}") },
                            onClick = { selectedCountry = country; expanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                placeholder = { Text("Teléfono (10 dígitos)", color = Color.LightGray, fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                isError = error != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, unfocusedBorderColor = Color(0xFFE5E7EB)),
                singleLine = true
            )
        }
        if (error != null) Text(text = error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 32.dp, top = 4.dp))
    }
}

@Composable
fun FullScreenLoading() { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF1A56DB)) } }

@Composable
fun FormInputField(value: String, onValueChange: (String) -> Unit, label: String, error: String? = null, icon: ImageVector? = null, keyboardType: KeyboardType = KeyboardType.Text, primaryColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) { if (icon != null) Icon(icon, contentDescription = null, tint = if (error != null) Color.Red else Color(0xFF6B7280)) }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth(), isError = error != null, keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, unfocusedBorderColor = Color(0xFFE5E7EB), focusedLabelColor = primaryColor), singleLine = true
            )
            if (error != null) Text(text = error, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

data class ExtendedValidationResult(val isValid: Boolean, val nameError: String? = null, val emailError: String? = null, val phoneError: String? = null)

suspend fun validateFormProfessionalExtended(name: String, email: String, phone: String, contactId: Long?, viewModel: ContactViewModel): ExtendedValidationResult {
    var nErr: String? = null; var eErr: String? = null; var pErr: String? = null; var valid = true
    if (name.isBlank()) { nErr = "El nombre es obligatorio"; valid = false }
    if (phone.length != 10) { pErr = "Debe tener exactamente 10 dígitos"; valid = false }
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    if (email.isNotBlank() && !email.matches(emailRegex)) { eErr = "Email inválido"; valid = false }
    if (valid && email.isNotBlank()) {
        val existing = viewModel.getContactByEmail(email)
        if (existing != null && (contactId == null || existing.id != contactId)) { eErr = "Email ya registrado"; valid = false }
    }
    return ExtendedValidationResult(valid, nErr, eErr, pErr)
}

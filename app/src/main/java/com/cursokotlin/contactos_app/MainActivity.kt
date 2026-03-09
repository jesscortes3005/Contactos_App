package com.cursokotlin.contactos_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cursokotlin.contactos_app.data.AppDatabase
import com.cursokotlin.contactos_app.data.ContactRepository
import com.cursokotlin.contactos_app.ui.ContactViewModel
import com.cursokotlin.contactos_app.ui.ContactViewModelFactory
import com.cursokotlin.contactos_app.ui.screens.ContactDetailScreen
import com.cursokotlin.contactos_app.ui.screens.ContactFormScreen
import com.cursokotlin.contactos_app.ui.screens.ContactListScreen
import com.cursokotlin.contactos_app.ui.screens.FavoritesScreen
import com.cursokotlin.contactos_app.ui.screens.SplashScreen
import com.cursokotlin.contactos_app.ui.theme.Contactos_appTheme

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { ContactRepository(database.contactDao()) }
    private val viewModel: ContactViewModel by viewModels {
        ContactViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Contactos_appTheme {
                ContactAppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun ContactAppNavigation(viewModel: ContactViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onAnimationFinished = {
                navController.navigate("contact_list") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        
        composable("contact_list") {
            ContactListScreen(
                viewModel = viewModel,
                onContactClick = { id -> navController.navigate("contact_detail/$id") },
                onAddContactClick = { navController.navigate("contact_form/-1") },
                onFavoritesClick = { navController.navigate("favorites") }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                viewModel = viewModel,
                onContactClick = { id -> navController.navigate("contact_detail/$id") },
                onBack = { navController.popBackStack() },
                onContactsClick = { 
                    navController.navigate("contact_list") {
                        popUpTo("contact_list") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "contact_detail/{contactId}",
            arguments = listOf(navArgument("contactId") { type = NavType.LongType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getLong("contactId") ?: return@composable
            ContactDetailScreen(
                viewModel = viewModel,
                contactId = contactId,
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate("contact_form/$id") }
            )
        }

        composable(
            route = "contact_form/{contactId}",
            arguments = listOf(navArgument("contactId") { type = NavType.LongType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getLong("contactId") ?: -1L
            ContactFormScreen(
                viewModel = viewModel,
                contactId = if (contactId == -1L) null else contactId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

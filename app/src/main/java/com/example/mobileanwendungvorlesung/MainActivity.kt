// src/main/java/com/example/mobileanwendungvorlesung/MainActivity.kt
package com.example.mobileanwendungvorlesung

import BottomNavigationBar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mobileanwendungvorlesung.ui.theme.MobileAnwendungVorlesungTheme
import com.example.mobileanwendungvorlesung.data.AppContainer
import com.example.mobileanwendungvorlesung.data.AppContainerImpl
import com.example.mobileanwendungvorlesung.Contactlist.ContactListViewModel
import com.example.mobileanwendungvorlesung.Details.ContactDetailViewModel
import com.example.mobileanwendungvorlesung.Settings.SettingsViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.camera.core.ExperimentalGetImage
import com.example.mobileanwendungvorlesung.Contactlist.ContactListScreen
import com.example.mobileanwendungvorlesung.Details.ContactDetailScreen
import com.example.mobileanwendungvorlesung.Settings.SettingsScreen
import com.example.mobileanwendungvorlesung.QRScanner.QRCodeScannerScreen

// Import für LocalContext
import androidx.compose.ui.platform.LocalContext
import android.app.Application // Diesen Import auch sicherstellen
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.mobileanwendungvorlesung.QRScanner.QRScanViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainerImpl(applicationContext)

        setContent {
            MobileAnwendungVorlesungTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContactApp(appContainer = appContainer)
                }
            }
        }
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @OptIn(ExperimentalGetImage::class, ExperimentalMaterial3Api::class)
    @Composable
    fun ContactApp(appContainer: AppContainer) {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.ContactList.route,
                modifier = Modifier.padding(paddingValues)
            ) {

                composable(Screen.ContactList.route) {
                    val contactListViewModel: ContactListViewModel = viewModel(
                        factory = ContactListViewModel.create(appContainer.contactRepository)
                    )
                    ContactListScreen(
                        contactListViewModel = contactListViewModel,
                        onContactClick = { contact ->
                            // Korrekte Navigation zur Detailansicht unter Verwendung der ID
                            navController.navigate(Screen.Detail.createRoute(contact.id))
                        }
                    )
                }

                composable(
                    route = Screen.Detail.route + "/{contactId}",
                    arguments = listOf(navArgument("contactId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val contactId = backStackEntry.arguments?.getInt("contactId")
                    if (contactId != null) {
                        val contactDetailViewModel: ContactDetailViewModel = viewModel(
                            factory = ContactDetailViewModel.create(
                                contactRepository = appContainer.contactRepository,
                                savedStateHandle = backStackEntry.savedStateHandle
                            )
                        )
                        ContactDetailScreen(
                            contactDetailViewModel = contactDetailViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    } else {
                        Log.e("Navigation", "ContactDetailScreen called without contactId")
                        navController.popBackStack() // Oder navigiere zur Kontaktliste
                    }
                }

                composable(Screen.QRScanner.route) {
                    val contactListViewModel: ContactListViewModel = viewModel(
                        factory = ContactListViewModel.create(appContainer.contactRepository)
                    )
                    val qrScanViewModel: QRScanViewModel = viewModel(
                        factory = QRScanViewModel.Factory(appContainer.contactRepository)
                    )
                    QRCodeScannerScreen(
                        onScanSuccess = { contact ->
                            navController.currentBackStackEntry?.lifecycleScope?.launch {
                                val newContactId = contactListViewModel.addContact(contact)
                                navController.navigate(Screen.Detail.createRoute(newContactId)) {
                                    popUpTo(Screen.QRScanner.route) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        },
                        onBack = { navController.popBackStack() },
                        viewModel = qrScanViewModel
                    )
                }

                composable(Screen.Settings.route) {
                    // NEUE ART, den Application-Kontext zu holen
                    val context = LocalContext.current
                    val application = context.applicationContext as Application

                    val settingsViewModel: SettingsViewModel = viewModel(
                        factory = SettingsViewModel.create(appContainer.contactRepository, application)
                    )
                    SettingsScreen(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}
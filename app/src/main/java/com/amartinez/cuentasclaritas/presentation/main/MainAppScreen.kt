package com.amartinez.cuentasclaritas.presentation.main

import android.annotation.SuppressLint
import android.util.Base64 // Added for Base64 encoding
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.amartinez.cuentasclaritas.navigation.AppScreen
import com.amartinez.cuentasclaritas.navigation.drawerItems
import com.amartinez.cuentasclaritas.presentation.scanticket.ScanTicketScreen
import com.amartinez.cuentasclaritas.presentation.settings.SettingsScreen
import com.amartinez.cuentasclaritas.presentation.ticketlist.TicketListScreen
import com.amartinez.cuentasclaritas.presentation.tickettable.TicketTableScreen
import com.amartinez.cuentasclaritas.presentation.tickettable.TicketTableViewModel
import com.amartinez.cuentasclaritas.presentation.profile.ProfileScreen
import com.amartinez.cuentasclaritas.presentation.auth.AuthViewModel
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") // Scaffold provides padding, but we might not use it directly in the NavHost
@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val authViewModel: AuthViewModel = hiltViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    if (!isAuthenticated) {
        com.amartinez.cuentasclaritas.presentation.auth.AuthScreen(
            onAuthSuccess = {},
            viewModel = authViewModel
        )
    } else {
        // App principal con Drawer y NavHost
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        var currentScreenTitle by remember { mutableStateOf(AppScreen.ScanTicket.title) }

        // Update title when navigation changes
        LaunchedEffect(currentRoute) {
            currentScreenTitle = drawerItems.find { it.route == currentRoute }?.title ?: AppScreen.ScanTicket.title
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(12.dp))
                    drawerItems.forEach { screen ->
                        NavigationDrawerItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(currentScreenTitle) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Abrir menú de navegación")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            ) {
                // The NavHost needs the padding from the Scaffold to not overlap the TopAppBar
                AppNavHost(navController = navController, modifier = Modifier.padding(it))
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.ScanTicket.route,
        modifier = modifier
    ) {
        composable(AppScreen.ScanTicket.route) {
            val viewModel = androidx.hilt.navigation.compose.hiltViewModel<com.amartinez.cuentasclaritas.presentation.scanticket.ScanTicketViewModel>()
            val recognizedText by viewModel.recognizedText.collectAsState()
            // Navegación automática cuando hay texto reconocido
            LaunchedEffect(recognizedText) {
                val text = recognizedText
                if (!text.isNullOrBlank()) {
                    val encodedText = Base64.encodeToString(text.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP)
                    navController.navigate("ticket_table/$encodedText")
                    viewModel.clearCapturedImage() // Limpia para evitar navegación repetida
                }
            }
            ScanTicketScreen(
                viewModel = viewModel,
                onTicketScanned = { viewModel.onImageCaptured(it) }
            )
        }
        composable(AppScreen.TicketList.route) {
            TicketListScreen()
        }
        composable(AppScreen.Settings.route) {
            SettingsScreen()
        }
        composable(AppScreen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(
            route = AppScreen.TicketTable.route,
            arguments = listOf(navArgument("ticketText") { type = NavType.StringType })
        ) { backStackEntry ->
            val ticketText = backStackEntry.arguments?.getString("ticketText")?.let {
                // Use Base64 decoding
                String(Base64.decode(it, Base64.URL_SAFE or Base64.NO_WRAP), StandardCharsets.UTF_8)
            } ?: ""
            val tableViewModel = androidx.hilt.navigation.compose.hiltViewModel<TicketTableViewModel>()
            val products by tableViewModel.products.collectAsState()
            val totalExtracted by tableViewModel.totalExtracted.collectAsState()
            val showSavedAlert by tableViewModel.showSavedAlert.collectAsState()
            var navigateToUserRegistration by remember { mutableStateOf<Long?>(null) }

            if (navigateToUserRegistration != null) {
                // Navegar a la pantalla de alta de usuarios pasando el ticketId
                LaunchedEffect(navigateToUserRegistration) {
                    navController.navigate("user_registration/${navigateToUserRegistration}")
                    navigateToUserRegistration = null
                }
            }

            TicketTableScreen(
                products = products,
                onProductChange = tableViewModel::updateProduct,
                totalExtracted = totalExtracted,
                onBackToScan = {
                    navController.popBackStack(AppScreen.ScanTicket.route, inclusive = false)
                },
                onAddProduct = { tableViewModel.addProduct() },
                onRemoveProduct = { tableViewModel.removeProduct(it) },
                onSaveProducts = {
                    tableViewModel.onSaveTicketAndProducts { ticketId ->
                        navigateToUserRegistration = ticketId
                    }
                },
                showSavedAlert = showSavedAlert,
                onDismissSavedAlert = { tableViewModel.dismissSavedAlert() }
            )
        }
        // Pantalla de alta de usuarios
        composable(
            route = "user_registration/{ticketId}",
            arguments = listOf(navArgument("ticketId") { type = NavType.LongType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getLong("ticketId") ?: -1L
            val userRegistrationViewModel = androidx.hilt.navigation.compose.hiltViewModel<com.amartinez.cuentasclaritas.presentation.userassignment.UserRegistrationViewModel>()
            val users by userRegistrationViewModel.users.collectAsState()
            var navigateToAssignment by remember { mutableStateOf(false) }

            if (navigateToAssignment) {
                // Navega a la pantalla de asignación de productos
                LaunchedEffect(ticketId) {
                    navController.navigate("product_assignment/$ticketId")
                }
            }

            com.amartinez.cuentasclaritas.presentation.userassignment.UserRegistrationScreen(
                users = users,
                onUserNameChange = userRegistrationViewModel::onUserNameChange,
                onAddUser = userRegistrationViewModel::onAddUser,
                onRemoveUser = userRegistrationViewModel::onRemoveUser,
                onSaveUsers = {
                    userRegistrationViewModel.saveUsers {
                        navigateToAssignment = true
                    }
                }
            )
        }
        // Pantalla de asignación de productos a usuarios
        composable(
            route = "product_assignment/{ticketId}",
            arguments = listOf(navArgument("ticketId") { type = NavType.LongType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getLong("ticketId") ?: -1L
            val assignmentViewModel = androidx.hilt.navigation.compose.hiltViewModel<com.amartinez.cuentasclaritas.presentation.userassignment.ProductAssignmentViewModel>()
            val users by assignmentViewModel.users.collectAsState()
            val products by assignmentViewModel.products.collectAsState()
            val assignments by assignmentViewModel.assignments.collectAsState()
            val saveSuccess by assignmentViewModel.saveSuccess.collectAsState()

            if (saveSuccess) {
                // Aquí podrías navegar a otra pantalla o mostrar un mensaje de éxito
                // navController.popBackStack(AppScreen.TicketList.route, false)
            }

            com.amartinez.cuentasclaritas.presentation.userassignment.ProductAssignmentScreen(
                users = users.map { it.name },
                products = products,
                assignments = users.associate { user ->
                    user.name to (assignments[user.userId] ?: emptyList())
                },
                onAssignProduct = { userName, productName ->
                    val user = users.find { it.name == userName }
                    user?.let { assignmentViewModel.onAssignProduct(it.userId, productName) }
                },
                onSaveAssignments = { assignmentViewModel.saveAssignments() },
                isAssignmentValid = assignmentViewModel.isAssignmentValid()
            )
        }
    }
}

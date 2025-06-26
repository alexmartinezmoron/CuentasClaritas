package com.amartinez.cuentasclaritas.presentation.main

import android.annotation.SuppressLint
import android.util.Base64 // Added for Base64 encoding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
// Removed unused import: com.amartinez.cuentasclaritas.presentation.tickettext.TicketTextScreen
// Removed unused import: java.net.URLEncoder
// Removed unused import: java.net.URLDecoder
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") // Scaffold provides padding, but we might not use it directly in the NavHost
@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
        composable(
            route = AppScreen.TicketTable.route,
            arguments = listOf(navArgument("ticketText") { type = NavType.StringType })
        ) { backStackEntry ->
            val ticketText = backStackEntry.arguments?.getString("ticketText")?.let {
                // Use Base64 decoding
                String(Base64.decode(it, Base64.URL_SAFE or Base64.NO_WRAP), StandardCharsets.UTF_8)
            } ?: ""
            // ViewModel de la tabla (usar Hilt para inyección)
            val tableViewModel = androidx.hilt.navigation.compose.hiltViewModel<TicketTableViewModel>()
            val products by tableViewModel.products.collectAsState()
            val totalExtracted by tableViewModel.totalExtracted.collectAsState()
            val showSavedAlert by tableViewModel.showSavedAlert.collectAsState()
            TicketTableScreen(
                products = products,
                onProductChange = tableViewModel::updateProduct,
                totalExtracted = totalExtracted,
                onBackToScan = {
                    navController.popBackStack(AppScreen.ScanTicket.route, inclusive = false)
                },
                onAddProduct = { tableViewModel.addProduct() },
                onRemoveProduct = { tableViewModel.removeProduct(it) },
                onSaveProducts = { tableViewModel.onSaveTicketAndProducts() },
                showSavedAlert = showSavedAlert,
                onDismissSavedAlert = { tableViewModel.dismissSavedAlert() }
            )
        }
    }
}

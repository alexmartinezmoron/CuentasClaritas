package com.amartinez.cuentasclaritas.presentation.main

import android.annotation.SuppressLint
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
import com.amartinez.cuentasclaritas.navigation.AppScreen
import com.amartinez.cuentasclaritas.navigation.drawerItems
import com.amartinez.cuentasclaritas.presentation.scanticket.ScanTicketScreen
import com.amartinez.cuentasclaritas.presentation.settings.SettingsScreen
import com.amartinez.cuentasclaritas.presentation.ticketlist.TicketListScreen
import kotlinx.coroutines.launch

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
            ScanTicketScreen(
                onTicketScanned = {
                    // TODO: Navigate to a ticket detail screen or process the bitmap
                    // For now, it could navigate to the list, for example:
                    // navController.navigate(AppScreen.TicketList.route)
                }
            )
        }
        composable(AppScreen.TicketList.route) {
            TicketListScreen()
        }
        composable(AppScreen.Settings.route) {
            SettingsScreen()
        }
    }
}

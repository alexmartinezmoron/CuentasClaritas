package com.amartinez.cuentasclaritas.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing the different screens in the application.
 *
 * @property route The navigation route string for the screen.
 * @property title The title resource ID for the screen (e.g., for TopAppBar).
 * @property icon The icon for the screen (e.g., for NavigationDrawer).
 */
sealed class AppScreen(
    val route: String,
    val title: String, // Using String directly for simplicity, can be @StringRes Int
    val icon: ImageVector? = null
) {
    object ScanTicket : AppScreen(
        route = "scan_ticket",
        title = "Escanear Ticket",
        icon = Icons.Filled.PhotoCamera
    )

    object TicketList : AppScreen(
        route = "ticket_list",
        title = "Mis Tickets",
        icon = Icons.Filled.List
    )

    object Settings : AppScreen(
        route = "settings",
        title = "Ajustes",
        icon = Icons.Filled.Settings
    )

    object TicketText : AppScreen(
        route = "ticket_text/{ticketText}",
        title = "Texto del Ticket",
        icon = null
    )
}

/**
 * List of items to be displayed in the Navigation Drawer.
 */
val drawerItems = listOf(
    AppScreen.ScanTicket,
    AppScreen.TicketList,
    AppScreen.Settings
    // No agregamos TicketText aquí porque no es parte del drawer
)

package com.amartinez.cuentasclaritas.presentation.tickettable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.amartinez.cuentasclaritas.presentation.tickettable.model.TicketProduct
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.util.Locale
import com.amartinez.cuentasclaritas.presentation.ui.common.SimpleAlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.amartinez.cuentasclaritas.R

@Composable
fun TicketTableScreen(
    products: List<TicketProduct>,
    onProductChange: (Int, TicketProduct) -> Unit,
    totalExtracted: Double?,
    onBackToScan: () -> Unit,
    onAddProduct: () -> Unit,
    onRemoveProduct: (Int) -> Unit,
    onSaveProducts: () -> Unit,
    showSavedAlert: Boolean,
    onDismissSavedAlert: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Mostrar Snackbar cuando showSavedAlert sea true
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(showSavedAlert) {
        if (showSavedAlert) {
            snackbarMessage = R.string.ticket_table_saved.toString()
            onDismissSavedAlert()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(id = R.string.ticket_table_title), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Encabezados de la tabla
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.ticket_table_add_product), modifier = Modifier.width(60.dp), style = MaterialTheme.typography.labelMedium)
                Text(stringResource(id = R.string.ticket_table_product), modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text(stringResource(id = R.string.ticket_table_remove_product), modifier = Modifier.width(80.dp), style = MaterialTheme.typography.labelMedium)
            }

            Spacer(modifier = Modifier.height(4.dp))

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
            ) {
                itemsIndexed(products) { index, product ->
                    ProductRow(
                        product = product,
                        onProductChange = { updated -> onProductChange(index, updated) },
                        onRemove = { onRemoveProduct(index) }
                    )
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = onAddProduct) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val totalCalculated = products.sumOf { it.quantity * it.unitPrice }

            Text(
                "Total productos: %.2f €".format(totalCalculated),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Total extraído del ticket: ${totalExtracted?.let { String.format(Locale.getDefault(), "%.2f", it) } ?: "-"} €",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onSaveProducts, // Este callback ahora debe llamar a onSaveTicketAndProducts
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Guardar")
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onBackToScan,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Volver a escanear")
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(id = R.string.ticket_table_back))
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter) // Cambiado a la parte superior
        )

        // Mostrar el Snackbar si hay un mensaje
        if (snackbarMessage != null) {
            val messageId = snackbarMessage!!.toInt()
            val message = stringResource(id = messageId)
            LaunchedEffect(messageId) {
                snackbarHostState.showSnackbar(message)
                snackbarMessage = null
            }
        }
    }
}

@Composable
fun ProductRow(
    product: TicketProduct,
    onProductChange: (TicketProduct) -> Unit,
    onRemove: (() -> Unit)? = null // NUEVO
) {
    var quantity by remember { mutableStateOf(product.quantity.toString()) }
    var name by remember { mutableStateOf(product.name) }
    var unitPrice by remember { mutableStateOf(product.unitPrice.toString()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = quantity,
            onValueChange = {
                // Solo permitir números
                if (it.all { c -> c.isDigit() }) {
                    quantity = it
                    onProductChange(product.copy(quantity = it.toIntOrNull() ?: 0))
                }
            },
            modifier = Modifier.width(60.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(4.dp))
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                onProductChange(product.copy(name = it))
            },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(4.dp))
        OutlinedTextField(
            value = unitPrice,
            onValueChange = {
                // Solo permitir números y punto/coma
                if (it.all { c -> c.isDigit() || c == '.' || c == ',' }) {
                    unitPrice = it
                    onProductChange(product.copy(unitPrice = it.replace(",", ".").toDoubleOrNull() ?: 0.0))
                }
            },
            modifier = Modifier.width(80.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        if (onRemove != null) {
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar producto")
            }
        }
    }
}

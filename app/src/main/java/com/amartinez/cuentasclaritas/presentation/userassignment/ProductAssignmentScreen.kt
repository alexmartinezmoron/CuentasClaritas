package com.amartinez.cuentasclaritas.presentation.userassignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.amartinez.cuentasclaritas.R
import androidx.compose.ui.unit.dp

@Composable
fun ProductAssignmentScreen(
    users: List<String>,
    products: List<String>,
    assignments: Map<String, List<String>>, // usuario -> productos asignados
    onAssignProduct: (String, String) -> Unit,
    onSaveAssignments: () -> Unit,
    isAssignmentValid: Boolean
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(id = R.string.product_assignment_title), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(user, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        products.forEach { product ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = assignments[user]?.contains(product) == true,
                                    onCheckedChange = { checked ->
                                        onAssignProduct(user, product)
                                    }
                                )
                                Text(product)
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onSaveAssignments,
            modifier = Modifier.fillMaxWidth(),
            enabled = isAssignmentValid
        ) {
            Text(stringResource(id = R.string.product_assignment_save))
        }
        if (!isAssignmentValid) {
            Text(
                stringResource(id = R.string.product_assignment_select_user),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

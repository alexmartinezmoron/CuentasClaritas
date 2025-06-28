package com.amartinez.cuentasclaritas.presentation.userassignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.amartinez.cuentasclaritas.R
import androidx.compose.ui.unit.dp

@Composable
fun UserRegistrationScreen(
    users: List<String>,
    onUserNameChange: (Int, String) -> Unit,
    onAddUser: () -> Unit,
    onRemoveUser: (Int) -> Unit,
    onSaveUsers: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(id = R.string.user_registration_title), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(users.size) { index ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = users[index],
                        onValueChange = { onUserNameChange(index, it) },
                        label = { Text(stringResource(id = R.string.user_registration_name_hint)) },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onRemoveUser(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.user_registration_remove))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = onAddUser) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.user_registration_add))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(id = R.string.user_registration_add))
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onSaveUsers,
            modifier = Modifier.fillMaxWidth(),
            enabled = users.all { it.isNotBlank() && users.size > 0 }
        ) {
            Text(stringResource(id = R.string.user_registration_save))
        }
    }
}

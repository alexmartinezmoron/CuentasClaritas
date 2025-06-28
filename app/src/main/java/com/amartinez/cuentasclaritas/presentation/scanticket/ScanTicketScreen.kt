package com.amartinez.cuentasclaritas.presentation.scanticket

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.amartinez.cuentasclaritas.R

// ScanTicketScreen
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanTicketScreen(
    viewModel: ScanTicketViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onTicketScanned: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    // Permisos para cámara y galería (almacenamiento)
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val galleryPermissionState = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    )

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador para seleccionar imagen de galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val bitmap = uriToBitmap(context, it)
                bitmap?.let(onTicketScanned)
            }
        }
    )

    // Lanzador para tomar foto con la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                cameraImageUri?.let { uri ->
                    val bitmap = uriToBitmap(context, uri)
                    bitmap?.let(onTicketScanned)
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            if (galleryPermissionState.allPermissionsGranted) {
                galleryLauncher.launch("image/*")
            } else {
                galleryPermissionState.launchMultiplePermissionRequest()
            }
        }) {
            Text(stringResource(id = R.string.scan_ticket_gallery))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            if (cameraPermissionState.status == PermissionStatus.Granted) {
                val uri = createImageUri(context)
                if (uri != null) {
                    cameraImageUri = uri
                    cameraLauncher.launch(uri)
                }
                // Si uri es null, no se lanza la cámara
            } else {
                cameraPermissionState.launchPermissionRequest()
            }
        }) {
            Text(stringResource(id = R.string.scan_ticket_camera))
        }
    }
}

private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val resolver = context.contentResolver
        val inputStream = resolver.openInputStream(uri)
        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        bitmap
    } catch (e: Exception) {
        null
    }
}

private fun createImageUri(context: Context): Uri? {
    val contentResolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "ticket_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}

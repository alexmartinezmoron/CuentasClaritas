package com.amartinez.cuentasclaritas.presentation.scanticket

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
// import com.google.accompanist.permissions.shouldShowRationale // No longer needed directly
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanTicketScreen(
    viewModel: ScanTicketViewModel = hiltViewModel(),
    onTicketScanned: (Bitmap) -> Unit // Callback to pass the captured image
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    LaunchedEffect(key1 = cameraPermissionState.status) {
        if (cameraPermissionState.status != PermissionStatus.Granted && !((cameraPermissionState.status as? PermissionStatus.Denied)?.shouldShowRationale == true)) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        floatingActionButton = {
            if (cameraPermissionState.status == PermissionStatus.Granted) {
                FloatingActionButton(
                    onClick = {
                        val localImageCapture = imageCapture
                        if (localImageCapture != null) {
                            captureImage(localImageCapture, ContextCompat.getMainExecutor(context)) {
                                viewModel.onImageCaptured(it)
                                onTicketScanned(it)
                            }
                        } else {
                            Log.e("ScanTicketScreen", "ImageCapture is null, cannot take photo.")
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Filled.Camera, contentDescription = "Take photo")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cameraPermissionState.status == PermissionStatus.Granted) {
                AndroidView(
                    factory = { ctx ->
                        val pv = PreviewView(ctx).apply {
                            this.scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                        setupCamera(pv, context, lifecycleOwner) { ic ->
                            imageCapture = ic
                        }
                        pv
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Permission is not granted, show appropriate UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val textToShow = if ((cameraPermissionState.status as? PermissionStatus.Denied)?.shouldShowRationale == true) {
                        "The camera is essential for scanning tickets. Please grant the permission to continue."
                    } else {
                        "Camera permission is required to scan tickets. Please grant it to use this feature."
                    }
                    Text(text = textToShow, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}

private fun setupCamera(
    previewView: PreviewView,
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageCaptureInstance = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCaptureInstance
            )
            onImageCaptureReady(imageCaptureInstance)
        } catch (exc: Exception) {
            Log.e("ScanTicketScreen", "Use case binding failed", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}

private fun captureImage(
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Bitmap) -> Unit
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                val rotationDegrees = image.imageInfo.rotationDegrees
                val bitmap = image.toBitmap()
                image.close()

                val rotatedBitmap = if (rotationDegrees != 0) {
                    val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                } else {
                    bitmap
                }
                onImageCaptured(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("ScanTicketScreen", "Photo capture failed: ${exception.message}", exception)
            }
        }
    )
}

// Extension function to convert ImageProxy to Bitmap
fun androidx.camera.core.ImageProxy.toBitmap(): Bitmap {
    val planeProxy = planes[0]
    val buffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

// Context extension for main executor
val Context.mainExecutor: Executor
    get() = ContextCompat.getMainExecutor(this)

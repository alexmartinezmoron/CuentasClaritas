package com.amartinez.cuentasclaritas.presentation.scanticket

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanTicketViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    private val _capturedImage = MutableStateFlow<Bitmap?>(null)
    val capturedImage: StateFlow<Bitmap?> = _capturedImage

    private val _recognizedText = MutableStateFlow<String?>(null)
    val recognizedText: StateFlow<String?> = _recognizedText

    fun onImageCaptured(bitmap: Bitmap) {
        _capturedImage.value = bitmap
        recognizeTextFromBitmap(bitmap)
    }

    private fun recognizeTextFromBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        _recognizedText.value = visionText.text
                    }
                    .addOnFailureListener { e ->
                        _recognizedText.value = "Error al reconocer texto: ${e.localizedMessage}"
                    }
            } catch (e: Exception) {
                _recognizedText.value = "Error al procesar imagen: ${e.localizedMessage}"
            }
        }
    }

    fun onImageCaptured(imageUri: Uri) {
        viewModelScope.launch {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    appContext.contentResolver,
                    imageUri
                )
                _capturedImage.value = bitmap
                recognizeTextFromBitmap(bitmap)
            } catch (e: Exception) {
                val msg = "ScanTicketViewModel.onImageCaptured: Error procesando imagen: ${e.localizedMessage}"
                FirebaseCrashlytics.getInstance().log(msg)
                FirebaseCrashlytics.getInstance().recordException(e)
                _recognizedText.value = null
            }
        }
    }

    fun clearCapturedImage() {
        _capturedImage.value = null
    }
}

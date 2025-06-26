package com.amartinez.cuentasclaritas.presentation.scanticket

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanTicketViewModel @Inject constructor() : ViewModel() {

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

    fun clearCapturedImage() {
        _capturedImage.value = null
    }
}

package com.amartinez.cuentasclaritas.presentation.scanticket

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ScanTicketViewModel @Inject constructor() : ViewModel() {

    private val _capturedImage = MutableStateFlow<Bitmap?>(null)
    val capturedImage: StateFlow<Bitmap?> = _capturedImage

    fun onImageCaptured(bitmap: Bitmap) {
        _capturedImage.value = bitmap
        // TODO: Send the bitmap for OCR processing
    }

    fun clearCapturedImage() {
        _capturedImage.value = null
    }
}

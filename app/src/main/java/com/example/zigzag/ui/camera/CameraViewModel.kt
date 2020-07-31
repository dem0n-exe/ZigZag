package com.example.zigzag.ui.camera

import androidx.lifecycle.ViewModel
import com.daasuu.gpuv.egl.filter.GlFilter

class CameraViewModel : ViewModel() {
    var seekBarProgress = 50
    var filter: GlFilter = GlFilter()
}
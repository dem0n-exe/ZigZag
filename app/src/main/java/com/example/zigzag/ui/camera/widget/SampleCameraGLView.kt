package com.example.zigzag.ui.camera.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class SampleCameraGLView(context: Context?, attrs: AttributeSet?) :
    GLSurfaceView(context, attrs), View.OnTouchListener {

    init {
        setOnTouchListener(this)
    }

    constructor(context: Context?) : this(context, null)

    private lateinit var touchListener: TouchListener

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val actionMasked = event.actionMasked
        if (actionMasked != MotionEvent.ACTION_DOWN) {
            return false
        }
        touchListener.onTouch(event, v.width, v.height)
        return false
    }

    interface TouchListener {
        fun onTouch(event: MotionEvent, width: Int, height: Int)
    }

    fun setTouchListener(touchListener: TouchListener) {
        this.touchListener = touchListener
    }
}
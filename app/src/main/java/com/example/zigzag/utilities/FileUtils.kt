package com.example.zigzag.utilities

import android.content.Context
import com.example.zigzag.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    fun getOutputDirectory(context: Context): String {
        val appContext = context.applicationContext
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            "$mediaDir/ZigZag-${getDate()}.mp4" else "${appContext.filesDir}/ZigZag-${getDate()}.mp4"
    }

    private fun getDate() = SimpleDateFormat("yyyyMM_dd-HHmmss", Locale.US).format(Date())
}
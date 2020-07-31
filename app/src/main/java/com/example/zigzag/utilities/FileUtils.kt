package com.example.zigzag.utilities

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
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

    fun addVideo(context: Context, filePath: String) {
        val values = ContentValues(2)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put("_data", filePath)
        context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
    }
}
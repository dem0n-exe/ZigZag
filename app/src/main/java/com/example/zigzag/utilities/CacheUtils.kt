package com.example.zigzag.utilities

import android.content.Context
import java.io.File

object CacheUtils {
    fun getVideoCacheDir(context: Context): File? {
        return File(context.externalCacheDir, "video-cache")
    }
}
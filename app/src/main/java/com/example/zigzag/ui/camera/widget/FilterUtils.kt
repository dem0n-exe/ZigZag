package com.example.zigzag.ui.camera.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.daasuu.gpuv.egl.filter.*
import com.example.zigzag.R
import java.io.IOException
import java.io.InputStream

object FilterUtils {

    fun createFilterList(): List<FilterType> {
        return FilterType.values().toList()
    }

    fun createGlFilter(filterType: FilterType, context: Context): GlFilter {
        return when (filterType) {
            FilterType.DEFAULT -> GlFilter()
            FilterType.BILATERAL_BLUR -> GlBilateralFilter()
            FilterType.BOX_BLUR -> GlBoxBlurFilter()
            FilterType.BRIGHTNESS -> {
                val glBrightnessFilter = GlBrightnessFilter()
                glBrightnessFilter.setBrightness(0.2f)
                glBrightnessFilter
            }
            FilterType.BULGE_DISTORTION -> GlBulgeDistortionFilter()
            FilterType.CGA_COLORSPACE -> GlCGAColorspaceFilter()
            FilterType.CONTRAST -> {
                val glContrastFilter = GlContrastFilter()
                glContrastFilter.setContrast(2.5f)
                glContrastFilter
            }
            FilterType.CROSSHATCH -> GlCrosshatchFilter()
            FilterType.EXPOSURE -> GlExposureFilter()
            FilterType.FILTER_GROUP_SAMPLE -> GlFilterGroup(GlSepiaFilter(), GlVignetteFilter())
            FilterType.GAMMA -> {
                val glGammaFilter = GlGammaFilter()
                glGammaFilter.setGamma(2f)
                glGammaFilter
            }
            FilterType.GAUSSIAN_FILTER -> GlGaussianBlurFilter()
            FilterType.GRAY_SCALE -> GlGrayScaleFilter()
            FilterType.HALFTONE -> GlHalftoneFilter()
            FilterType.HAZE -> {
                val glHazeFilter = GlHazeFilter()
                glHazeFilter.slope = -0.5f
                glHazeFilter
            }
            FilterType.HIGHLIGHT_SHADOW -> GlHighlightShadowFilter()
            FilterType.HUE -> GlHueFilter()
            FilterType.INVERT -> GlInvertFilter()
            FilterType.LOOK_UP_TABLE_SAMPLE -> {
                val bitmap =
                    BitmapFactory.decodeResource(context.resources, R.drawable.sample)
                GlLookUpTableFilter(bitmap)
            }
            FilterType.LUMINANCE -> GlLuminanceFilter()
            FilterType.LUMINANCE_THRESHOLD -> GlLuminanceThresholdFilter()
            FilterType.MONOCHROME -> GlMonochromeFilter()
            FilterType.OPACITY -> GlOpacityFilter()
            FilterType.PIXELATION -> GlPixelationFilter()
            FilterType.POSTERIZE -> GlPosterizeFilter()
            FilterType.RGB -> {
                val glRGBFilter = GlRGBFilter()
                glRGBFilter.setRed(0f)
                glRGBFilter
            }
            FilterType.SATURATION -> GlSaturationFilter()
            FilterType.SEPIA -> GlSepiaFilter()
            FilterType.SHARP -> {
                val glSharpenFilter = GlSharpenFilter()
                glSharpenFilter.sharpness = 4f
                glSharpenFilter
            }
            FilterType.SOLARIZE -> GlSolarizeFilter()
            FilterType.SPHERE_REFRACTION -> GlSphereRefractionFilter()
            FilterType.SWIRL -> GlSwirlFilter()
            FilterType.TONE_CURVE_SAMPLE -> {
                try {
                    val inputStream: InputStream =
                        context.assets.open("acv/tone_cuver_sample.acv")
                    return GlToneCurveFilter(inputStream)
                } catch (e: IOException) {
                    Log.e("FilterType", "Error")
                }
                GlFilter()
            }
            FilterType.TONE -> GlToneFilter()
            FilterType.VIBRANCE -> {
                val glVibranceFilter = GlVibranceFilter()
                glVibranceFilter.setVibrance(3f)
                glVibranceFilter
            }
            FilterType.VIGNETTE -> GlVignetteFilter()
            FilterType.WATERMARK -> GlWatermarkFilter(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.sample
                ), GlWatermarkFilter.Position.RIGHT_BOTTOM
            )
            FilterType.WEAK_PIXEL -> GlWeakPixelInclusionFilter()
            FilterType.WHITE_BALANCE -> {
                val glWhiteBalanceFilter = GlWhiteBalanceFilter()
                glWhiteBalanceFilter.setTemperature(2400f)
                glWhiteBalanceFilter.setTint(2f)
                glWhiteBalanceFilter
            }
            FilterType.ZOOM_BLUR -> GlZoomBlurFilter()
            else -> GlFilter()
        }
    }

    fun createFilterAdjuster(filterType: FilterType?): FilterAdjuster? {
        return when (filterType) {
            FilterType.BILATERAL_BLUR -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlBilateralFilter).blurSize = range(percentage, 0.0f, 1.0f)
                }
            }
            FilterType.BOX_BLUR -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlBoxBlurFilter).blurSize = range(percentage, 0.0f, 1.0f)
                }
            }
            FilterType.BRIGHTNESS -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlBrightnessFilter).setBrightness(range(percentage, -1.0f, 1.0f))
                }
            }
            FilterType.CONTRAST -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlContrastFilter).setContrast(range(percentage, 0.0f, 2.0f))
                }
            }
            FilterType.CROSSHATCH -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlCrosshatchFilter).setCrossHatchSpacing(
                        range(
                            percentage,
                            0.0f,
                            0.06f
                        )
                    )
                    filter.setLineWidth(range(percentage, 0.0f, 0.006f))
                }
            }
            FilterType.EXPOSURE -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlExposureFilter).setExposure(range(percentage, -10.0f, 10.0f))
                }
            }
            FilterType.GAMMA -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlGammaFilter).setGamma(range(percentage, 0.0f, 3.0f))
                }
            }
            FilterType.HAZE -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlHazeFilter).distance = range(percentage, -0.3f, 0.3f)
                    filter.slope = range(percentage, -0.3f, 0.3f)
                }
            }
            FilterType.HIGHLIGHT_SHADOW -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlHighlightShadowFilter).setShadows(range(percentage, 0.0f, 1.0f))
                    filter.setHighlights(range(percentage, 0.0f, 1.0f))
                }
            }
            FilterType.HUE -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlHueFilter).setHue(range(percentage, 0.0f, 360.0f))
                }
            }
            FilterType.LUMINANCE_THRESHOLD -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlLuminanceThresholdFilter).setThreshold(
                        range(
                            percentage,
                            0.0f,
                            1.0f
                        )
                    )
                }
            }
            FilterType.MONOCHROME -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlMonochromeFilter).intensity = range(percentage, 0.0f, 1.0f)
                }
            }
            FilterType.OPACITY -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlOpacityFilter).setOpacity(range(percentage, 0.0f, 1.0f))
                }
            }
            FilterType.PIXELATION -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlPixelationFilter).setPixel(range(percentage, 1.0f, 100.0f))
                }
            }
            FilterType.POSTERIZE -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    // In theory to 256, but only first 50 are interesting
                    (filter as GlPosterizeFilter).setColorLevels(range(percentage, 1f, 50f).toInt())
                }
            }
            FilterType.RGB -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlRGBFilter).setRed(range(percentage, 0.0f, 1.0f))
                }
            }
            FilterType.SATURATION -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlSaturationFilter).setSaturation(range(percentage, 0.0f, 2.0f))
                }
            }
            FilterType.SHARP -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlSharpenFilter).sharpness = range(percentage, -4.0f, 4.0f)
                }
            }
            FilterType.SOLARIZE -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlSolarizeFilter).setThreshold(range(percentage, 0.0f, 1.0f))
                }
            }
            FilterType.SWIRL -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlSwirlFilter).setAngle(range(percentage, 0.0f, 2.0f))
                }
            }
            FilterType.VIBRANCE -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlVibranceFilter).setVibrance(range(percentage, -1.2f, 1.2f))
                }
            }
            FilterType.VIGNETTE -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlVignetteFilter).vignetteStart = range(percentage, 0.0f, 1.0f)
                }
            }
            FilterType.WHITE_BALANCE -> object : FilterAdjuster {
                override fun adjust(filter: GlFilter, percentage: Int) {
                    (filter as GlWhiteBalanceFilter).setTemperature(
                        range(
                            percentage,
                            2000.0f,
                            8000.0f
                        )
                    )
                }
            }
            else -> null
        }
    }

    private fun range(percentage: Int, start: Float, end: Float): Float {
        return (end - start) * percentage / 100.0f + start
    }

    interface FilterAdjuster {
        fun adjust(filter: GlFilter, percentage: Int)
    }
}
package com.example.zigzag.ui.camera

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daasuu.gpuv.camerarecorder.CameraRecordListener
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorder
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorderBuilder
import com.daasuu.gpuv.camerarecorder.LensFacing
import com.example.zigzag.R
import com.example.zigzag.ui.camera.widget.FilterAdapter
import com.example.zigzag.ui.camera.widget.FilterUtils
import com.example.zigzag.ui.camera.widget.SampleCameraGLView
import com.example.zigzag.utilities.FileUtils


class CameraFragment : Fragment() {
    companion object {
        private const val videoWidth = 720
        private const val videoHeight = 1280
    }

    private val cameraViewModel: CameraViewModel by viewModels()

    private lateinit var frameLayout: FrameLayout
    private lateinit var flashButton: ToggleButton
    private lateinit var switchCameraButton: ToggleButton
    private lateinit var shutterButton: ToggleButton
    private lateinit var switchFilterButton: ToggleButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var filterSeekBar: SeekBar
    private lateinit var recordProgress: ProgressBar

    private lateinit var glSurfaceView: SampleCameraGLView
    private lateinit var gpuCameraRecorder: GPUCameraRecorder
    private lateinit var countDownTimer: CountDownTimer

    private var switchCamera = false
    private lateinit var _activity: FragmentActivity
    private lateinit var filePath: String
    private var filterAdjuster: FilterUtils.FilterAdjuster? = null
    private var lensFacing = LensFacing.FRONT

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_camera, container, false)
        frameLayout = root.findViewById(R.id.frame_layout)
        flashButton = root.findViewById(R.id.switch_flash)
        switchCameraButton = root.findViewById(R.id.switch_camera)
        shutterButton = root.findViewById(R.id.switch_shutter)
        switchFilterButton = root.findViewById(R.id.switch_filters)
        recyclerView = root.findViewById(R.id.list_filter)
        filterSeekBar = root.findViewById(R.id.seek_bar_filter)
        recordProgress = root.findViewById(R.id.progress)


        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        _activity = requireActivity()
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_camera_to_permission)
        } else {
            startCamera()
            setViews()
            restoreState()
        }
    }

    override fun onPause() {
        super.onPause()
        saveState()
        stopCamera()
    }

    private fun setViews() {
        switchCameraButton.setOnCheckedChangeListener { _, _ ->
            stopCamera()
            flashButton.isChecked = false
            lensFacing = if (lensFacing == LensFacing.FRONT) {
                flashButton.visibility = View.VISIBLE
                LensFacing.BACK
            } else {
                flashButton.visibility = View.INVISIBLE
                LensFacing.FRONT
            }
            switchCamera = true
        }

        flashButton.setOnCheckedChangeListener { _, _ ->
            if (gpuCameraRecorder.isFlashSupport) {
                gpuCameraRecorder.switchFlashMode()
                gpuCameraRecorder.changeAutoFocus()
            }
        }

        setTimer()
        shutterButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                filePath = FileUtils.getOutputDirectory(requireContext())
                gpuCameraRecorder.start(filePath)
                recordProgress.visibility = View.VISIBLE
                countDownTimer.start()
            } else {
                if (gpuCameraRecorder.isStarted) {
                    gpuCameraRecorder.stop()
                    recordProgress.visibility = View.GONE
                    countDownTimer.cancel()
                    recordProgress.progress = 15
                }
            }
        }

        switchFilterButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setFilters()
                recyclerView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.GONE
            }
        }

        filterSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (filterAdjuster != null) {
                    filterAdjuster!!.adjust(cameraViewModel.filter, progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setTimer() {
        val time = 15000L
        recordProgress.max = 15
        countDownTimer = object : CountDownTimer(time, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                recordProgress.progress = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                if (gpuCameraRecorder.isStarted) {
                    gpuCameraRecorder.stop()
                    shutterButton.isChecked = false
                }
            }
        }
    }

    private fun saveState() {
        cameraViewModel.seekBarProgress = filterSeekBar.progress
    }

    private fun restoreState() {
        shutterButton.isChecked = false
        flashButton.isChecked = false
        if (filterSeekBar.isVisible) filterSeekBar.progress = cameraViewModel.seekBarProgress
    }

    private fun setFilters() {
        val filters = FilterUtils.createFilterList()
        val adapter = FilterAdapter()
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        adapter.onItemClick = { filterType ->
            cameraViewModel.filter =
                FilterUtils.createGlFilter(filterType, requireContext().applicationContext)
            filterAdjuster = FilterUtils.createFilterAdjuster(filterType)
            if (filterAdjuster != null) {
                filterSeekBar.visibility = View.VISIBLE
                filterSeekBar.progress = cameraViewModel.seekBarProgress
            } else {
                filterSeekBar.visibility = View.GONE
            }
            gpuCameraRecorder.setFilter(cameraViewModel.filter)
        }
        adapter.setFiltersList(filters)
    }

    private fun startCamera() {
        setCameraView()
        gpuCameraRecorder = GPUCameraRecorderBuilder(activity, glSurfaceView)
            .cameraRecordListener(object : CameraRecordListener {
                override fun onVideoFileReady() {
                    _activity.runOnUiThread {
                        Toast.makeText(requireContext(), "Video Recorded", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCameraThreadFinish() {
                    if (switchCamera) {
                        _activity.runOnUiThread {
                            startCamera()
                        }
                    }
                    switchCamera = false
                }

                override fun onGetFlashSupport(flashSupport: Boolean) {
                    _activity.runOnUiThread {
                        flashButton.isEnabled = flashSupport
                    }
                }

                override fun onRecordComplete() {}

                override fun onRecordStart() {}

                override fun onError(exception: Exception?) {}
            })
            .lensFacing(lensFacing)
            .videoSize(videoWidth, videoHeight)
            .filter(cameraViewModel.filter)
            .build()
    }

    private fun setCameraView() {
        frameLayout.removeAllViews()
        glSurfaceView = SampleCameraGLView(requireContext().applicationContext)
        glSurfaceView.setTouchListener(object : SampleCameraGLView.TouchListener {
            override fun onTouch(event: MotionEvent, width: Int, height: Int) {
                gpuCameraRecorder.changeManualFocusPoint(
                    event.x,
                    event.y,
                    width,
                    height
                )
            }
        })
        frameLayout.addView(glSurfaceView)
    }

    private fun stopCamera() {
        if (this::gpuCameraRecorder.isInitialized && this::glSurfaceView.isInitialized) {
            recordProgress.visibility = View.GONE
            countDownTimer.cancel()
            recordProgress.progress = 15

            glSurfaceView.onPause()
            gpuCameraRecorder.stop()
            gpuCameraRecorder.release()
            frameLayout.removeView(glSurfaceView)
        }
    }
}
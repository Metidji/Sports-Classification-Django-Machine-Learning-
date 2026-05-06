package com.cuaflou.sportsclassification

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var sportsHelper: SportsHelper
    private lateinit var cameraOverlayView: CameraOverlayView
    private lateinit var classView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        setContentView(R.layout.activity_main)

        // 1. Request Permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 10)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        sportsHelper = SportsHelper(this)
        cameraOverlayView = findViewById<CameraOverlayView>(R.id.overlayView)
        classView = findViewById<TextView>(R.id.class_view)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview Use Case
            val viewFinder: PreviewView = findViewById(R.id.viewFinder)
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = viewFinder.surfaceProvider
            }

            // Image Analysis Use Case
            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, SquareAnalyzer @androidx.annotation.RequiresPermission(
                        android.Manifest.permission.BLUETOOTH_CONNECT
                    ) { bitmap ->
                        // This is where you pass the bitmap to your CNN!
                        runInference(bitmap)
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (exc: Exception) {
                Log.e("CameraX", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun runInference(bitmap: Bitmap) {
        val output = sportsHelper.runInference(bitmap)
        if (output != null) {
            var argmax = 0
            for (i in 1..99)
            {
                if (output[0][i] > output[0][argmax])
                    argmax = i
            }
            runOnUiThread {
                classView.setText("Class: " + argmax)
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
package com.cuaflou.sportsclassification

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class SportsHelper(context: Context) {

    private var interpreter: Interpreter

    init {
        val options = Interpreter.Options().apply {
            setNumThreads(4)
            // Use GPU for SimCC if possible
            // addDelegate(GpuDelegate())
        }
        interpreter = Interpreter(loadModelFile(context, "sports.tflite"), options)
    }

    fun runInference(bitmap: Bitmap): Array<FloatArray>? {
        val inputSize = 224 // Matches your training
        val outputSize = 100  // Adjust to your vector length

        // 1. Pre-process Bitmap to ByteBuffer
        val inputBuffer = convertBitmapToBuffer(bitmap, inputSize)

        // 2. Prepare Output Tensors
        val output = Array(1){FloatArray(outputSize)}

        // 3. Run Inference
        interpreter.run(inputBuffer, output)

        return output
    }

    private fun convertBitmapToBuffer(bitmap: Bitmap, size: Int): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(size * size * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(size * size)
        bitmap.getPixels(pixels, 0, size, 0, 0, size, size)

        for (pixel in pixels) {
            buffer.putFloat((((pixel shr 16 and 0xFF).toFloat())))
            buffer.putFloat((((pixel shr 8 and 0xFF).toFloat())))
            buffer.putFloat((((pixel and 0xFF).toFloat())))
        }
        return buffer
    }

    private fun loadModelFile(context: Context, path: String): ByteBuffer {
        val fileDescriptor = context.assets.openFd(path)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }
}
data class PointF(val x: Float, val y: Float)
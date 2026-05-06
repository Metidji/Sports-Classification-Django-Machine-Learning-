package com.cuaflou.sportsclassification
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.graphics.scale

class SquareAnalyzer(private val listener: (Bitmap) -> Unit) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        val bitmap = image.toBitmap()
        val rotatedBitmap = rotateBitmap(bitmap, image.imageInfo.rotationDegrees.toFloat())

        // This logic finds the largest square possible centered in the frame
        val width = rotatedBitmap.width
        val height = rotatedBitmap.height
        val squareSize = Math.min(width, height)

        val left = (width - squareSize) / 2
        val top = (height - squareSize) / 2

        val squareBitmap = Bitmap.createBitmap(
            rotatedBitmap,
            left,
            top,
            squareSize,
            squareSize
        )

        // Resize to your CNN's input (e.g., 224x224)
        val finalBitmap = squareBitmap.scale(224, 224)

        listener(finalBitmap)
        image.close()
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
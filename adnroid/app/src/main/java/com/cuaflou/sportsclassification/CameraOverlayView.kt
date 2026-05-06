package com.cuaflou.sportsclassification

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CameraOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#99000000") // 60% black (darkened)
        style = Paint.Style.FILL
    }

    private val eraserPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val rectPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLUE
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Draw Background and Punch Hole
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        val size = Math.min(width, height).toFloat()
        val left = (width - size) / 2
        val top = (height - size) / 2
        val rect = RectF(left, top, left + size, top + size)

        canvas.drawRect(rect, eraserPaint)
    }
}
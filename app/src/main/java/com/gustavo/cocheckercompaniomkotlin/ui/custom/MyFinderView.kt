package com.gustavo.cocheckercompaniomkotlin.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import com.gustavo.cocheckercompanionkotlin.R
import com.journeyapps.barcodescanner.ViewfinderView

class MyViewFinderView(context: Context?, attrs: AttributeSet?) :
    ViewfinderView(context, attrs) {

    private var laserVisibility = false
    private var cameraMaskPaddingTop: Float? = null
    private var cameraMaskPaddingBottom: Float? = null

    init {
        attrs?.let { attributes ->
            val typedArray = context?.obtainStyledAttributes(
                attributes, R.styleable.MyViewFinderView,
                0, 0
            )

            try {
                cameraMaskPaddingTop =
                    typedArray?.getDimension(R.styleable.MyViewFinderView_top_padding, 0F)
                cameraMaskPaddingBottom =
                    typedArray?.getDimension(R.styleable.MyViewFinderView_bottom_padding, 0F)
            } finally {
                typedArray?.recycle()
            }
        }
    }

    @SuppressLint("DrawAllocation", "CanvasSize")
    override fun onDraw(canvas: Canvas) {
        refreshSizes()
        if (framingRect == null || previewSize == null) {
            return
        }
        val frame = Rect(
            framingRect.left,
            framingRect.top + (cameraMaskPaddingTop?.toInt()
                ?: 0) - (cameraMaskPaddingBottom?.toInt() ?: 0),
            framingRect.right,
            framingRect.bottom + (cameraMaskPaddingTop?.toInt()
                ?: 0) - (cameraMaskPaddingBottom?.toInt() ?: 0)
        )

        val previewFrame = previewSize
        val width = canvas.width
        val height = canvas.height
        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.color = if (resultBitmap != null) resultColor else maskColor
        canvas.drawRect(0f, 0f, width.toFloat(), frame.top.toFloat(), paint)
        canvas.drawRect(
            0f,
            frame.top.toFloat(),
            frame.left.toFloat(),
            frame.bottom + 1.toFloat(),
            paint
        )
        canvas.drawRect(
            frame.right + 1.toFloat(),
            frame.top.toFloat(),
            width.toFloat(),
            frame.bottom + 1.toFloat(),
            paint
        )
        canvas.drawRect(0f, frame.bottom + 1.toFloat(), width.toFloat(), height.toFloat(), paint)
        if (resultBitmap != null) { // Draw the opaque result bitmap over the scanning rectangle
            paint.alpha = CURRENT_POINT_OPACITY
            canvas.drawBitmap(resultBitmap, null, frame, paint)
        } else {
            // If wanted, draw a red "laser scanner" line through the middle to show decoding is active
            if (laserVisibility) {
                paint.color = laserColor
                paint.alpha = SCANNER_ALPHA[scannerAlpha]
                scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.size
                val middle = frame.height() / 2 + frame.top
                canvas.drawRect(
                    frame.left + 2.toFloat(),
                    middle - 1.toFloat(),
                    frame.right - 1.toFloat(),
                    middle + 2.toFloat(),
                    paint
                )
            }

            val scaleX = frame.width() / previewFrame.width.toFloat()
            val scaleY = frame.height() / previewFrame.height.toFloat()
            val frameLeft = frame.left
            val frameTop = frame.top
            // draw the last possible result points
            if (lastPossibleResultPoints.isNotEmpty()) {
                paint.alpha = CURRENT_POINT_OPACITY / 2
                paint.color = resultPointColor
                val radius = POINT_SIZE / 2.0f
                for (point in lastPossibleResultPoints) {
                    canvas.drawCircle(
                        (frameLeft + (point.x * scaleX).toInt()).toFloat(),
                        (frameTop + (point.y * scaleY).toInt()).toFloat(),
                        radius, paint
                    )
                }
                lastPossibleResultPoints.clear()
            }
            // draw current possible result points
            if (possibleResultPoints.isNotEmpty()) {
                paint.alpha = CURRENT_POINT_OPACITY
                paint.color = resultPointColor
                for (point in possibleResultPoints) {
                    canvas.drawCircle(
                        (frameLeft + (point.x * scaleX).toInt()).toFloat(),
                        (frameTop + (point.y * scaleY).toInt()).toFloat(),
                        POINT_SIZE.toFloat(), paint
                    )
                }
                // swap and clear buffers
                val temp = possibleResultPoints
                possibleResultPoints = lastPossibleResultPoints
                lastPossibleResultPoints = temp
                possibleResultPoints.clear()
            }
            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(
                ANIMATION_DELAY,
                frame.left - POINT_SIZE,
                frame.top - POINT_SIZE,
                frame.right + POINT_SIZE,
                frame.bottom + POINT_SIZE
            )
        }
    }

    fun setCameraMaskPaddingTop(padding: Float) {
        cameraMaskPaddingTop = padding
    }

    fun setCameraMaskPaddingBottom(padding: Float) {
        cameraMaskPaddingBottom = padding
    }
}
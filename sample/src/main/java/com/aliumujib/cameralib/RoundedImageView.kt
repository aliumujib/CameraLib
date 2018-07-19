package com.aliumujib.cameralib

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.PorterDuff.Mode
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView

/**
 *
 * Copy and paste coding made possible by
 * https://stackoverflow.com/questions/16208365/how-to-create-a-circular-imageview-in-android
 *
 **/

class RoundedImageView : ImageView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    protected override fun onDraw(canvas: Canvas) {

        val drawable = drawable ?: return

        if (getWidth() == 0 || getHeight() == 0) {
            return
        }
        val b = (drawable as BitmapDrawable).bitmap
        val bitmap = b.copy(Bitmap.Config.ARGB_8888, true)

        val w = getWidth()
        val h = getHeight()

        val roundBitmap = getCroppedBitmap(bitmap, w)
        canvas.drawBitmap(roundBitmap, 0f, 0f, null)
    }

    companion object {

        fun getCroppedBitmap(bmp: Bitmap, radius: Int): Bitmap {
            val sbmp: Bitmap

            if (bmp.width != radius || bmp.height != radius) {
                val smallest = Math.min(bmp.width, bmp.height).toFloat()
                val factor = smallest / radius
                sbmp = Bitmap.createScaledBitmap(bmp,
                        (bmp.width / factor).toInt(),
                        (bmp.height / factor).toInt(), false)
            } else {
                sbmp = bmp
            }

            val output = Bitmap.createBitmap(radius, radius, Config.ARGB_8888)
            val canvas = Canvas(output)

            val color = "#BAB399"
            val paint = Paint()
            val rect = Rect(0, 0, radius, radius)

            paint.setAntiAlias(true)
            paint.setFilterBitmap(true)
            paint.setDither(true)
            canvas.drawARGB(0, 0, 0, 0)
            paint.setColor(Color.parseColor(color))
            canvas.drawCircle(radius / 2 + 0.7f, radius / 2 + 0.7f,
                    radius / 2 + 0.1f, paint)
            paint.setXfermode(PorterDuffXfermode(Mode.SRC_IN))
            canvas.drawBitmap(sbmp, rect, rect, paint)

            return output
        }
    }

}




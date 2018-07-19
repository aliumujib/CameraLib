package com.aliumujib.cameralib

import android.content.Context
import android.os.Handler
import android.support.annotation.StringRes
import android.util.DisplayMetrics
import android.widget.Toast
import java.util.*

/*
 * Created by troy379 on 04.04.17.
 */
object AppUtils {

    fun showToast(context: Context, @StringRes text: Int, isLong: Boolean) {
        showToast(context, context.getString(text), isLong)
    }

    fun showToast(context: Context, text: String, isLong: Boolean) {
        Toast.makeText(context, text, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
}

fun Context.showToast(text: String, isLong: Boolean) {
    Toast.makeText(this, text, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun Context.showToast(@StringRes text: Int, isLong: Boolean) {
    this.showToast(this.getString(text), isLong)
}

fun Any.delay(function: () -> Unit, timeMillis: Long = 1000) {
    val handler = Handler()
    handler.postDelayed({
        function.invoke()
    }, timeMillis)
}

fun Any.delayForASecond(function: () -> Unit) {
    val handler = Handler()
    handler.postDelayed({
        function.invoke()
    }, 1000)
}

inline fun Context.dpToPx(dp: Int): Int {
    var displayMetrics = resources.displayMetrics
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

fun <E> List<E>.randomElements(number: Int): List<E> {
    val copy: List<E> = this
    Collections.shuffle(copy)
    return copy.subList(0, number)
}
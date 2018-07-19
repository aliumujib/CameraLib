package com.aliumujib.cameralib.imageloader

import android.widget.ImageView

/**
 * Created by aliumujib on 12/05/2018.
 */

interface ImageLoader {

    fun load(url: String, imageView: ImageView, fadeEffect: Boolean = true)

}


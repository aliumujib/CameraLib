package com.aliumujib.cameralib.imageloader

import com.squareup.picasso.Picasso
import android.widget.ImageView
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation


/**
 * Created by aliumujib on 12/05/2018.
 */

class PicassoImageLoader(private val picasso: Picasso) : ImageLoader {

    override fun load(url: String, imageView: ImageView, fadeEffect: Boolean) {
        if (fadeEffect)
            picasso.load(url)
                    .fit()
                    .transform(RoundedCornersTransformation(10, 0))
                    .centerCrop()
                    .into(imageView)
        else
            picasso.load(url)
                    .fit()
                    .centerCrop()
                    .noFade().into(imageView)
    }

}
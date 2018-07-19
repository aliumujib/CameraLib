package com.aliumujib.cameralib

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aliumujib.cameralib.imageloader.ImageLoader
import kotlinx.android.synthetic.main.photo_list_item.view.*


class PhotoAdapter(private val items: List<String>, private val context: Context, private val imageLoader: ImageLoader) : RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of photos in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.photo_list_item, parent, false))
    }

    // Binds each photo in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        imageLoader.load("file://"+ items[position], holder!!.photo, true)
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the Imageview that will add each photo to
    val photo = view.snapped_photo!!
}

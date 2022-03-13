package com.example.bgg89.travelmaker_project.Data

import android.annotation.SuppressLint
import android.widget.ImageView

import com.bumptech.glide.Glide
//import com.bumptech.glide.load.engine.DiskCacheStrategy

class GlideHelper {
    @SuppressLint("CheckResult")
    fun loadPaintingImage(image : ImageView, painting : Painting){
        Glide.with(image.context.applicationContext).load(painting.imageId)
        Glide.with(image.context.applicationContext)
                .load(painting.imageId)
                .into(image)
    }
}
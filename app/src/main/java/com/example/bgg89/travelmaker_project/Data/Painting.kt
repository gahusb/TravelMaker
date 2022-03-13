package com.example.bgg89.travelmaker_project.Data

import android.content.res.Resources
import com.example.bgg89.travelmaker_project.R

class Painting internal constructor(imageId:Int, title:String) {
    var imageId:Int = 0
    var title:String
    init{
        this.imageId = imageId
        this.title = title
    }
    companion object {
        fun getAllPaintings(res:Resources): Array<Painting?> {
            val titles = res.getStringArray(R.array.paintings_titles)
//            val years = res.getStringArray(R.array.paintings_years)
//            val locations = res.getStringArray(R.array.paintings_location)
            val images = res.obtainTypedArray(R.array.paintings_images)
            val size = titles.size
            val paintings = arrayOfNulls<Painting>(size)

            for (i in 0 until size)
            {
                val imageId = images.getResourceId(i, -1)
                paintings[i] = Painting(imageId, titles[i])
            }
            images.recycle()
            return paintings
        }
    }
}
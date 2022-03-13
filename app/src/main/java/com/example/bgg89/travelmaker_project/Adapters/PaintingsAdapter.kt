package com.example.bgg89.travelmaker_project.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.alexvasilkov.android.commons.adapters.ItemsAdapter
import com.alexvasilkov.android.commons.ui.ContextHelper
import com.alexvasilkov.android.commons.ui.Views
import com.example.bgg89.travelmaker_project.MainActivity
import com.example.bgg89.travelmaker_project.Data.GlideHelper
import com.example.bgg89.travelmaker_project.Data.Painting
import com.example.bgg89.travelmaker_project.R
import com.example.bgg89.travelmaker_project.CameraActivity

//import com.example.bgg89.travelmaker_project.FoldableListActivity

class PaintingsAdapter internal constructor (context:Context):ItemsAdapter<Painting, PaintingsAdapter.ViewHolder>(), View.OnClickListener {
    init{
        var temp = Painting.getAllPaintings(context.resources)
        var li = temp.toList()
        itemsList = li
    }
    override protected fun onCreateHolder(parent:ViewGroup, viewType:Int): ViewHolder {
        val holder = ViewHolder(parent)
        holder.image.setOnClickListener(this)
        return holder
    }
    override protected fun onBindHolder(holder: ViewHolder, position:Int) {
        val item = getItem(position)
        holder.image.setTag(R.id.list_item_image, item)
        GlideHelper().loadPaintingImage(holder.image, item)
        holder.title.setText(item.title)
    }

    override fun onClick(view:View) {
        val item : Painting = view.getTag(R.id.list_item_image) as Painting
        val activity : Activity = ContextHelper.asActivity(view.context)

        if(item.title == "일정관리" || item.title == "지출내역관리"){
            if (activity is MainActivity) {
                (activity as MainActivity).openDetails(view, item)
            }
        } else {
            if (activity is MainActivity){
                (activity as MainActivity).openDetails(view, item)
            }
        }
    }
    class ViewHolder(parent:ViewGroup):ItemsAdapter.ViewHolder(Views.inflate(parent, R.layout.list_item)) {
        val image:ImageView
        val title:TextView
        init{
            image = Views.find(itemView, R.id.list_item_image)
            title = Views.find(itemView, R.id.list_item_title)
        }
    }
}

package com.example.bgg89.travelmaker_project.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.bgg89.travelmaker_project.R
import com.example.bgg89.travelmaker_project.TravelListActivity

import com.ramotion.expandingcollection.ECCardContentListItemAdapter

class CardListItemAdapter(private var activity: TravelListActivity, context : Context, objects: List<String>) : ECCardContentListItemAdapter<String>(context, R.layout.travel_list_item, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder : ViewHolder
        var rowView = convertView

        if(rowView == null){
            val inflater = LayoutInflater.from(context)
            rowView = inflater.inflate(R.layout.travel_list_item, null)
            viewHolder = ViewHolder()
            viewHolder.itemText = rowView!!.findViewById(R.id.list_item_text) as TextView
            rowView.tag = viewHolder
        } else {
            viewHolder = rowView.tag as ViewHolder
        }

        val item = getItem(position)
        if(item != null) {
            viewHolder.itemText!!.text = item
        }
        return rowView
    }

    internal class ViewHolder{
        var itemText : TextView? = null
    }
}
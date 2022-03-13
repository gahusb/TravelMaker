package com.example.bgg89.travelmaker_project

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.util.AttributeSet
import android.view.View
import android.widget.*
import com.transferwise.sequencelayout.SequenceLayout

import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import java.util.*

class MergedView(context : Context, attributeSet: AttributeSet?) : LinearLayout(context, attributeSet) {
    var myCalender : HorizontalCalendar? = null
    var SequenceView : SequenceLayout?= null
    var Scroller : ScrollView ?= null

    init{
        val infService = Context.LAYOUT_INFLATER_SERVICE
        val li = context.getSystemService(infService) as LayoutInflater
        val v = li.inflate(R.layout.calendar_sequence_merged, this, false)

        addView(v)

        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()

        myCalender = HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build()

        Scroller = findViewById<ScrollView>(R.id.Scroll_Sequence)
        SequenceView = findViewById<SequenceLayout>(R.id.seq_layout)

    }


    fun setScrollTopMargin(topMargin : Int) {
        val layoutParams = (Scroller!!.layoutParams) as LinearLayout.LayoutParams
        layoutParams.topMargin =topMargin
        Scroller!!.setLayoutParams(layoutParams)
    }


}

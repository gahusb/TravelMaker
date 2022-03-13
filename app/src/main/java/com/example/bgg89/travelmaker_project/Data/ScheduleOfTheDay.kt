package com.example.bgg89.travelmaker_project.Data

import android.os.Parcelable
import java.io.Serializable
import java.util.Calendar
import kotlin.collections.ArrayList

class ScheduleOfTheDay (day_ : Calendar) : Serializable{

    val the_date : Calendar = day_
    val schedule : ArrayList<ScheduleItem> = ArrayList()

    // 멤버 데이터 : 날짜,
    //   - 1. 날짜
    //   - 2. ArrayList<SequenceLayoutAdapScheduleItem> '



}

class ScheduleItem ( var title : String,var  subtitle : String,var startTime : List<Int>, var finishTime : List<Int>) : Serializable {

}
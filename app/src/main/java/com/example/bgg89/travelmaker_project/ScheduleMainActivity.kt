package com.example.bgg89.travelmaker_project

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Context
import android.content.Intent
import android.os.PersistableBundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.util.AttributeSet
import android.widget.*
import com.transferwise.sequencelayout.SequenceLayout

import com.example.bgg89.travelmaker_project.Data.ScheduleOfTheDay
import com.example.bgg89.travelmaker_project.Data.ScheduleItem
import com.example.bgg89.travelmaker_project.SequenceLayoutAdapter

import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import devs.mulham.horizontalcalendar.HorizontalCalendarView
import java.util.*
import java.text.DecimalFormat
import kotlin.collections.ArrayList
import android.widget.RelativeLayout
import com.example.bgg89.travelmaker_project.Common.DBHelper
import com.example.bgg89.travelmaker_project.Data.Travel
import com.example.bgg89.travelmaker_project.Data.Schedule


class ScheduleMainActivity : AppCompatActivity(){

    var ScheduleList : ArrayList<ScheduleOfTheDay> = ArrayList()                //   날짜 별로 스케줄 리스트를 저장 (Data Base 에 저장할, 모든 데이터들의 집합)
    val FloatingAction_button : FloatingActionButton by lazy {findViewById<FloatingActionButton>(R.id.FAB)}
    val merged : MergedView by lazy {findViewById<MergedView>(R.id.Merge)}
    val start_day : Calendar by lazy {Calendar.getInstance()}         //  CardView 에서 넘어온 Claendar 데이터(시작 날짜)를 받을 객체
    val end_day : Calendar by lazy {Calendar.getInstance()}           //  CardView 에서 넘어온 Calendar 데이터(종료 날짜) 를 받을 객체
    private val travelnumber by lazy { intent.extras["TravelNumber"] as Int}
    var temp_day : Calendar = Calendar.getInstance()
    lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_main)
        ScheduleList = ArrayList()
        dbHelper = DBHelper(this@ScheduleMainActivity, null, null, 11)

        val travel : Travel = dbHelper.queryTravel(travelnumber)
        val start_year = travel.start.substring(0, 4).toInt()
        val start_month = travel.start.substring(5, 7).toInt() - 1
        val start_date = travel.start.substring(8, 10).toInt()

        val end_year = travel.end.substring(0, 4).toInt()
        val end_month = travel.end.substring(5, 7).toInt() - 1
        val end_date = travel.end.substring(8, 10).toInt()

        // ---------- 해당 줄부터는 intent의 Calendar 데이터를 넘겨받아 시작 날짜 종료 날짜를 대입하는 과정을 보여주는 것 (따라서 intent 를 연결할시 해당 줄들은 삭제)
        start_day.set(start_year,start_month,start_date,0,0)
        end_day.set(end_year,end_month,end_date,0,0)

        // ----------------------------------------------------------------------------------

        val Travel_duration = getDifferOfDate(start_day,end_day)

        merged.setScrollTopMargin(30)

        val schedule_list : ArrayList<Schedule> = dbHelper.queryAllScheduleOfDay(travelnumber)

        val date_iterator : Calendar =  start_day.clone() as Calendar

        for (day in 0 until Travel_duration) {
            val temp : ScheduleOfTheDay = ScheduleOfTheDay(date_iterator)
            ScheduleList.add(temp)
            date_iterator.add(Calendar.DAY_OF_MONTH,1)
        }

        merged.myCalender!!.setRange(start_day,end_day)
        merged.myCalender!!.calendarListener = object : HorizontalCalendarListener(){
            override fun onDateSelected(date: Calendar?, position: Int) {
                val gotten_Schedule = ScheduleList

                val selected : Calendar = merged.myCalender!!.selectedDate
                val selected_idx = getDifferOfDate(start_day,selected) -1
                val selected_schedule = gotten_Schedule[selected_idx].schedule

                Toast.makeText(this@ScheduleMainActivity, "${selected.get(Calendar.DAY_OF_MONTH)}",Toast.LENGTH_SHORT).show()

                repaint(selected_schedule)

            }
            override fun onCalendarScroll(calendarView: HorizontalCalendarView?, dx: Int, dy: Int) {
                super.onCalendarScroll(calendarView, dx, dy)
            }
        }

        FloatingAction_button.setOnClickListener { view->

            val temp_day = merged.myCalender!!.selectedDate
            val date_idx =  getDifferOfDate(start_day,temp_day)-1
            val intent = Intent(this, PopupActivity::class.java)
            val array_data = ScheduleList[date_idx]
            intent.putExtra("Schedule",array_data)
            startActivityForResult(intent,1)


            // intent 에 ScheduleList[date_index] 를 담음
            //     - (1). ArrayList 에 담기는 데이터(ScheduleOfTheDay)이 반드시 직렬화
            //     - (2). it.putExtra("users", listUsers)
            // intent 를 PopupActivity 로 전달

        }

        for(i in 0 until schedule_list.size){
            val schedule = schedule_list[i]
            val today_year = schedule.sch_date.substring(0, 4).toInt()
            val today_month = schedule.sch_date.substring(5, 7).toInt() -1
            val today_date = schedule.sch_date.substring(8, 10).toInt()

            temp_day.set(today_year, today_month, today_date, 0, 0)
            val selected_idx = getDifferOfDate(start_day, temp_day) - 1
            val progArr = ScheduleList[selected_idx].schedule

            val start_list : List<Int> = listOf(schedule.start_time_hour, schedule.start_time_min)
            val finish_list : List<Int> = listOf(schedule.end_time_hour, schedule.end_time_min)
            val newScheduleItem = ScheduleItem(schedule.sch_title, schedule.sub_title, start_list, finish_list)

            val set_index : Int = findInsertIndex(newScheduleItem, progArr)
            progArr.add(set_index, newScheduleItem)
            repaint(progArr)
        }
        val progArr = ScheduleList[0].schedule
        repaint(progArr)

    }


    fun getDifferOfDate (firstDate : Calendar, secondDate: Calendar) : Int {   //  firstDate 와 seondDate 를 포함한 총 날짜 차이수를 Int 로 가져옴
        val diffSec : Long = (secondDate.getTimeInMillis() - firstDate.getTimeInMillis())/1000
        return  (diffSec/(60*60*24) + 1).toInt()
    }

    fun compareTime(a : List<Int>, b : List<Int>) : Int {

        if(a[0] == b[0] && a[1] == b[1])
            return 0

        else if (a[0] < b[0] || (a[0] == b[0] && a[1] < b[1]))
            return -1;

        return 1

    }

    fun findInsertIndex(new_schedule : ScheduleItem, recent_schedule : ArrayList<ScheduleItem>) : Int {

        var memorize = 0

        for(iterator in 0 until recent_schedule.size) { // 0
            // new ScheculeTime <= recent_schedule[i] 라면 return i

            var compare_result = compareTime(new_schedule.finishTime, recent_schedule[iterator].startTime)

            if(compare_result == 0 ||compare_result == -1)
                return iterator

            memorize = iterator+1
        }
        return memorize
    }



    fun repaint (brand_new_Schedule : ArrayList<ScheduleItem>) {

        if(brand_new_Schedule.size == 0) {
            merged.SequenceView!!.visibility = View.INVISIBLE
            return
        }


        val temp_arraylist : ArrayList<SequenceLayoutAdapter.SequenceItem> = ArrayList()   // adapter 에 전달할 리스트
        val formatter = DecimalFormat("00")

        for (iterator in 0 until brand_new_Schedule.size) {   // 0, 1

            val startTime_text : String = formatter.format(brand_new_Schedule[iterator].startTime[0])
            val startMin_text : String = formatter.format(brand_new_Schedule[iterator].startTime[1])
            val finishTime_text : String = formatter.format(brand_new_Schedule[iterator].finishTime[0])
            val finishMin_text : String = formatter.format(brand_new_Schedule[iterator].finishTime[1])

            val time_range_text : String = startTime_text + " : " + startMin_text +"\n     ~   \n" + finishTime_text + " : " + finishMin_text
            temp_arraylist.add(SequenceLayoutAdapter.SequenceItem(brand_new_Schedule[iterator].title,
                    brand_new_Schedule[iterator].subtitle + "\n",time_range_text,false))

        }

        temp_arraylist[temp_arraylist.lastIndex].isActive = true
        val eventual_list = temp_arraylist.toList()
        val adapter = SequenceLayoutAdapter(eventual_list,this@ScheduleMainActivity)
        merged.SequenceView!!.setAdapter(adapter)
        merged.SequenceView!!.visibility = View.VISIBLE
    }

    fun dumyPainter() {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1) {

            if(resultCode == 101) {   //  "New Schedule" 의 key 값을 가진 데이터가 들어올 경우

                val temp_date : Calendar = merged.myCalender!!.selectedDate
                val date_index = getDifferOfDate(start_day,temp_date) - 1
                val progArr = ScheduleList[date_index].schedule

                val schedule_cnt = progArr.size
                val recieved = data!!.getSerializableExtra("New Schedule") as ScheduleItem
                val set_index : Int = findInsertIndex(recieved, progArr)
                progArr.add(set_index,recieved)

                val new_year = temp_date.get(Calendar.YEAR)
                val new_month = temp_date.get(Calendar.MONTH) + 1
                val new_day = temp_date.get(Calendar.DATE)
                var date_string : String = ""

                if(new_month < 10){
                    if(new_day < 10){
                        date_string = new_year.toString() + "-0" + new_month.toString() + "-0" + new_day.toString()
                    } else {
                        date_string = new_year.toString() + "-0" + new_month.toString() + "-" + new_day.toString()
                    }
                } else {
                    if(new_day < 10){
                        date_string = new_year.toString() + "-" + new_month.toString() + "-0" + new_day.toString()
                    } else {
                        date_string = new_year.toString() + "-" + new_month.toString() + "-" + new_day.toString()
                    }
                }
                val schedule = Schedule(0, travelnumber, recieved.title, recieved.subtitle, recieved.startTime[0],
                                        recieved.startTime[1], recieved.finishTime[0], recieved.finishTime[1], date_string)
                dbHelper.insertSchedule(schedule)
                repaint(progArr)
                // 데이터가 추가된 ScheduleItem 을 repaint 함수에 전달
            }

            else if(resultCode== 102) {   //  "Null" 의 key 값을 가진 데이터가 들어올 경우
                // 아무것도 실행하지 않음
            }

        }

        else if(requestCode == 2) {

            if(resultCode == 201) {

                val temp_date_fix : Calendar = merged.myCalender!!.selectedDate
                val date_index_fix = getDifferOfDate(start_day,temp_date_fix) - 1

                val fixed_data = data!!.getBundleExtra("Fixed Schedule_Bundle") as Bundle
                val fixed_index = fixed_data.getInt("Fixed Position")
                val fixed_scheuleItem = fixed_data.getSerializable("Refreshed Schedule") as ScheduleItem

                val new_year = temp_date_fix.get(Calendar.YEAR)
                val new_month = temp_date_fix.get(Calendar.MONTH) + 1
                val new_day = temp_date_fix.get(Calendar.DATE)
                var date_string : String = ""

                val changeTime = ScheduleList[date_index_fix].schedule[fixed_index].startTime[0]
                val changeTitle = ScheduleList[date_index_fix].schedule[fixed_index].title

                if(new_month < 10){
                    if(new_day < 10){
                        date_string = new_year.toString() + "-0" + new_month.toString() + "-0" + new_day.toString()
                    } else {
                        date_string = new_year.toString() + "-0" + new_month.toString() + "-" + new_day.toString()
                    }
                } else {
                    if(new_day < 10){
                        date_string = new_year.toString() + "-" + new_month.toString() + "-0" + new_day.toString()
                    } else {
                        date_string = new_year.toString() + "-" + new_month.toString() + "-" + new_day.toString()
                    }
                }
                val newSchedule = Schedule(0, travelnumber, fixed_scheuleItem.title,
                                            fixed_scheuleItem.subtitle, fixed_scheuleItem.startTime[0], fixed_scheuleItem.startTime[1],
                                            fixed_scheuleItem.finishTime[0], fixed_scheuleItem.finishTime[1], date_string)
                dbHelper.updateSchedule(newSchedule, changeTime, date_string)

                ScheduleList[date_index_fix].schedule.removeAt(fixed_index)

                val progArr = ScheduleList[date_index_fix].schedule
                val set_index : Int = findInsertIndex(fixed_scheuleItem, progArr)
                progArr.add(set_index,fixed_scheuleItem)
                repaint(progArr)
            }

            else if(resultCode == 202) {

            }

        }

        else if(requestCode == 3) {

            if(resultCode == 301) {
                val index_to_delete = data!!.getIntExtra("delete_this!!",-1)
                val temp_date_for_delete : Calendar = merged.myCalender!!.selectedDate
                val date_index_delete = getDifferOfDate(start_day,temp_date_for_delete) - 1

                val new_year = temp_date_for_delete.get(Calendar.YEAR)
                val new_month = temp_date_for_delete.get(Calendar.MONTH) + 1
                val new_day = temp_date_for_delete.get(Calendar.DATE)
                var date_string : String = ""

                if(new_month < 10){
                    if(new_day < 10){
                        date_string = new_year.toString() + "-0" + new_month.toString() + "-0" + new_day.toString()
                    } else {
                        date_string = new_year.toString() + "-0" + new_month.toString() + "-" + new_day.toString()
                    }
                } else {
                    if(new_day < 10){
                        date_string = new_year.toString() + "-" + new_month.toString() + "-0" + new_day.toString()
                    } else {
                        date_string = new_year.toString() + "-" + new_month.toString() + "-" + new_day.toString()
                    }
                }
                val delData = ScheduleList[date_index_delete].schedule[index_to_delete]

                dbHelper.deleteSchedule(delData.finishTime[0], date_string)
                ScheduleList[date_index_delete].schedule.removeAt(index_to_delete)
                repaint(ScheduleList[date_index_delete].schedule)
            }

            else if(resultCode == 302) {

            }
        }

    }

    override fun onBackPressed() {
        val resultIntent = Intent(this, TravelListActivity::class.java)
        setResult(1, resultIntent)
        finish()
        super.onBackPressed()
    }
}
package com.example.bgg89.travelmaker_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.transferwise.sequencelayout.SequenceAdapter
import com.transferwise.sequencelayout.SequenceLayout
import com.transferwise.sequencelayout.SequenceStep
import java.util.*

class SequenceLayoutAdapter(val items : List<SequenceItem>, val context : Context) : SequenceAdapter<SequenceLayoutAdapter.SequenceItem>() {

    override fun bindView(sequenceStep: SequenceStep, item: SequenceItem) {

        with(sequenceStep) {
            setActive(item.isActive)
            setAnchor(item.anchor)

            setTitle(item.title)

            setSubtitle(item.subtitle)


            findViewById<Button>(R.id.change).setOnClickListener {


                val this_activity = context as ScheduleMainActivity
                val this_date = this_activity.merged.myCalender!!.selectedDate
                val selected_index : Int = this_activity.getDifferOfDate(this_activity.start_day,this_date) -1
                val selected_ScheduleList = this_activity.ScheduleList[selected_index]
                val time_text = this.getAnchorText() as String
                val char_sequence = time_text.split(" ",":","~","\n")
                val time_ = ArrayList<Int>()
                var iterator = 0
                var step_index = 0

                while(time_.size < 2) {

                    if(char_sequence[iterator] != "") {
                        time_.add(char_sequence[iterator].toInt())
                    }
                    iterator += 1
                }

                for (i in 0 until selected_ScheduleList.schedule.size) {

                    if (this_activity.compareTime(time_.toList(),selected_ScheduleList.schedule[i].startTime) == 0)
                        step_index = i
                }


                val intent = Intent(context,FixPopupActivity::class.java)
                val bundle : Bundle = Bundle()
                bundle.putInt("child index",step_index)
                bundle.putSerializable("Schedule",selected_ScheduleList)
                intent.putExtra("Fix_item",bundle)
                this_activity.startActivityForResult(intent,2)

            }

            findViewById<Button>(R.id.delete).setOnClickListener {
                val this_activity = context as ScheduleMainActivity
                val this_date = this_activity.merged.myCalender!!.selectedDate
                val selected_index : Int = this_activity.getDifferOfDate(this_activity.start_day,this_date) -1
                val selected_ScheduleList = this_activity.ScheduleList[selected_index]
                val time_text = this.getAnchorText() as String
                val char_sequence = time_text.split(" ",":","~","\n")
                val time_ = ArrayList<Int>()
                var iterator = 0
                var step_index = 0

                while(time_.size < 2) {

                    if(char_sequence[iterator] != "") {
                        time_.add(char_sequence[iterator].toInt())
                    }
                    iterator += 1
                }

                for (i in 0 until selected_ScheduleList.schedule.size) {
                    if (this_activity.compareTime(time_.toList(),selected_ScheduleList.schedule[i].startTime) == 0)
                        step_index = i
                }

                val intent = Intent(context,DeleteScheduleActivity::class.java)
                intent.putExtra("index_to_delete",step_index)
                this_activity.startActivityForResult(intent,3)

            }
        }


    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): SequenceItem {
        return items[position]
    }



    class SequenceItem (var title : String, var subtitle : String, var anchor : String, var isActive: Boolean) {

    }
}
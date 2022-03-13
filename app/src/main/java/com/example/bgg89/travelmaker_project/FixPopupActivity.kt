package com.example.bgg89.travelmaker_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.Display
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.example.bgg89.travelmaker_project.Data.ScheduleItem
import com.example.bgg89.travelmaker_project.Data.ScheduleOfTheDay

class FixPopupActivity : Activity() {

    val bundle_ by lazy {intent.getBundleExtra("Fix_item") as Bundle}
    val EditTextList_f : LinearLayout by lazy { findViewById<LinearLayout>(R.id.input_texts_f) }
    val ButtonList_f : ConstraintLayout by lazy { findViewById<ConstraintLayout>(R.id.constraintLayout_f) }
    var selected_index = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_fix_popup)

        val popup_display : Display = (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay()
        val size : Point = Point()
        popup_display.getSize(size)
        val width = size.x * 0.9   // 팝업창의 가로를 디바이스 가로의 0.9 로 설정
        val height =size.y * 0.8   //  팝업창의 세로를 디바이스 세로의 0.8 로 설정
        getWindow().getAttributes().width = width.toInt()
        getWindow().getAttributes().height = height.toInt()

        val selected_item = bundle_.getSerializable("Schedule") as ScheduleOfTheDay
        selected_index = bundle_.getInt("child index")

        val spinner_start_f : TimePicker = findViewById(R.id.timePicker1_f)
        val spinner_finish_f : TimePicker = findViewById(R.id.timePicker2_f)

        val ok_f = ButtonList_f.findViewById<Button>(R.id.onOk_f)
        val close_f = ButtonList_f.findViewById<Button>(R.id.onClose_f)

        val place_editable_text_f : EditText =  EditTextList_f.findViewById(R.id.place_to_go_f)
        val memo_editable_text_f : EditText = EditTextList_f.findViewById(R.id.memo_text_f)

        place_editable_text_f.setText((selected_item.schedule[selected_index].title))
        memo_editable_text_f.setText((selected_item.schedule[selected_index].subtitle))

        spinner_start_f.setHour(selected_item.schedule[selected_index].startTime[0])
        spinner_start_f.setMinute(selected_item.schedule[selected_index].startTime[1])
        spinner_finish_f.setHour(selected_item.schedule[selected_index].finishTime[0])
        spinner_finish_f.setMinute(selected_item.schedule[selected_index].finishTime[1])

        ok_f.setOnClickListener {

            val fixed_place_text : String = place_editable_text_f.getText().toString()
            val fixed_memo_text : String = memo_editable_text_f.getText().toString()

            val fixed_start_hour = spinner_start_f.getHour()
            val fixed_start_min = spinner_start_f.getMinute()

            val fixed_finish_hour = spinner_finish_f.getHour()
            val fixed_finish_min = spinner_finish_f.getMinute()

            val fixed_startTime: List<Int> = listOf(fixed_start_hour,fixed_start_min)  // 시작시간,시작분을 List 에 차례대로 넣어 시작 시각 을 관리
            val fixed_finishTime: List<Int> = listOf(fixed_finish_hour,fixed_finish_min) // 종료시간,종료분을 List 에 차례대로 넣어 시작 시각 을 관리

            val curr_ScheduleList = selected_item.schedule


            if (fixed_place_text.equals("") || fixed_place_text.trim().isEmpty()) {
                Toast.makeText(this@FixPopupActivity, "장소명은 적어도 한글자 이상으로 구성되야 합니다", Toast.LENGTH_LONG).show()
            }

            else {

                if (compareTime(fixed_startTime,fixed_finishTime) == 0 || compareTime(fixed_startTime,fixed_finishTime) == 1) {
                    Toast.makeText(this@FixPopupActivity, "시작시간은 종료 시간보다 빨라야 합니다", Toast.LENGTH_LONG).show()
                }

                else {

                    if (!isValidFixedSchedule(fixed_startTime, fixed_finishTime, curr_ScheduleList)) {
                        Toast.makeText(this@FixPopupActivity, "입력하신 시간대에 이미 다른 스케줄이 있습니다", Toast.LENGTH_LONG).show()
                    }

                    else {
                        val fixed_schedule = ScheduleItem(fixed_place_text,fixed_memo_text,fixed_startTime,fixed_finishTime)
                        val intent: Intent = Intent()
                        val result_bundle = Bundle()
                        result_bundle.putInt("Fixed Position",selected_index)
                        result_bundle.putSerializable("Refreshed Schedule",fixed_schedule)
                        intent.putExtra("Fixed Schedule_Bundle", result_bundle)
                        setResult(201, intent)
                        finish()
                    }
                }

            }

        }

        close_f.setOnClickListener {
            val intent = Intent()
            setResult(202,intent)             //  무의마한 데이터를 구별해주기 위한 resultCode(102)
            finish()
        }

    }

    fun isValidFixedSchedule (start : List<Int>, finish : List<Int>,scheduleList : ArrayList<ScheduleItem>) : Boolean {

        for(iterator in 0 until scheduleList.size ) {   // 기존 스케줄 표 상의, 모든 스캐줄과 새로운 스케줄 검사

            if(iterator == selected_index )
                continue

            val temp_start : List<Int> = listOf(scheduleList[iterator].startTime[0],scheduleList[iterator].startTime[1])   //가존 스케줄의 시작 시간 (시간, 분))
            val temp_finish : List<Int> = listOf(scheduleList[iterator].finishTime[0],scheduleList[iterator].finishTime[1]) // 기존 스케줄의 종료 시간 (시간, 분)

            val compare_result1 = compareTime(start,temp_start)
            val compare_result2 = compareTime(start, temp_finish)
            val compare_result3 = compareTime(finish,temp_start)
            val compare_result4 = compareTime(finish,temp_finish)

            val condition_1 = (compare_result1 == 0 || (compare_result1 == -1 && compare_result3 == 1))  || (compare_result4 ==0  ||  (compare_result4 == 1 && compare_result2 == -1))
            // 새로운 스케줄의 시간대(시작 시간 ~ 종료 시간) 에 기존의 스케줄이 이미 있는 경우
            val condition_2 = (compare_result1 == 0 || (compare_result1 == 1 && compare_result2 == -1)) || (compare_result4 == 0 || (compare_result4 == -1 && compare_result3 == 1))
            // 기존 스케줄의 시간대(시작 시간 ~ 종료 시간)에 새로운 스케줄이 들어오는 경우

            if(condition_1)
                return false

            else if(condition_2)
                return false
        }

        return true



    }

    fun compareTime(a : List<Int>, b : List<Int>) : Int {    //  a 시간과 와 b 시간이 같을 경우 : 0
        //  a 시간 보다 b 시간 가 나중일 경우 : -1
        // a 시간 보다 b 시간이 일찍일 경우 : 1
        if(a[0] == b[0] && a[1] == b[1])
            return 0

        else if (a[0] < b[0] || (a[0] == b[0] && a[1] < b[1]))
            return -1;

        return 1
    }

    override fun onBackPressed() {   // 뒤로 가기 기능 삭제
        return
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {     // 외부를 터치하더라도, 팝업창이 꺼지지 않음
        if(event?.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false
        return true
    }
}

package com.example.bgg89.travelmaker_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.Display
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.example.bgg89.travelmaker_project.Data.ScheduleItem
import com.example.bgg89.travelmaker_project.Data.ScheduleOfTheDay
import java.util.*
import kotlin.collections.ArrayList

class PopupActivity : Activity() {

    val ButtonList : ConstraintLayout by lazy { findViewById<ConstraintLayout>(R.id.constraintLayout) }   // 확인, 취소 버튼  접근
    val schedule_today : ScheduleOfTheDay by lazy {getIntent().getSerializableExtra("Schedule") as ScheduleOfTheDay }   // ScheduleMainActivity 에서 넘어온 데이터 접근
    val EditTextList : LinearLayout by lazy { findViewById<LinearLayout>(R.id.input_texts) }   // 비고 및 장소 입력창 접근

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)   // 타이틀 바 지움 (반드시 inflate 과정 이전에 위치)
        setContentView(R.layout.activity_popup)

        // ---------- 팝업창의 크기를, 디바이스의 가로 세로 크기에 비례하여 조정 ---------------------------------
        val popup_display : Display = (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay()
        val size : Point = Point()
        popup_display.getSize(size)
        var width = size.x * 0.9   // 팝업창의 가로를 디바이스 가로의 0.9 로 설정
        var height =size.y * 0.8   //  팝업창의 세로를 디바이스 세로의 0.8 로 설정
        getWindow().getAttributes().width = width.toInt()
        getWindow().getAttributes().height = height.toInt()
        //---------------------------------------------------------------------------------------------------


        val day : Calendar = schedule_today.the_date      // intent 로 넘어온 날짜 데이터
        val schedule_list : ArrayList<ScheduleItem> = schedule_today.schedule     // intent 로 넘어온 해당 날짜의, 스케줄 리스트

        val ok = ButtonList.findViewById<Button>(R.id.onOk)
        val close = ButtonList.findViewById<Button>(R.id.onClose)

        val place_editable_text : EditText =  EditTextList.findViewById(R.id.place_to_go)
        val memo_editable_text : EditText = EditTextList.findViewById(R.id.memo_text)

        val TimePicker1 : TimePicker = findViewById(R.id.timePicker1)
        val TimePicker2 : TimePicker = findViewById(R.id.timePicker2)

        ok.setOnClickListener { view ->     // 확인 버튼 터치 시

            var place_text: String = place_editable_text.getText().toString()   // 장소 입력창의 텍스트
            var memo_text: String = memo_editable_text.getText().toString()     // 비고(메모) 입력창의 텍스트

            var startHour: Int = TimePicker1.getHour()    // TimePicker1 에 선택된 시작 시간(hour)을 가져옴
            var startMin: Int = TimePicker1.getMinute()   // TimePicker1 에 선택된 시작 분(Minute) 을 가져옴
            var finishHour: Int = TimePicker2.getHour()   // TimePicker2 에 선택된 종료 시간(Hour) 을 가져옴
            var finishMin: Int = TimePicker2.getMinute()   // TimePicker2 에 선택된 종료 분(Minute) 을 가져옴

            val startTime: List<Int> = listOf(startHour, startMin)  // 시작시간,시작분을 List 에 차례대로 넣어 시작 시각 을 관리
            val finishTime: List<Int> = listOf(finishHour, finishMin) // 종료시간,종료분을 List 에 차례대로 넣어 시작 시각 을 관리
            val schedule_array = schedule_today.schedule

            if (place_text.equals("") || place_text.trim().isEmpty()) {   // 장소 텍스트 가 공백("   ")이거나, 아예 입력이 되지 않았을 경우
                Toast.makeText(this@PopupActivity, "Travel Place must be at least one character!!", Toast.LENGTH_LONG).show()
            }

            else {
                if (compareTime(startTime,finishTime) == 0 || compareTime(startTime,finishTime) == 1) {
                    Toast.makeText(this@PopupActivity, "Start time must be sooner than Finish Time!!", Toast.LENGTH_LONG).show()
                }

                else {
                    if (!isValidSchedule(startTime, finishTime, schedule_array)) {
                        Toast.makeText(this@PopupActivity, "Input time is not valid on the current Schedule", Toast.LENGTH_LONG).show()
                    }

                    else {
                        val added_schedule: ScheduleItem = ScheduleItem(place_text, memo_text, startTime, finishTime)
                        val intent: Intent = Intent()
                        intent.putExtra("New Schedule", added_schedule) //
                        setResult(101, intent)
                        finish()
                    }
                }
            }
        }

        close.setOnClickListener{ view  ->                          // 취소 (==뒤로 가기) 버튼을 누를 경우

            val intent = Intent()
            intent.putExtra("Null",0)      // "Null" 키를 가진, 무의미한 데이터 전송
            setResult(102,intent)             //  무의마한 데이터를 구별해주기 위한 resultCode(102)
            finish()
        }

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


    fun isValidSchedule(start : List<Int>, finish : List<Int>,scheduleList : ArrayList<ScheduleItem>) : Boolean {

        // 새로 추가하려는 스케줄이 유효한 지를 기존의 스케줄들의 시간대와 비교하여 판정 (유효 : true,  무효 : false)

        for(iterator in 0 until scheduleList.size ) {   // 기존 스케줄 표 상의, 모든 스캐줄과 새로운 스케줄 검사

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

    override fun onBackPressed() {   // 뒤로 가기 기능 삭제
        return ;
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {     // 외부를 터치하더라도, 팝업창이 꺼지지 않음
        if(event?.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false
        return true
    }
}

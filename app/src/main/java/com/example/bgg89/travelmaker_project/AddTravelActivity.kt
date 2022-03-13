package com.example.bgg89.travelmaker_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatSpinner
import android.view.*
import android.widget.*
import com.example.bgg89.travelmaker_project.Data.ScheduleItem
import com.example.bgg89.travelmaker_project.Data.ScheduleOfTheDay
import java.util.*
import kotlin.collections.ArrayList

class AddTravelActivity : Activity(), AdapterView.OnItemSelectedListener {
    private var cur1: Int = 0
    val nations = arrayOf("호주", "불가리아", "브라질", "캐나다", "스위스", "중국", "체코", "덴마크", "영국", "홍콩",
                            "헝가리", "인도네시아", "이스라엘", "인도", "일본", "대한민국", "멕시코", "말레이시아",
                            "노르웨이", "뉴질랜드", "필리핀", "폴란드", "러시아", "스웨덴", "싱가포르", "태국", "터키", "미국", "남아공")

    val nations_img = arrayOf("aud", "bgn", "brl", "cad", "chf", "cny", "czk", "dkk", "gbp", "hkd",
                                "huf", "idr", "ils", "inr", "jpy", "krw", "mxn", "myr", "nok", "nzd",
                                "php", "pln", "rub", "sek", "sgd", "thb", "try", "usd", "zar")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)   // 타이틀 바 지움 (반드시 inflate 과정 이전에 위치)
        setContentView(R.layout.activity_add_travel)

        // ---------- 팝업창의 크기를, 디바이스의 가로 세로 크기에 비례하여 조정 ---------------------------------
        val popup_display: Display = (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay()
        val size: Point = Point()
        popup_display.getSize(size)
        var width = size.x * 0.9   // 팝업창의 가로를 디바이스 가로의 0.9 로 설정
        var height = size.y * 0.8   //  팝업창의 세로를 디바이스 세로의 0.8 로 설정
        getWindow().getAttributes().width = width.toInt()
        getWindow().getAttributes().height = height.toInt()
        //---------------------------------------------------------------------------------------------------
        val spinner = findViewById<Spinner>(R.id.travel_spinner)
        spinner.onItemSelectedListener = this

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter

        val place_editable_text : EditText = findViewById(R.id.travel_place)
        val DatePicker1 : DatePicker = findViewById(R.id.datePicker1)
        val DatePicker2 : DatePicker = findViewById(R.id.datePicker2)

        val okBtn = findViewById<Button>(R.id.Travel_onOk)
        val closeBtn = findViewById<Button>(R.id.Travel_onClose)

        okBtn.setOnClickListener {
            var place_text : String = place_editable_text.text.toString()

            var start_date_year = DatePicker1.year
            var start_date_month = DatePicker1.month + 1
            var start_date_day = DatePicker1.dayOfMonth

            var end_date_year = DatePicker2.year
            var end_date_month = DatePicker2.month + 1
            var end_date_day = DatePicker2.dayOfMonth

            val startTime: List<Int> = listOf(start_date_year, start_date_month, start_date_day)  // 시작시간,시작분을 List 에 차례대로 넣어 시작 시각 을 관리
            val finishTime: List<Int> = listOf(end_date_year, end_date_month, end_date_day) // 종료시간,종료분을 List 에 차례대로 넣어 시작 시각 을 관리

            if (place_text.equals("") || place_text.trim().isEmpty()) {   // 장소 텍스트 가 공백("   ")이거나, 아예 입력이 되지 않았을 경우
                Toast.makeText(this@AddTravelActivity, "Travel Place must be at least one character!!", Toast.LENGTH_LONG).show()
            } else {
                if (compareTime(startTime,finishTime) == 0 || compareTime(startTime,finishTime) == 1) {
                    Toast.makeText(this@AddTravelActivity, "Start time must be sooner than Finish Time!!", Toast.LENGTH_LONG).show()
                } else {
                    var start_date : String = ""
                    var end_date : String = ""
                    if(start_date_month < 10){
                        if(start_date_day < 10){
                            start_date = start_date_year.toString() + "-0" + start_date_month.toString() + "-0" + start_date_day.toString()
                        } else {
                            start_date = start_date_year.toString() + "-0" + start_date_month.toString() + "-" + start_date_day.toString()
                        }
                    } else {
                        if(start_date_day < 10){
                            start_date = start_date_year.toString() + "-" + start_date_month.toString() + "-0" + start_date_day.toString()
                        } else {
                            start_date = start_date_year.toString() + "-" + start_date_month.toString() + "-" + start_date_day.toString()
                        }
                    }

                    if(end_date_month < 10){
                        if(end_date_day < 10){
                            end_date = end_date_year.toString() + "-0" + end_date_month.toString() + "-0" + end_date_day.toString()
                        } else {
                            end_date = end_date_year.toString() + "-0" + end_date_month.toString() + "-" + end_date_day.toString()
                        }
                    } else {
                        if(end_date_day < 10){
                            end_date = end_date_year.toString() + "-" + end_date_month.toString() + "-0" + end_date_day.toString()
                        } else {
                            end_date = end_date_year.toString() + "-" + end_date_month.toString() + "-" + end_date_day.toString()
                        }
                    }
                    val nation = nations_img.get(cur1)
                    val intent: Intent = Intent(this@AddTravelActivity, TravelListActivity::class.java)

                    intent.putExtra("Title", place_text)
                    intent.putExtra("Nation", nation)
                    intent.putExtra("StartDate", start_date)
                    intent.putExtra("EndDate", end_date)

                    setResult(101, intent)
                    finish()
                }
            }
        }

        closeBtn.setOnClickListener{                       // 취소 (==뒤로 가기) 버튼을 누를 경우
            val intent = Intent(this@AddTravelActivity, TravelListActivity::class.java)
            setResult(102,intent)             //  무의마한 데이터를 구별해주기 위한 resultCode(102)
            finish()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        println("SELECTED!!!!!!!!!!!!")
        cur1 = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBackPressed() {   // 뒤로 가기 기능 삭제
        return ;
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {     // 외부를 터치하더라도, 팝업창이 꺼지지 않음
        if(event?.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false
        return true
    }

    fun compareTime(a : List<Int>, b : List<Int>) : Int {    //  a 시간과 와 b 시간이 같을 경우 : 0
        //  a 시간 보다 b 시간 가 나중일 경우 : -1
        // a 시간 보다 b 시간이 일찍일 경우 : 1
        if(a[0] == b[0] && a[1] == b[1] && a[2] == b[2])
            return 0
        else if (a[0] < b[0] || (a[0] == b[0] && a[1] < b[1]) || (a[0] == b[0] && a[1] == b[1] && a[2] < b[2]))
            return -1
        return 1
    }
}

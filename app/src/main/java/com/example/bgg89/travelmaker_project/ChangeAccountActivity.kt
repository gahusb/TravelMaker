package com.example.bgg89.travelmaker_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.view.Display
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.*
import kotlinx.android.synthetic.main.activity_plus_account.*

class ChangeAccountActivity : Activity() {

    var option = 2
    var selector = 2
    val clickedTime by lazy  {intent.extras["ClickedTime"]}
    val items by lazy {intent.extras["Items"] as? ArrayList<SpendMainActivity.MyAdapter.MyItem>}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_change_account)

        var clickedTimeOk = 0

        val popup_display : Display = (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay()
        val size : Point = Point()
        popup_display.getSize(size)
        var width = size.x * 0.9   // 팝업창의 가로를 디바이스 가로의 0.9 로 설정
        var height =size.y * 0.8   //  팝업창의 세로를 디바이스 세로의 0.8 로 설정
        getWindow().getAttributes().width = width.toInt()
        getWindow().getAttributes().height = height.toInt()

        for(i in 0 until items!!.size) {
            if(items!![i].formattedDate == clickedTime.toString()) {
                clickedTimeOk = i
                break
            }
        }

        radioGroup1.setOnCheckedChangeListener { group, checkedId ->
            val rb1 = findViewById<RadioButton>(checkedId)
            if(rb1 == findViewById(R.id.minus)) {
                option = 0
            }
            else if(rb1 == findViewById(R.id.plus)) {
                option = 1
            }
            else {
                option = 2
            }
        }

        radioGroup2.setOnCheckedChangeListener { group, checkedId ->
            val rb2 = findViewById<RadioButton>(checkedId)
            if(rb2 == findViewById(R.id.cash)) {
                selector = 0
            }
            else if(rb2 == findViewById(R.id.card)) {
                selector = 1
            }
            else {
                selector = 2
            }
        }

        val history = findViewById<EditText>(R.id.history)
        val money = findViewById<EditText>(R.id.money)
        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        val onOk = findViewById<Button>(R.id.onOk)
        val onClose = findViewById<Button>(R.id.onClose)

        onOk.setOnClickListener {
            var count = 0
            if(items!!.size == 0) {

            }
            else {
                for (i in 0 until items!!.size) {
                    if (items!![clickedTimeOk].formattedDate == timePicker!!.hour.toString() + "시" + timePicker!!.minute.toString() + "분") {
                        count = 0
                        break
                    }
                    var tempHour = ""
                    var tempMinute = ""
                    var a = 0
                    for (j in items!![i].formattedDate) {
                        if (j != '시' && a == 0) {
                            tempHour += j
                        } else {
                            if (j == '시') {
                                a = 1
                            }
                            if (j != '시' && j != '분' && a == 1) {
                                tempMinute += j
                            }
                        }
                    }
                    if (tempHour.toInt() == timePicker!!.hour && tempMinute.toInt() == timePicker!!.minute) {
                        count++
                    }
                }
            }
            when {
                count != 0 -> Toast.makeText(this, "시간이 중복됩니다. 다시 설정하세요!!!", Toast.LENGTH_LONG).show()
                history.text.toString() == "" -> Toast.makeText(this, "내역을 쓰세요!!!", Toast.LENGTH_LONG).show()
                money.text.toString() == "" -> Toast.makeText(this, "금액을 쓰세요!!!", Toast.LENGTH_LONG).show()
                option == 2 -> Toast.makeText(this, "지출 또는 수입을 선택하세요!!!", Toast.LENGTH_LONG).show()
                selector == 2 -> Toast.makeText(this, "현금 또는 카드를 선택하세요!!!", Toast.LENGTH_LONG).show()
                else -> {
                    val back = Intent(this, SpendMainActivity::class.java)
                    back.putExtra("History", history?.text)
                    back.putExtra("Money", money?.text)
                    back.putExtra("Hour", timePicker?.hour)
                    back.putExtra("Minute", timePicker?.minute)
                    back.putExtra("Operator", option)
                    back.putExtra("Selector", selector)
                    back.putExtra("Index", clickedTimeOk)
                    setResult(2, back)
                    //Toast.makeText(this, "변경 완료!", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        onClose.setOnClickListener {
            //Toast.makeText(this, "취소를 누르셨습니다.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onBackPressed() {
        return
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_OUTSIDE)
            return false
        return true
    }
}
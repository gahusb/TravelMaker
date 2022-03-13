package com.example.bgg89.travelmaker_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

class DeleteAccountActivity : Activity() {

    val clickedTime by lazy  {intent.extras["ClickedTime"]}
    val items by lazy {intent.extras["Items"] as? ArrayList<SpendMainActivity.MyAdapter.MyItem>}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_delete_account)
        var clickedTimeOk = 0

        val popup_display : Display = (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay()
        val size : Point = Point()
        popup_display.getSize(size)
        var width = size.x * 0.7
        var height =size.y * 0.2
        getWindow().getAttributes().width = width.toInt()
        getWindow().getAttributes().height = height.toInt()

        for(i in 0 until items!!.size) {
            if(items!![i].formattedDate == clickedTime.toString()) {
                clickedTimeOk = i
                break
            }
        }

        val yes = findViewById<Button>(R.id.yes)
        val no = findViewById<Button>(R.id.no)

        yes.setOnClickListener {
            val back = Intent(this, SpendMainActivity::class.java)
            back.putExtra("Index", clickedTimeOk)
            setResult(3, back)
            //Toast.makeText(this,  "삭제 완료!", Toast.LENGTH_LONG).show()
            finish()
        }

        no.setOnClickListener {
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
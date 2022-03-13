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
import android.widget.Button
import android.widget.Toast

class DeleteScheduleActivity : Activity() {

    val ButtonList by lazy {findViewById<ConstraintLayout>(R.id.ButtonList)}
    val index_to_delete by lazy {intent.getIntExtra("index_to_delete",-1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_delete_schedule)

        val popup_display : Display = (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay()
        val size : Point = Point()
        popup_display.getSize(size)
        val width = size.x * 0.7
        val height =size.y * 0.2
        getWindow().getAttributes().width = width.toInt()
        getWindow().getAttributes().height = height.toInt()

        val ok_delete = ButtonList.findViewById<Button>(R.id.yes_for_schedule_delete)
        val no_delete = ButtonList.findViewById<Button>(R.id.no_for_schedule_delete)


        ok_delete.setOnClickListener {
            val intent = Intent()
            intent.putExtra("delete_this!!",index_to_delete)
            setResult(301,intent)
            finish()
        }

        no_delete.setOnClickListener {
            val intent = Intent()
            setResult(302,intent)
            finish()
        }


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

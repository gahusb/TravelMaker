package com.example.bgg89.travelmaker_project

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.example.bgg89.travelmaker_project.Common.CustomProgressDialog

/**
 * Created by bgg89 on 2018-11-21.
 */

class IntroActivity : AppCompatActivity() {
    private var start : ImageView? = null
    private var background : ImageView? = null
    private var introTxt : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_screen)

        background = findViewById(R.id.introImage)
        introTxt = findViewById(R.id.introTxt)
        start = findViewById(R.id.startBtn)

        start?.setOnClickListener {
            val intent = Intent(this@IntroActivity, MainActivity::class.java)
            intent.putExtra("state", "launch")
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
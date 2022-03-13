package com.example.bgg89.travelmaker_project

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory


import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*

import com.alexvasilkov.foldablelayout.UnfoldableView
import com.alexvasilkov.android.commons.ui.Views.find

import com.example.bgg89.travelmaker_project.Data.Painting
import com.example.bgg89.travelmaker_project.Adapters.PaintingsAdapter
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading
import com.example.bgg89.travelmaker_project.Data.GlideHelper
import java.util.*
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity(){
    //    var gps: GPSProvider? = null
    lateinit var items : List<ActivityInfo>
    lateinit var text2 : TextView
    lateinit var parent : ViewGroup
    lateinit var listTouchInterceptor : View
    lateinit var detailsLayout : View
    lateinit var unfoldableView : UnfoldableView
    lateinit var listView : ListView

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
//        b1 = findViewById(R.id.b1) as Button;

        setContentView(R.layout.activity_unfoldable_details)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        listView = find(this, R.id.list_view)
        listView.adapter = PaintingsAdapter(this)

        listTouchInterceptor = find(this, R.id.touch_interceptor_view)
        listTouchInterceptor.isClickable = false

        detailsLayout = find(this, R.id.details_layout)
        detailsLayout.visibility = View.INVISIBLE

        unfoldableView = find(this, R.id.unfoldable_view)

        var glance : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.unfold_glance)
        unfoldableView.setFoldShading(GlanceFoldShading(glance))

        unfoldableView.setOnFoldingListener(object:UnfoldableView.SimpleFoldingListener() {
            override fun onUnfolding(unfoldableView:UnfoldableView) {
                listTouchInterceptor.isClickable = true
                detailsLayout.visibility = View.VISIBLE
            }
            override fun onUnfolded(unfoldableView:UnfoldableView) {
                listTouchInterceptor.isClickable = false
            }
            override fun onFoldingBack(unfoldableView:UnfoldableView) {
                listTouchInterceptor.isClickable = true
            }
            override fun onFoldedBack(unfoldableView:UnfoldableView) {
                listTouchInterceptor.isClickable = false
                detailsLayout.visibility = View.INVISIBLE
            }
        })
    }

    override fun onBackPressed() {
        if(unfoldableView != null && (unfoldableView.isUnfolded || unfoldableView.isUnfolding)){
            unfoldableView.foldBack()
        } else {
            super.onBackPressed()
        }
    }

    fun openDetails(coverView : View, painting : Painting){
        val image : ImageView = find(detailsLayout, R.id.details_image)
        val timer = Timer()
        GlideHelper().loadPaintingImage(image, painting)
        var nextIntent = Intent(this@MainActivity, TravelListActivity::class.java)

        if(painting.title == "일정관리"){
            nextIntent.putExtra("Kind", "Schedule")
        } else if(painting.title == "지출내역관리"){
            nextIntent.putExtra("Kind", "Spend")
        } else if(painting.title == "카메라"){
            nextIntent = Intent(this@MainActivity, CameraActivity::class.java)
            nextIntent.flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        } else if(painting.title == "지도"){
            nextIntent = Intent(this@MainActivity, MapActivity::class.java)
        }
        unfoldableView.unfold(coverView, detailsLayout)
        timer.schedule(timerTask {
            startActivityForResult(nextIntent, 1)
            overridePendingTransition(0, 0)
            //       finish()
        }, 522)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        val image : ImageView = find(detailsLayout, R.id.details_image)

//        if(data != null){
//            val temp = data.extras["ImageId"] as Int
//            val painting = Painting(temp , "")
//            GlideHelper().loadPaintingImage(image, painting)
//        }
        unfoldableView.foldBack()
        listView.adapter = PaintingsAdapter(this)
    }

//    fun replay(view: View) {
//        val preferenceManager = PreferenceManager(applicationContext)
//        preferenceManager.setFirstTimeLaunch(true)
//        startActivity(Intent(this@MainActivity, Main_Screen::class.java))
//        finish()
//    }

//    fun initGPS() {
//        var mlocManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        gps = GPSProvider(this@MainActivity, mlocManager)
////        gps.setMlocListener(mlocListener)
//    }

//    var mlocListener: LocationListener = LocationListener() {
//        fun onLocationChanged(myLocation: Location) { // 사용자의 위치가 변할때마다 그를 감지해내는 메소드
//            checkLocation(myLocation.getLatitude(), myLocation.getLongitude())
//        }
//    }

    fun checkLocation(latitude: Double, longitude: Double) {

    }
}

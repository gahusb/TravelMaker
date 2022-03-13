package com.example.bgg89.travelmaker_project

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import android.graphics.Bitmap

import com.example.bgg89.travelmaker_project.Data.CardData
import com.example.bgg89.travelmaker_project.Adapters.CardListItemAdapter
import com.example.bgg89.travelmaker_project.Common.DBHelper
import com.example.bgg89.travelmaker_project.Data.Travel

import com.ramotion.expandingcollection.ECBackgroundSwitcherView
import com.ramotion.expandingcollection.ECCardData
import com.ramotion.expandingcollection.ECPagerView
import com.ramotion.expandingcollection.ECPagerViewAdapter
import com.ramotion.expandingcollection.ECPager

import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_travellist.view.*
import java.io.File
import java.util.*

class TravelListActivity : AppCompatActivity() {
    private var ecPagerView : ECPagerView? = null
    private val kind by lazy { intent.extras["Kind"] as String }
    private val FloatingAction_button : FloatingActionButton by lazy {findViewById<FloatingActionButton>(R.id.fab)}
    private val FloatingAction_button2 : FloatingActionButton by lazy {findViewById<FloatingActionButton>(R.id.delete_travel)}
    lateinit var ecPagerViewAdapter : ECPagerViewAdapter
    lateinit var dbHelper: DBHelper
    private var dataset: List<ECCardData<*>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travellist)
        dbHelper = DBHelper(this@TravelListActivity, null, null, 11)

        val guide1 = Travel(0, "+ 버튼으로 여행지를 추가하거나", "default1", "1990-01-01", "2040-12-31")
        dbHelper.insertTravel(guide1)
        val guide2 = Travel(1, "- 버튼으로 여행지를 삭제하세요", "default2", "2042-01-01", "2070-12-31")
        dbHelper.insertTravel(guide2)

        updateCardList()
    }

    private fun updateCardList(){
        ecPagerView = findViewById<View>(R.id.ec_pager_element) as ECPagerView
        ecPagerView!!.ec_pager_element.background = null
        val temp : ArrayList<Travel> = dbHelper.queryAllTravel()
        var templist : MutableList<ECCardData<*>> = ArrayList()
        templist.clear()
        clearApplicationData(this)

        for(i in 0 until temp.size){
            if(i == 0){
                var list = ArrayList<String>()
                list.addAll(Arrays.asList("Item 1"))
                val cardData = CardData(temp[i].title, R.drawable.basic, R.drawable.basic_head, list, 0)
                templist.add(cardData)
            } else if(i == 1){
                var list = ArrayList<String>()
                list.addAll(Arrays.asList("Item 1"))
                val cardData = CardData(temp[i].title, R.drawable.basic, R.drawable.basic_head, list, 1)
                templist.add(cardData)
            } else {
                var list = ArrayList<String>()
                list.addAll(Arrays.asList("Item 1"))
                val travel = temp[i]
                val resName = "@drawable/" + travel.name

                val resId = this.resources.getIdentifier(resName, "drawable", this@TravelListActivity.packageName)
                val resHeaderName = "@drawable/" + travel.name + "_head"

                val resHeadId = this.resources.getIdentifier(resHeaderName, "drawable", this@TravelListActivity.packageName)
                val cardData = CardData(travel.title, R.drawable.basic, resHeadId, list, travel.id )
                templist.add(cardData)
            }
        }
        dataset = templist.toList()

        ecPagerViewAdapter = object : ECPagerViewAdapter(applicationContext, dataset){
            override fun instantiateCard(
                    inflaterService : LayoutInflater,
                    head : ViewGroup,
                    list : ListView,
                    data : ECCardData<*>
            ) {
                val cardData = data as CardData

                val listItems = cardData.listItems
                val listItemAdapter = CardListItemAdapter(this@TravelListActivity, applicationContext, listItems)
                list.adapter = listItemAdapter
                list.setBackgroundColor(Color.WHITE)

                val cardTitle = TextView(applicationContext)
                cardTitle.text = cardData.cardTitle
                cardTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30f)
                cardTitle.setTextColor(Color.WHITE)
                cardTitle.gravity = Gravity.CENTER

                val layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.gravity = Gravity.CENTER
                head.addView(cardTitle, layoutParams)

                if(!cardTitle.text.toString().equals("+ 버튼으로 여행지를 추가하거나") && cardTitle.text.toString() != "- 버튼으로 여행지를 삭제하세요"){

                    head.setOnClickListener {
                        if(kind == "Schedule"){
                            val nextIntent = Intent(this@TravelListActivity, ScheduleMainActivity::class.java)
                            nextIntent.putExtra("TravelNumber", cardData.travelNumber)
                            startActivity(nextIntent)
                        } else if(kind == "Spend"){
                            val nextIntent = Intent(this@TravelListActivity, SpendMainActivity::class.java)
                            nextIntent.putExtra("TravelNumber", cardData.travelNumber)
                            startActivity(nextIntent)
                        }
                    }
                }
            }
        }

        FloatingAction_button.setOnClickListener {
            val intent = Intent(this@TravelListActivity, AddTravelActivity::class.java)
            startActivityForResult(intent, 1)
        }

        FloatingAction_button2.setOnClickListener {
            deleteTravel()
        }
        ecPagerView!!.setPagerViewAdapter(ecPagerViewAdapter)
        ecPagerView!!.setBackgroundSwitcherView(findViewById<View>(R.id.ec_bg_switcher_element) as ECBackgroundSwitcherView)

        ecPagerViewAdapter.notifyDataSetChanged()
    }

    fun deleteTravel(){
        var tempList = dataset!!.toMutableList()
        if(tempList.size == 1 || tempList.size == 2) {
            Toast.makeText(this@TravelListActivity, "삭제할 데이터가 없습니다.", Toast.LENGTH_LONG).show()
        } else {
            dbHelper.deleteTravel(tempList.size - 1)
            dbHelper.deleteAllSchedule(tempList.size-1)
            dbHelper.deleteAllPayment(tempList.size-1)
            tempList.removeAt(tempList.size - 1)
            dataset = tempList.toList()
        }
//        updateCardList()
//        val intent = intent
//        finish()
//        startActivity(intent)
        ecPagerView!!.ec_pager_element.refreshDrawableState()
        updateCardList()
        val refresh = Intent(this@TravelListActivity, TravelListActivity::class.java)
        refresh.putExtra("Kind", kind)
        this.finish()
        startActivity(refresh)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 101){
            if(data != null) {
                val title = data.extras["Title"] as String
                val nation = data.extras["Nation"] as String
                val start_date = data.extras["StartDate"] as String
                val end_date = data.extras["EndDate"] as String
                var number = dbHelper.findTravelCount()

                println("FIND NUMBER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                println(number)
                if(number == 0){
                    val travel = Travel(1, title, nation, start_date, end_date)
                    dbHelper.insertTravel(travel)
                } else {
                    val travel = Travel(number, title, nation, start_date, end_date)
                    dbHelper.insertTravel(travel)
                }
            }
        }
//        updateCardList()
//        val intent = intent
//        finish()
//        startActivity(intent)
        updateCardList()
        val refresh = Intent(this@TravelListActivity, TravelListActivity::class.java)
        refresh.putExtra("Kind", kind)
        this.finish()
        startActivity(refresh)
    }
    override fun onBackPressed() {
//        //if(!ecPagerView!!.collapse())
//        val imageId = this.ecPagerView!!.getP
//        println("imageId !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! $imageId")
        val resultIntent = Intent(this, MainActivity::class.java)
//        resultIntent.putExtra("ImageId", imageId)
        setResult(1, resultIntent)
        super.onBackPressed()
    }

    fun clearApplicationData(context : Context){
        var cache = context.cacheDir
        var appDir = File(cache.parent)
        if(appDir.exists()){
            var children = appDir.list()
            for(s in children){
                deleteDir(File(appDir, s))
                Log.d("test", "File /data/data" + context.packageName + "/" + s + " DELETED")
            }
        }
    }

    fun deleteDir(dir : File) : Boolean{
        if(dir != null && dir.isDirectory){
            var children = dir.list()
            for(i in 0 until children.size){
                var success = deleteDir(File(dir, children[i]))
                if(!success) return false
            }
        }
        return true
    }
}
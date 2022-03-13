package com.example.bgg89.travelmaker_project

import android.view.ViewGroup
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.text.Html
import android.widget.TextView
import android.R.array
import android.content.Context
import android.widget.LinearLayout
import android.widget.Button
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View


/**
 * Created by bgg89 on 2018-11-22.
 */
class Main_Screen : AppCompatActivity() {

    protected lateinit var preferenceManager: PreferenceManager
    lateinit var Layout_bars: LinearLayout
    lateinit var bottomBars: Array<TextView?>
    var screens: IntArray? = null
    lateinit var Skip: Button
    lateinit var Next: Button
    lateinit var vp: ViewPager
    lateinit var myvpAdapter: MyViewPagerAdapter

    internal var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {
            ColoredBars(position)
            if (position == screens!!.size - 1) {
                Next.setText("start")
                Skip.setVisibility(View.GONE)
            } else {
                Next.setText("next")
                Skip.setVisibility(View.VISIBLE)
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

        }

        override fun onPageScrollStateChanged(arg0: Int) {

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        vp = findViewById<View>(R.id.view_pager) as ViewPager
        Layout_bars = findViewById<View>(R.id.layoutBars) as LinearLayout
        // Skip = findViewById<View>(R.id.skip) as Button
        Next = findViewById<View>(R.id.next) as Button
        myvpAdapter = MyViewPagerAdapter()
        vp.adapter = myvpAdapter
        preferenceManager = PreferenceManager(this)
        vp.addOnPageChangeListener(viewPagerPageChangeListener)
        if (!preferenceManager.FirstLaunch()) {
            launchMain()
            finish()
        }
        screens = intArrayOf(R.layout.intro_screen_1, R.layout.intro_screen_2, R.layout.intro_screen_3)
        ColoredBars(0)

        val myThread = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(1200)
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        myThread.start()
    }

    fun next(v: View) {
        val i = getItem(+1)
        if (i < screens!!.size) {
            vp.currentItem = i
        } else {
            launchMain()
        }
    }

    fun skip(view: View) {
        launchMain()
    }

    private fun ColoredBars(thisScreen: Int) {
        val colorsInactive = resources.getIntArray(android.R.array.imProtocols)
        val colorsActive = resources.getIntArray(android.R.array.imProtocols)
        bottomBars = arrayOfNulls(screens!!.size)

        Layout_bars.removeAllViews()
        for (i in bottomBars?.indices) {
            bottomBars[i] = TextView(this)
            bottomBars[i]?.textSize = 100f
            bottomBars[i]?.text = Html.fromHtml("¯")
            Layout_bars.addView(bottomBars!![i])
            bottomBars[i]?.setTextColor(colorsInactive[thisScreen])

        }
        if (bottomBars.size > 0)
            bottomBars[thisScreen]?.setTextColor(colorsActive[thisScreen])
    }

    private fun getItem(i: Int): Int {
        return vp.currentItem + i
    }

    private fun launchMain() {
        preferenceManager.setFirstTimeLaunch(false)
        startActivity(Intent(this@Main_Screen, MainActivity::class.java))
        finish()
    }

    inner class MyViewPagerAdapter : PagerAdapter() {
        private var inflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater!!.inflate(screens!![position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
//            return screens.size
            return 3
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val v = `object` as View
            container.removeView(v)
        }

        override fun isViewFromObject(v: View, `object`: Any): Boolean {
            return v === `object`
        }
    }
}

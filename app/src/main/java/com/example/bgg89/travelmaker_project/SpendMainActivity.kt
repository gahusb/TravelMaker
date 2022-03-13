package com.example.bgg89.travelmaker_project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.icu.util.ValueIterator
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatSpinner
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import com.afollestad.bridge.Bridge
import com.example.bgg89.travelmaker_project.Common.DBHelper
import com.example.bgg89.travelmaker_project.Data.Travel
import com.example.bgg89.travelmaker_project.Data.Payment
import com.transferwise.sequencelayout.SequenceAdapter
import com.transferwise.sequencelayout.SequenceLayout
import com.transferwise.sequencelayout.SequenceStep
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.HorizontalCalendarView
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import kotlinx.android.synthetic.main.activity_plus_account.*

import kotlinx.android.synthetic.main.activity_spend_main.*
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

//import kotlin.collections.ArrayList

class SpendMainActivity() : AppCompatActivity() {


    private val currency1 by lazy { findViewById<EditText>(R.id.currency1) }
    private val currency2 by lazy { findViewById<TextView>(R.id.currency2) }
    private val spinner1 by lazy { findViewById<AppCompatSpinner>(R.id.spinner1)  }
    private val convertBtn by lazy { findViewById<FloatingActionButton>(R.id.convertBtn)  }
    private var cur1: Int = 0
    private var lastEdited: Int = 0
    private val currencyArray by lazy { resources.getStringArray(R.array.currencies) }
    val start_day : Calendar by lazy {Calendar.getInstance()}         //  CardView 에서 넘어온 Claendar 데이터(시작 날짜)를 받을 객체
    val end_day : Calendar by lazy {Calendar.getInstance()}           //  CardView 에서 넘어온 Calendar 데이터(종료 날짜) 를 받을 객체
    private val travelnumber by lazy { intent.extras["TravelNumber"] as Int}
    lateinit var dbHelper: DBHelper
    var parsing :String? = null
    var count = 0
    var temp_day : Calendar = Calendar.getInstance()

    var items = ArrayList<MyAdapter.MyItem>()
    var item = ArrayList<FullItem>()
    var itemsFull = ArrayList<ArrayList<FullItem>>()

    val merge :MergedView by lazy {findViewById<MergedView>(R.id.mergedV)}
    var processor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spend_main)
        merge.myCalender!!.refresh()

        dbHelper = DBHelper(this@SpendMainActivity, null, null, 11)

        val travel : Travel = dbHelper.queryTravel(travelnumber)
        val start_year = travel.start.substring(0, 4).toInt()
        val start_month = travel.start.substring(5, 7).toInt() - 1
        val start_date = travel.start.substring(8, 10).toInt()

        val end_year = travel.end.substring(0, 4).toInt()
        val end_month = travel.end.substring(5, 7).toInt() - 1
        val end_date = travel.end.substring(8, 10).toInt()

        var tmp = total.text.toString().toInt()

        // ---------- 해당 줄부터는 intent의 Calendar 데이터를 넘겨받아 시작 날짜 종료 날짜를 대입하는 과정을 보여주는 것 (따라서 intent 를 연결할시 해당 줄들은 삭제)
        start_day.set(start_year,start_month,start_date,0,0)
        end_day.set(end_year,end_month,end_date,0,0)

        val plus = findViewById<FloatingActionButton>(R.id.plus)
        plus.setOnClickListener {
            val plusIntent = Intent(this, PlusAccountActivity::class.java)
            plusIntent.putExtra("Items", items)
            startActivityForResult(plusIntent, 1)
        }

        val adapter = ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        val payments = dbHelper.queryAllPaymentOfDay(travelnumber)

        var calc_total = 0
        for(i in 0 until payments.size){
            if(payments[i].withdrawordeposit == 0){
                calc_total -= payments[i].payment_amount.toInt()
            } else {
                calc_total += payments[i].payment_amount.toInt()
            }
        }
        total.text = calc_total.toString()

        lastEdited = 1
        currency1?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                lastEdited = 1
            }
        })

        spinner1?.adapter = adapter
        spinner1?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                cur1 = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        JSONTASK().execute("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey="YOUR API KEY"&searchdate=20181206&data=AP01")
        convertBtn.setOnClickListener {
            JSONTASK().execute("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey="YOUR API KEY"&searchdate=20181206&data=AP01")
            currency1?.isEnabled = false
            var appendString = ""
            val inputValue = currency1?.text?.toString()?.toDoubleOrNull() ?: 0.toDouble()
            var tmpString = ""
            if (inputValue == 0.toDouble()) {
                Snackbar.make(it, R.string.empty_value, Snackbar.LENGTH_LONG).show()
                currency1?.isEnabled = true
            } else  {
                val tmp = parsing?.contains(currencyArray[cur1])
                if(tmp != null && tmp) {
                    // 서버로부터 요청을 하면 다른 값들은 잘 나오는데 아래 4가지 값들만 1로 반환돼서 우선 지정해줬습니다.
                    if(currencyArray[cur1] == "CHF") {
                        val returnValue = 1129.84
                        if (lastEdited == 1) {
                            currency2.text = (returnValue * inputValue).toString()
                        }
                        lastEdited = 2
                        currency1?.isEnabled = true
                    }
                    else if(currencyArray[cur1] == "EUR") {
                        val returnValue = 1276.34
                        if (lastEdited == 1) {
                            currency2.text = (returnValue * inputValue).toString()
                        }
                        lastEdited = 2
                        currency1?.isEnabled = true
                    }
                    else if(currencyArray[cur1] == "GBP") {
                        val returnValue = 1433.04
                        if (lastEdited == 1) {
                            currency2.text = (returnValue * inputValue).toString()
                        }
                        lastEdited = 2
                        currency1?.isEnabled = true
                    }
                    else if(currencyArray[cur1] == "USD") {
                        val returnValue = 1122.50
                        if (lastEdited == 1) {
                            currency2.text = (returnValue * inputValue).toString()
                        }
                        lastEdited = 2
                        currency1?.isEnabled = true
                    }
                    else {
                        for (i in parsing!!) {
                            if (i == currencyArray[cur1][0] && parsing!![count + 1] == currencyArray[cur1][1] && parsing!![count + 2] == currencyArray[cur1][2]) {
                                //parsing = parsing!!.removeRange(0, count+3)
                                for (j in (count + 3) until parsing!!.length) {
                                    tmpString += parsing!![j]
                                    count = 0
                                }
                                break
                            }
                            count++
                        }
                        for (i in tmpString) {
                            if (i == ',') {
                                break
                            }
                            appendString += i
                        }
                        for (i in appendString) {
                            if (i == '(') {
                                appendString = appendString.removeRange(0, 5)
                                appendString = (appendString.toDouble() * 0.01).toString()
                                break
                            }
                        }
                        var returnValue: Double? = null
                        if (appendString != "") {
                            returnValue = appendString.toDouble()
                        }
                        if (lastEdited == 1) {
                            if (returnValue != null) {
                                currency2.text = (returnValue * inputValue).toString()
                            }
                        }
                        lastEdited = 2
                        currency1?.isEnabled = true
                    }
                }
            }
        }


//        deleteAllBtn.setOnClickListener {
//
//        }
        merge.myCalender!!.setRange(start_day,end_day)
        merge.myCalender!!.calendarListener = object : HorizontalCalendarListener(){
            override fun onDateSelected(date: Calendar?, position: Int) {
                var tmp : Int = 0
                items.clear()
                findViewById<SequenceLayout>(R.id.seq_layout).removeAllSteps()
                findViewById<SequenceLayout>(R.id.seq_layout).placeDots()

                val payment_list : ArrayList<Payment> = dbHelper.queryAllPaymentOfDay(travelnumber)
                val sq = findViewById<SequenceLayout>(R.id.seq_layout)

                for(i in 0 until payment_list.size){
                    val payment = payment_list[i]
                    val today_year = payment.pay_date.substring(0, 4).toInt()
                    val today_month = payment.pay_date.substring(5, 7).toInt()
                    val today_date = payment.pay_date.substring(8, 10).toInt()

                    val temp_year = merge.myCalender!!.selectedDate.get(Calendar.YEAR)
                    val temp_month = merge.myCalender!!.selectedDate.get(Calendar.MONTH) + 1
                    val temp_date = merge.myCalender!!.selectedDate.get(Calendar.DATE)

                    temp_day.set(today_year, today_month, today_date, 0, 0)
                    if(today_year == temp_year && today_month == temp_month && today_date == temp_date){
                        if(payment.withdrawordeposit == 0 && payment.cashorcredit == 0){
                            items.add(MyAdapter.MyItem(false,
                                    payment.pay_hour + "시" + payment.pay_min + "분",
                                    "현금사용" + " : " + payment.payment_title,
                                    "지출 : " + payment.payment_amount + "원"))
                        } else if(payment.withdrawordeposit == 0 && payment.cashorcredit == 1){
                            items.add(MyAdapter.MyItem(false,
                                    payment.pay_hour + "시" + payment.pay_min + "분",
                                    "카드사용" + " : " + payment.payment_title,
                                    "지출 : " + payment.payment_amount + "원"))

                        } else if(payment.withdrawordeposit == 1 && payment.cashorcredit == 0){
                            items.add(MyAdapter.MyItem(false,
                                    payment.pay_hour + "시" + payment.pay_min + "분",
                                    "현금사용" + " : " + payment.payment_title,
                                    "수입 : " + payment.payment_amount + "원"))

                        } else if(payment.withdrawordeposit == 1 && payment.cashorcredit == 1){
                            items.add(MyAdapter.MyItem(false,
                                    payment.pay_hour + "시" + payment.pay_min + "분",
                                    "카드사용" + " : " + payment.payment_title,
                                    "수입 : " + payment.payment_amount + "원"))

                        }
                        tmp += payment.payment_amount.toInt()
                        for(i in 0 until items.size) {
                            for(k in 0 until items.size-(i+1)) {
                                var tmpHour1 = ""
                                var tmpMinute1 = ""
                                var a = 0
                                for(j in items[k].formattedDate) {
                                    if(j != '시' && a == 0) {
                                        tmpHour1 += j
                                    }
                                    else{
                                        if(j == '시') {
                                            a = 1
                                        }
                                        if(j != '시' && j != '분' && a == 1) {
                                            tmpMinute1 += j
                                        }
                                    }
                                }
                                var tmpHour2 = ""
                                var tmpMinute2 = ""
                                var b = 0
                                for(m in items[k+1].formattedDate) {
                                    if(m != '시' && b == 0) {
                                        tmpHour2 += m
                                    }
                                    else{
                                        if(m == '시') {
                                            b = 1
                                        }
                                        if(m != '시' && m != '분' && b == 1) {
                                            tmpMinute2 += m
                                        }
                                    }
                                }
                                if(tmpHour1.toInt() > tmpHour2.toInt()) {
                                    val garbage = items[k+1]
                                    items[k+1] = items[k]
                                    items[k] = garbage
                                }
                                if((tmpHour1.toInt() == tmpHour2.toInt()) && (tmpMinute1.toInt() > tmpMinute2.toInt())) {
                                    val garbage = items[k+1]
                                    items[k+1] = items[k]
                                    items[k] = garbage
                                }
                            }
                        }

                        items[items.size-1].isActive = true
                        sq.setAdapter(MyAdapter(items))
                        items[items.size-1].isActive = false
                    }
                }
            }
            override fun onCalendarScroll(calendarView: HorizontalCalendarView?, dx: Int, dy: Int) {
                super.onCalendarScroll(calendarView, dx, dy)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null && requestCode == 1 && resultCode == 1) {
            val history = data.extras["History"]
            var money = data.extras["Money"]
            val hour = data.extras["Hour"]
            val minute = data.extras["Minute"]
            val option = data.extras["Operator"]
            val selector = data.extras["Selector"]
            var tmp = total.text.toString().toInt()
            val sq = findViewById<SequenceLayout>(R.id.seq_layout)
            var flag : Boolean = false
            var payment : Payment

            val reselectedDate : Calendar = merge.myCalender!!.selectedDate
//                for(i in 0 until itemsFull.size) {
//                    for(j in 0 until itemsFull[i].size) {
//                        if(itemsFull[i][j].date == reselectedDate) {
//                            for(k in 0 until itemsFull[i][j].schedule.size) {
//                                items.add(itemsFull[i][j].schedule[k])
//                            }
//                            break
//                        }
//                    }
//                }
            val yaer = reselectedDate.get(Calendar.YEAR)
            val month = reselectedDate.get(Calendar.MONTH) + 1
            val date = reselectedDate.get(Calendar.DATE)
            val date_strng : String
            if(month < 10){
                if(date < 10){
                    date_strng = yaer.toString() + "-0" + month.toString() + "-0" + date.toString()
                } else {
                    date_strng = yaer.toString() + "-0" + month.toString() + "-" + date.toString()
                }
            } else {
                if(date < 10){
                    date_strng = yaer.toString() + "-" + month.toString() + "-0" + date.toString()
                } else {
                    date_strng = yaer.toString() + "-" + month.toString() + "-" + date.toString()
                }
            }
            if(option == 0 && selector == 0) {
                tmp -= money.toString().toInt()
                total.text = tmp.toString()

                items.add(MyAdapter.MyItem(flag,
                        hour.toString() + "시" + minute.toString() + "분",
                        "현금사용" + " : " + history.toString(),
                        "지출 : " + money.toString() + "원"))
                payment = Payment(0, travelnumber, 0, 0,history.toString(), money.toString(), hour.toString(), minute.toString(), date_strng)
                dbHelper.insertPayment(payment)
                for(i in 0 until items.size) {
                    for(k in 0 until items.size-(i+1)) {
                        var tmpHour1 = ""
                        var tmpMinute1 = ""
                        var a = 0
                        for(j in items[k].formattedDate) {
                            if(j != '시' && a == 0) {
                                tmpHour1 += j
                            }
                            else{
                                if(j == '시') {
                                    a = 1
                                }
                                if(j != '시' && j != '분' && a == 1) {
                                    tmpMinute1 += j
                                }
                            }
                        }
                        var tmpHour2 = ""
                        var tmpMinute2 = ""
                        var b = 0
                        for(m in items[k+1].formattedDate) {
                            if(m != '시' && b == 0) {
                                tmpHour2 += m
                            }
                            else{
                                if(m == '시') {
                                    b = 1
                                }
                                if(m != '시' && m != '분' && b == 1) {
                                    tmpMinute2 += m
                                }
                            }
                        }
                        if(tmpHour1.toInt() > tmpHour2.toInt()) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                        if((tmpHour1.toInt() == tmpHour2.toInt()) && (tmpMinute1.toInt() > tmpMinute2.toInt())) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                    }
                }
                items[items.size-1].isActive = true
                sq.setAdapter(MyAdapter(items))
                items[items.size-1].isActive = false
            }
            else if(option == 0 && selector == 1){
                tmp -= money.toString().toInt()
                total.text = tmp.toString()
                items.add(MyAdapter.MyItem(flag,
                        hour.toString() + "시" + minute.toString() + "분",
                        "카드사용" + " : " + history.toString(),
                        "지출 : " + money.toString() + "원"))
                payment = Payment(0, travelnumber, 0, 1,history.toString(), money.toString(), hour.toString(), minute.toString(), date_strng)
                dbHelper.insertPayment(payment)
                for(i in 0 until items.size) {
                    for(k in 0 until items.size-(i+1)) {
                        var tmpHour1 = ""
                        var tmpMinute1 = ""
                        var a = 0
                        for(j in items[k].formattedDate) {
                            if(j != '시' && a == 0) {
                                tmpHour1 += j
                            }
                            else{
                                if(j == '시') {
                                    a = 1
                                }
                                if(j != '시' && j != '분' && a == 1) {
                                    tmpMinute1 += j
                                }
                            }
                        }
                        var tmpHour2 = ""
                        var tmpMinute2 = ""
                        var b = 0
                        for(m in items[k+1].formattedDate) {
                            if(m != '시' && b == 0) {
                                tmpHour2 += m
                            }
                            else{
                                if(m == '시') {
                                    b = 1
                                }
                                if(m != '시' && m != '분' && b == 1) {
                                    tmpMinute2 += m
                                }
                            }
                        }
                        if(tmpHour1.toInt() > tmpHour2.toInt()) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                        if((tmpHour1.toInt() == tmpHour2.toInt()) && (tmpMinute1.toInt() > tmpMinute2.toInt())) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                    }
                }
                items[items.size-1].isActive = true
                sq.setAdapter(MyAdapter(items))
                items[items.size-1].isActive = false
            }
            else if(option == 1 && selector == 0) {
                tmp += money.toString().toInt()
                total.text = tmp.toString()
                items.add(MyAdapter.MyItem(flag,
                        hour.toString() + "시" + minute.toString() + "분",
                        "현금사용" + " : " + history.toString(),
                        "수입 : " + money.toString() + "원"))
                payment = Payment(0, travelnumber, 1, 0,history.toString(), money.toString(), hour.toString(), minute.toString(), date_strng)
                println("Test!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                println(payment.payment_title)
                dbHelper.insertPayment(payment)
                for(i in 0 until items.size) {
                    for(k in 0 until items.size-(i+1)) {
                        var tmpHour1 = ""
                        var tmpMinute1 = ""
                        var a = 0
                        for(j in items[k].formattedDate) {
                            if(j != '시' && a == 0) {
                                tmpHour1 += j
                            }
                            else{
                                if(j == '시') {
                                    a = 1
                                }
                                if(j != '시' && j != '분' && a == 1) {
                                    tmpMinute1 += j
                                }
                            }
                        }
                        var tmpHour2 = ""
                        var tmpMinute2 = ""
                        var b = 0
                        for(m in items[k+1].formattedDate) {
                            if(m != '시' && b == 0) {
                                tmpHour2 += m
                            }
                            else{
                                if(m == '시') {
                                    b = 1
                                }
                                if(m != '시' && m != '분' && b == 1) {
                                    tmpMinute2 += m
                                }
                            }
                        }
                        if(tmpHour1.toInt() > tmpHour2.toInt()) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                        if((tmpHour1.toInt() == tmpHour2.toInt()) && (tmpMinute1.toInt() > tmpMinute2.toInt())) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                    }
                }
                items[items.size-1].isActive = true
                sq.setAdapter(MyAdapter(items))
                items[items.size-1].isActive = false
            }
            else if(option == 1 && selector == 1){
                tmp += money.toString().toInt()
                total.text = tmp.toString()
                items.add(MyAdapter.MyItem(flag,
                        hour.toString() + "시" + minute.toString() + "분",
                        "카드사용" + " : " + history.toString(),
                        "수입 : " + money.toString() + "원"))
                payment = Payment(0, travelnumber, 1, 1,history.toString(), money.toString(), hour.toString(), minute.toString(), date_strng)
                dbHelper.insertPayment(payment)
                for(i in 0 until items.size) {
                    for(k in 0 until items.size-(i+1)) {
                        var tmpHour1 = ""
                        var tmpMinute1 = ""
                        var a = 0
                        for(j in items[k].formattedDate) {
                            if(j != '시' && a == 0) {
                                tmpHour1 += j
                            }
                            else{
                                if(j == '시') {
                                    a = 1
                                }
                                if(j != '시' && j != '분' && a == 1) {
                                    tmpMinute1 += j
                                }
                            }
                        }
                        var tmpHour2 = ""
                        var tmpMinute2 = ""
                        var b = 0
                        for(m in items[k+1].formattedDate) {
                            if(m != '시' && b == 0) {
                                tmpHour2 += m
                            }
                            else{
                                if(m == '시') {
                                    b = 1
                                }
                                if(m != '시' && m != '분' && b == 1) {
                                    tmpMinute2 += m
                                }
                            }
                        }
                        if(tmpHour1.toInt() > tmpHour2.toInt()) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                        if((tmpHour1.toInt() == tmpHour2.toInt()) && (tmpMinute1.toInt() > tmpMinute2.toInt())) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                    }
                }
                items[items.size-1].isActive = true
                sq.setAdapter(MyAdapter(items))
                items[items.size-1].isActive = false
            }

//            val temp = items.clone() as java.util.ArrayList<SpendMainActivity.MyAdapter.MyItem>
//            val data = SpendMainActivity.FullItem(reselectedDate, temp)
//            item.add(data)
//            Toast.makeText(this@SpendMainActivity, "${processor}", Toast.LENGTH_LONG).show()
        }

        else if(data != null && requestCode == 2 && resultCode == 2) {
            var index = data.extras["Index"]
            val history = data.extras["History"]
            var money = data.extras["Money"]
            val hour = data.extras["Hour"]
            val minute = data.extras["Minute"]
            val option = data.extras["Operator"]
            val selector = data.extras["Selector"]
            var tmp = total.text.toString().toInt()
            val sq = findViewById<SequenceLayout>(R.id.seq_layout)
            var cnt = 0

            val tmpMoney = items[index.toString().toInt()].subtitle
            val tmpOperator = items[index.toString().toInt()].subtitle
            var realMoney = ""
            var realOperator = ""
            var payment : Payment
            var newPayment : Payment
//                for(i in 0 until itemsFull.size) {
//                    for(j in 0 until itemsFull[i].size) {
//                        if(itemsFull[i][j].date == reselectedDate) {
//                            for(k in 0 until itemsFull[i][j].schedule.size) {
//                                items.add(itemsFull[i][j].schedule[k])
//                            }
//                            break
//                        }
//                    }
//                }


            for(i in tmpMoney) {
                if(i.isDigit()) {
                    realMoney += i
                }
            }

            for(i in tmpOperator) {
                if(cnt == 2) {
                    break
                }
                realOperator += i
                cnt++
            }
            val temp_year = merge.myCalender!!.selectedDate.get(Calendar.YEAR)
            val temp_month = merge.myCalender!!.selectedDate.get(Calendar.MONTH) + 1
            val temp_date = merge.myCalender!!.selectedDate.get(Calendar.DATE)

            val date_strng : String
            if(temp_month < 10){
                if(temp_date < 10){
                    date_strng = temp_year.toString() + "-0" + temp_month.toString() + "-0" + temp_date.toString()
                } else {
                    date_strng = temp_year.toString() + "-0" + temp_month.toString() + "-" + temp_date.toString()
                }
            } else {
                if(temp_date < 10){
                    date_strng = temp_year.toString() + "-" + temp_month.toString() + "-0" + temp_date.toString()
                } else {
                    date_strng = temp_year.toString() + "-" + temp_month.toString() + "-" + temp_date.toString()
                }
            }

            val pivot_hour = items[index as Int].formattedDate.substring(0, 2)
            val pivot_min = items[index as Int].formattedDate.substring(3, 5)

            val temp_payment = dbHelper.queryAllPaymentOfDay(travelnumber)
            println("UPDATE TEST!!!!!!!!!!!!!!!!!")
            println(temp_payment[0].pay_hour)
            println(pivot_hour)
            println(temp_payment[0].pay_min)
            println(pivot_min)
            println(temp_payment[0].pay_date)
            println(date_strng)
            items[index.toString().toInt()].subtitle = money.toString()

            if(option == 0 && selector == 0) {
                payment = Payment(0, travelnumber, 0, 0,history.toString(), money.toString(), hour.toString(), minute.toString(), date_strng)
                dbHelper.updatePayment(payment, pivot_hour, pivot_min, date_strng)
                if(realOperator == "지출") {
                    tmp = tmp + realMoney.toInt() - items[index.toString().toInt()].subtitle.toInt()
                    total.text = tmp.toString()
                }
                else {
                    tmp = tmp - realMoney.toInt() - items[index.toString().toInt()].subtitle.toInt()
                    total.text = tmp.toString()
                }


                items[index.toString().toInt()].title =  "현금사용" + " : " + history.toString()
                items[index.toString().toInt()].subtitle = "지출 : " + money.toString() + "원"
                items[index.toString().toInt()].formattedDate = hour.toString() + "시" + minute.toString() + "분"



                for(i in 0 until items.size) {
                    for(k in 0 until items.size-(i+1)) {
                        var tmpHour1 = ""
                        var tmpMinute1 = ""
                        var a = 0
                        for(j in items[k].formattedDate) {
                            if(j != '시' && a == 0) {
                                tmpHour1 += j
                            }
                            else{
                                if(j == '시') {
                                    a = 1
                                }
                                if(j != '시' && j != '분' && a == 1) {
                                    tmpMinute1 += j
                                }
                            }
                        }
                        var tmpHour2 = ""
                        var tmpMinute2 = ""
                        var b = 0
                        for(m in items[k+1].formattedDate) {
                            if(m != '시' && b == 0) {
                                tmpHour2 += m
                            }
                            else{
                                if(m == '시') {
                                    b = 1
                                }
                                if(m != '시' && m != '분' && b == 1) {
                                    tmpMinute2 += m
                                }
                            }
                        }
                        if(tmpHour1.toInt() > tmpHour2.toInt()) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                        if((tmpHour1.toInt() == tmpHour2.toInt()) && (tmpMinute1.toInt() > tmpMinute2.toInt())) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                    }
                }
                items[items.size-1].isActive = true
                sq.setAdapter(MyAdapter(items))
                items[items.size-1].isActive = false
            }
            else if(option == 0 && selector == 1){
                payment = Payment(0, travelnumber, 0, 1,history.toString(), money.toString(), hour.toString(), minute.toString(), date_strng)
                dbHelper.updatePayment(payment, pivot_hour, pivot_min, date_strng)
                if(realOperator == "지출") {
                    tmp = tmp + realMoney.toInt() - items[index.toString().toInt()].subtitle.toInt()
                    total.text = tmp.toString()
                }
                else {
                    tmp = tmp - realMoney.toInt() - items[index.toString().toInt()].subtitle.toInt()
                    total.text = tmp.toString()
                }


                items[index.toString().toInt()].title = "카드사용" + " : " + history.toString()
                items[index.toString().toInt()].subtitle = "지출 : " + money.toString() + "원"
                items[index.toString().toInt()].formattedDate = hour.toString() + "시" + minute.toString() + "분"


                for(i in 0 until items.size) {
                    for(k in 0 until items.size-(i+1)) {
                        var tmpHour1 = ""
                        var tmpMinute1 = ""
                        var a = 0
                        for(j in items[k].formattedDate) {
                            if(j != '시' && a == 0) {
                                tmpHour1 += j
                            }
                            else{
                                if(j == '시') {
                                    a = 1
                                }
                                if(j != '시' && j != '분' && a == 1) {
                                    tmpMinute1 += j
                                }
                            }
                        }
                        var tmpHour2 = ""
                        var tmpMinute2 = ""
                        var b = 0
                        for(m in items[k+1].formattedDate) {
                            if(m != '시' && b == 0) {
                                tmpHour2 += m
                            }
                            else{
                                if(m == '시') {
                                    b = 1
                                }
                                if(m != '시' && m != '분' && b == 1) {
                                    tmpMinute2 += m
                                }
                            }
                        }
                        if(tmpHour1.toInt() > tmpHour2.toInt()) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                        if((tmpHour1.toInt() == tmpHour2.toInt()) && (tmpMinute1.toInt() > tmpMinute2.toInt())) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                    }
                }
                items[items.size-1].isActive = true
                sq.setAdapter(MyAdapter(items))
                items[items.size-1].isActive = false
            }
            else if(option == 1 && selector == 0) {
                payment = Payment(0, travelnumber, 1, 0,history.toString(), money.toString(), hour.toString(), minute.toString(), date_strng)
                dbHelper.updatePayment(payment, pivot_hour, pivot_min, date_strng)
                if(realOperator == "지출") {
                    tmp = tmp + realMoney.toInt() + items[index.toString().toInt()].subtitle.toInt()
                    total.text = tmp.toString()
                }
                else {
                    tmp = tmp - realMoney.toInt() + items[index.toString().toInt()].subtitle.toInt()
                    total.text = tmp.toString()
                }


                items[index.toString().toInt()].title = "현금사용" + " : " + history.toString()
                items[index.toString().toInt()].subtitle = "수입 : " + money.toString() + "원"
                items[index.toString().toInt()].formattedDate = hour.toString() + "시" + minute.toString() + "분"


                for(i in 0 until items.size) {
                    for(k in 0 until items.size-(i+1)) {
                        var tmpHour1 = ""
                        var tmpMinute1 = ""
                        var a = 0
                        for(j in items[k].formattedDate) {
                            if(j != '시' && a == 0) {
                                tmpHour1 += j
                            }
                            else{
                                if(j == '시') {
                                    a = 1
                                }
                                if(j != '시' && j != '분' && a == 1) {
                                    tmpMinute1 += j
                                }
                            }
                        }
                        var tmpHour2 = ""
                        var tmpMinute2 = ""
                        var b = 0
                        for(m in items[k+1].formattedDate) {
                            if(m != '시' && b == 0) {
                                tmpHour2 += m
                            }
                            else{
                                if(m == '시') {
                                    b = 1
                                }
                                if(m != '시' && m != '분' && b == 1) {
                                    tmpMinute2 += m
                                }
                            }
                        }
                        if(tmpHour1.toInt() > tmpHour2.toInt()) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                        if((tmpHour1.toInt() == tmpHour2.toInt()) && (tmpMinute1.toInt() > tmpMinute2.toInt())) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                    }
                }
                items[items.size-1].isActive = true
                sq.setAdapter(MyAdapter(items))
                items[items.size-1].isActive = false
            }
            else if(option == 1 && selector == 1){
                payment = Payment(0, travelnumber, 1, 1,history.toString(), money.toString(), hour.toString(), minute.toString(), date_strng)
                dbHelper.updatePayment(payment, pivot_hour, pivot_min, date_strng)
                if(realOperator == "지출") {
                    tmp = tmp + realMoney.toInt() + items[index.toString().toInt()].subtitle.toInt()
                    total.text = tmp.toString()
                }
                else {
                    tmp = tmp - realMoney.toInt() + items[index.toString().toInt()].subtitle.toInt()
                    total.text = tmp.toString()
                }


                items[index.toString().toInt()].title = "카드사용" + " : " +  history.toString()
                items[index.toString().toInt()].subtitle = "수입 : " + money.toString() + "원"
                items[index.toString().toInt()].formattedDate = hour.toString() + "시" + minute.toString() + "분"


                for(i in 0 until items.size) {
                    for(k in 0 until items.size-(i+1)) {
                        var tmpHour1 = ""
                        var tmpMinute1 = ""
                        var a = 0
                        for(j in items[k].formattedDate) {
                            if(j != '시' && a == 0) {
                                tmpHour1 += j
                            }
                            else{
                                if(j == '시') {
                                    a = 1
                                }
                                if(j != '시' && j != '분' && a == 1) {
                                    tmpMinute1 += j
                                }
                            }
                        }
                        var tmpHour2 = ""
                        var tmpMinute2 = ""
                        var b = 0
                        for(m in items[k+1].formattedDate) {
                            if(m != '시' && b == 0) {
                                tmpHour2 += m
                            }
                            else{
                                if(m == '시') {
                                    b = 1
                                }
                                if(m != '시' && m != '분' && b == 1) {
                                    tmpMinute2 += m
                                }
                            }
                        }
                        if(tmpHour1.toInt() > tmpHour2.toInt()) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                        if((tmpHour1.toInt() == tmpHour2.toInt()) && (tmpMinute1.toInt() > tmpMinute2.toInt())) {
                            val garbage = items[k+1]
                            items[k+1] = items[k]
                            items[k] = garbage
                        }
                    }
                }
                items[items.size-1].isActive = true
                sq.setAdapter(MyAdapter(items))
                items[items.size-1].isActive = false
            }
            else {

            }


        }

        else if(data != null && requestCode == 3 && resultCode == 3) {
            var index = data.extras["Index"]
            val sq = findViewById<SequenceLayout>(R.id.seq_layout)
            var tmp = total.text.toString().toInt()
            var cnt = 0

            val tmpMoney = items[index.toString().toInt()].subtitle
            val tmpOperator = items[index.toString().toInt()].subtitle
            var realMoney = ""
            var realOperator = ""

            val temp_year = merge.myCalender!!.selectedDate.get(Calendar.YEAR)
            val temp_month = merge.myCalender!!.selectedDate.get(Calendar.MONTH) + 1
            val temp_date = merge.myCalender!!.selectedDate.get(Calendar.DATE)

            val date_strng : String
            if(temp_month < 10){
                if(temp_date < 10){
                    date_strng = temp_year.toString() + "-0" + temp_month.toString() + "-0" + temp_date.toString()
                } else {
                    date_strng = temp_year.toString() + "-0" + temp_month.toString() + "-" + temp_date.toString()
                }
            } else {
                if(temp_date < 10){
                    date_strng = temp_year.toString() + "-" + temp_month.toString() + "-0" + temp_date.toString()
                } else {
                    date_strng = temp_year.toString() + "-" + temp_month.toString() + "-" + temp_date.toString()
                }
            }

            val hour = items[index as Int].formattedDate.substring(0, 2)
            val min = items[index as Int].formattedDate.substring(3, 5)

            val temp = dbHelper.queryAllPaymentOfDay(travelnumber)
            dbHelper.deletePayment(hour, min, date_strng)

            for(i in tmpMoney) {
                if(i.isDigit()) {
                    realMoney += i
                }
            }

            for(i in tmpOperator) {
                if(cnt == 2) {
                    break
                }
                realOperator += i
                cnt++
            }

            items.removeAt(index.toString().toInt())

            if (items.size == 0) {
                if(realOperator == "지출") {
                    tmp = tmp + realMoney.toInt()
                    total.text = tmp.toString()
                }
                else {
                    tmp = tmp - realMoney.toInt()
                    total.text = tmp.toString()
                }
                sq.removeAllSteps()
                sq.placeDots()
            } else {
                if(realOperator == "지출") {
                    tmp = tmp + realMoney.toInt()
                    total.text = tmp.toString()
                }
                else {
                    tmp = tmp - realMoney.toInt()
                    total.text = tmp.toString()
                }
                for (i in 0 until items.size) {
                    for (k in 0 until items.size - (i + 1)) {
                        var tmpHour1 = ""
                        var tmpMinute1 = ""
                        var a = 0
                        for (j in items[k].formattedDate) {
                            if (j != '시' && a == 0) {
                                tmpHour1 += j
                            } else {
                                if (j == '시') {
                                    a = 1
                                }
                                if (j != '시' && j != '분' && a == 1) {
                                    tmpMinute1 += j
                                }
                            }
                        }
                        var tmpHour2 = ""
                        var tmpMinute2 = ""
                        var b = 0
                        for (m in items[k + 1].formattedDate) {
                            if (m != '시' && b == 0) {
                                tmpHour2 += m
                            } else {
                                if (m == '시') {
                                    b = 1
                                }
                                if (m != '시' && m != '분' && b == 1) {
                                    tmpMinute2 += m
                                }
                            }
                        }
                        if (tmpHour1.toInt() > tmpHour2.toInt()) {
                            val garbage = items[k + 1]
                            items[k + 1] = items[k]
                            items[k] = garbage
                        }
                        if ((tmpHour1.toInt() == tmpHour2.toInt()) && (tmpMinute1.toInt() > tmpMinute2.toInt())) {
                            val garbage = items[k + 1]
                            items[k + 1] = items[k]
                            items[k] = garbage
                        }
                    }
                }
                items[items.size - 1].isActive = true
                sq.setAdapter(MyAdapter(items))
                items[items.size - 1].isActive = false

            }
        }

        //Toast.makeText(this, "현재 ${items.size}개의 가계부리스트가 있습니다.", Toast.LENGTH_LONG).show()

    }

    class MyAdapter(private val items: List<MyItem>) : SequenceAdapter<MyAdapter.MyItem>() {

        override fun getCount(): Int {
            return items.size
        }

        override fun getItem(position: Int): MyItem {
            return items[position]
        }

        override fun bindView(sequenceStep: SequenceStep, item: MyItem) {
            with(sequenceStep) {
                setActive(item.isActive)
                setAnchor(item.formattedDate)
                setTitle(item.title)
                setSubtitle(item.subtitle)

                this.findViewById<Button>(R.id.change).setOnClickListener {
                    val xyz = context as SpendMainActivity
                    val changeIntent = Intent(xyz, ChangeAccountActivity::class.java)
                    changeIntent.putExtra("ClickedTime", getAnchorText())
                    changeIntent.putExtra("Items", xyz.items)
                    xyz.startActivityForResult(changeIntent, 2)
                }

                this.findViewById<Button>(R.id.delete).setOnClickListener {
                    val ijk = context as SpendMainActivity
                    val deleteIntent = Intent(ijk, DeleteAccountActivity::class.java)
                    deleteIntent.putExtra("ClickedTime", getAnchorText())
                    deleteIntent.putExtra("Items", ijk.items)
                    ijk.startActivityForResult(deleteIntent, 3)
                }
            }
        }

        data class MyItem(var isActive: Boolean,
                          var formattedDate: String,
                          var title: String,
                          var subtitle: String) :Parcelable {
            constructor(parcel: Parcel) : this(
                    parcel.readByte() != 0.toByte(),
                    parcel.readString(),
                    parcel.readString(),
                    parcel.readString())

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeByte(if (isActive) 1 else 0)
                parcel.writeString(formattedDate)
                parcel.writeString(title)
                parcel.writeString(subtitle)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<MyItem> {
                override fun createFromParcel(parcel: Parcel): MyItem {
                    return MyItem(parcel)
                }

                override fun newArray(size: Int): Array<MyItem?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    inner class JSONTASK : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            var connection : HttpURLConnection? = null
            var reader : BufferedReader? = null
            try {
                val url = URL(params[0])
                connection = url.openConnection() as HttpURLConnection?
                connection?.connect()

                val stream = connection?.inputStream
                reader = BufferedReader(InputStreamReader(stream))
                val line = reader?.readLine()

                val jArray = JSONArray(line)
                var parsingResult = ArrayList<String>()
                for(i in 0 until jArray.length()) {
                    val parentObject = jArray.getJSONObject(i)
                    val name = parentObject.getString("cur_unit")
                    val rate = parentObject.getString("ttb")

                    parsingResult.add("$name$rate")
                }

                return parsingResult.toString()
            }
            catch(e: MalformedURLException) {
                e.printStackTrace()
            }
            catch(e: IOException) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
                reader?.close()
            }
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            this@SpendMainActivity.parsing = result
        }
    }

    data class FullItem(var date:Int, var schedule : ArrayList<MyAdapter.MyItem>)


}

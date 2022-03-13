package com.example.bgg89.travelmaker_project.Common

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context;
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bgg89.travelmaker_project.Data.Travel
import com.example.bgg89.travelmaker_project.Data.Schedule
import com.example.bgg89.travelmaker_project.Data.Payment
/**
 * Created by bgg89 on 2018-12-09.
 */

@SuppressLint("ByteOrderMark")
class DBHelper(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    // db 생성
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TAVEL_TABLE = "CREATE TABLE $TRAVEL_LIST (" +
                "$TREVEL_ID INTEGER PRIMARY KEY," +
                "$TREVEL_TITLE VARCHAR," +
                "$TREVEL_NAME VARCHAR," +
                "$TREVEL_START TEXT," +
                "$TREVEL_END TEXT);"
        val CREATE_SCHEDULE_TABLE = "CREATE TABLE $SCHEDULE_LIST (" +
                "$SCH_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$SCH_FK INTEGER," +
                "$SCH_TITLE VARCHAR," +
                "$SUB_TITLE VARCHAR," +
                "$START_TIME_HOUR INTEGER," +
                "$START_TIME_MIN INTEGER," +
                "$END_TIME_HOUR INTEGER," +
                "$END_TIME_MIN INTEGER," +
                "$SCH_DATE TEXT," +
                "FOREIGN KEY($SCH_FK)" +
                "REFERENCES TRAVEL_LIST($SCH_FK));"
        val CREATE_PAYMENT_TABLE = "CREATE TABLE $PAYMENT_LIST (" +
                "$PAY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$PAY_FK INTEGER," +
                "$WITHDRAWORDEPOSIT SMALLINT," +
                "$CASHORCREDIT SMALLINT," +
                "$PAYMENT_TITLE VARCHAR," +
                "$PAYMENT_AMOUNT VARCHAR," +
                "$PAY_HOUR TEXT," +
                "$PAY_MIN TEXT," +
                "$PAY_DATE TEXT," +
                "FOREIGN KEY($PAY_FK)" +
                "REFERENCES TRAVEL_LIST($PAY_FK));"
        db?.execSQL(CREATE_TAVEL_TABLE)
        db?.execSQL(CREATE_SCHEDULE_TABLE)
        db?.execSQL(CREATE_PAYMENT_TABLE)
    }

    // upgrade시 기존 db삭제
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TRAVEL_LIST)
        db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE_LIST)
        db.execSQL("DROP TABLE IF EXISTS " + PAYMENT_LIST)
        onCreate(db)
    }

    companion object {
        private val mDbHelper: DBHelper? = null
        // All Static variables
        private val DATABASE_PATH: String? = null
        // Database Version
        private val DATABASE_VERSION = 11
        // Database Name
        private val DATABASE_NAME = "TRAVEL"

        // Contacts table name
        private val TRAVEL_LIST = "travel_list"
        private val SCHEDULE_LIST = "schedule_list"
        private val PAYMENT_LIST = "payment_list"
        private val db: SQLiteDatabase? = null

        // Contacts Trevel_List Table Columns names
        val TREVEL_ID = "_id"
        val TREVEL_TITLE = "title"
        val TREVEL_NAME = "name"
        val TREVEL_START = "travel_start"
        val TREVEL_END = "travel_end"

        // Contacts Schedule_List Table Columns names
        val SCH_ID = "_id"
        val SCH_FK = "fk"
        val SCH_TITLE = "schedule_title"
        val SUB_TITLE = "subtitle"
        val START_TIME_HOUR = "start_time_hour"
        val START_TIME_MIN = "start_time_min"
        val END_TIME_HOUR = "end_time_hour"
        val END_TIME_MIN = "end_time_min"
        val SCH_DATE = "date"

        // Contacts Payment_List Table Columns names
        val PAY_ID = "_id"
        val PAY_FK = "fk"
        val WITHDRAWORDEPOSIT = "withdrawORdeposit"
        val CASHORCREDIT = "cashORcredit"
        val PAYMENT_TITLE = "payment_title"
        val PAYMENT_AMOUNT = "payment_amount"
        val PAY_HOUR = "payment_hour"
        val PAY_MIN = "payment_min"
        val PAY_DATE = "payment_date"
    }

    fun insertTravel(values: Travel) {
        val temp = ContentValues()
        temp.put(TREVEL_ID, values.id)
        temp.put(TREVEL_TITLE, values.title)
        temp.put(TREVEL_NAME, values.name)
        temp.put(TREVEL_START, values.start)
        temp.put(TREVEL_END, values.end)
        val db = this.writableDatabase

        db.insert(TRAVEL_LIST, null, temp)
        db.close()
    }

    fun insertSchedule(values: Schedule) {
        val temp = ContentValues()
        temp.put(SCH_FK, values.sch_fk)
        temp.put(SCH_TITLE, values.sch_title)
        temp.put(SUB_TITLE, values.sub_title)
        temp.put(START_TIME_HOUR, values.start_time_hour)
        temp.put(START_TIME_MIN, values.start_time_min)
        temp.put(END_TIME_HOUR, values.end_time_hour)
        temp.put(END_TIME_MIN, values.end_time_min)
        temp.put(SCH_DATE, values.sch_date)
        val db = this.writableDatabase

        db.insert(SCHEDULE_LIST, null, temp)
        db.close()
    }

    fun insertPayment(values: Payment) {
        val temp = ContentValues()
        temp.put(PAY_FK, values.pay_fk)
        temp.put(WITHDRAWORDEPOSIT, values.withdrawordeposit)
        temp.put(CASHORCREDIT, values.cashorcredit)
        temp.put(PAYMENT_TITLE, values.payment_title)
        temp.put(PAYMENT_AMOUNT, values.payment_amount)
        temp.put(PAY_HOUR, values.pay_hour)
        temp.put(PAY_MIN, values.pay_min)
        temp.put(PAY_DATE, values.pay_date)
        val db = this.writableDatabase
        db.insert(PAYMENT_LIST, null, temp)
        db.close()
    }

//    fun queryAll(): Cursor {
//        return db!!.rawQuery("select * from " + TRAVEL_LIST, null)
//    }

    fun deleteTravel(select : Int) {
        val selected = select.toString()
        val query = "SELECT * FROM $TRAVEL_LIST WHERE $TREVEL_ID = \"$selected\""
        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        if(cursor != null){
            cursor.moveToFirst()
            val id = cursor.getInt(0)
            db.delete(TRAVEL_LIST, TREVEL_ID + " =?", arrayOf(id.toString()))
            cursor.close()
        }
        db.close()
    }
    fun deleteAllSchedule(fk : Int){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM " + SCHEDULE_LIST + " WHERE " + SCH_FK + "= '" + "$fk" + "'")
        db.close()
    }
    fun deleteSchedule(start_hour : Int, date : String){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM " + SCHEDULE_LIST + " WHERE " + START_TIME_HOUR + "= '" +"$start_hour" + "' AND " +
                   SCH_DATE + "= '" + "$date" + "'" )
        db.close()
    }
    fun deleteAllPayment(fk : Int){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM " + PAYMENT_LIST + " WHERE " + PAY_FK + "= '" + "$fk" + "'")
        db.close()
    }
    fun deletePayment(hour : String, min : String, date : String){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM " + PAYMENT_LIST + " WHERE " + PAY_HOUR + "= '" + hour + "' AND " +
                    PAY_DATE + "= '" + date + "' AND " + PAY_MIN + "= '" + min + "'")
        db.close()
    }

    fun updateSchedule(schedule : Schedule, start : Int, date : String){
        val db = this.writableDatabase
        db.execSQL("UPDATE " + SCHEDULE_LIST + " SET " + SCH_FK + "= '" + "${schedule.sch_fk}" +"' , " +
                    SCH_TITLE + "= '" + "${schedule.sch_title}" + "' , " + SUB_TITLE + "= '" + "${schedule.sub_title}" + "' , " +
                    START_TIME_HOUR + "= '" +"${schedule.start_time_hour}" + "' , " + START_TIME_MIN + "= '" + "${schedule.start_time_min}" + "' , " +
                    END_TIME_HOUR + "= '" + "${schedule.end_time_hour}" + "' , " + END_TIME_MIN + "= '" + "${schedule.end_time_min}" + "' , " +
                    SCH_DATE + "= '" + "${schedule.sch_date}" + "' " + "WHERE " + START_TIME_HOUR + "= '" + "$start" + "' AND " +
                    SCH_DATE + "= '" + "$date" + "'")
        db.close()
    }

    fun updatePayment(payment: Payment, hour: String, min: String, date : String) {
        val db = this.writableDatabase
        db.execSQL("UPDATE " + PAYMENT_LIST + " SET " + PAY_FK + "= '" + "${payment.pay_fk}" + "' , " +
                    WITHDRAWORDEPOSIT + "= '" + "${payment.withdrawordeposit}" + "' , " + CASHORCREDIT + "= '" + "${payment.cashorcredit}" + "' , " +
                    PAYMENT_TITLE + "= '" + "${payment.payment_title}" + "' , " + PAYMENT_AMOUNT + "= '" + "${payment.payment_amount}" + "' , " +
                    PAY_HOUR + "= '" + "${payment.pay_hour}" + "' , " +PAY_MIN + "= '" + "${payment.pay_min}" + "' , " + PAY_DATE + "= '" + "${payment.pay_date}" + "' " + "WHERE " +
                    PAY_HOUR + "= '" + "$hour" + "' AND " + PAY_MIN + "= '" + min + "' AND " + PAY_DATE + "= '" + "$date" + "'")
        db.close()
    }

    fun queryTravel(values: Int): Travel {
        val query = "SELECT * FROM $TRAVEL_LIST WHERE $TREVEL_ID = \"$values\""
        val db = this.readableDatabase
        val cursor : Cursor = db.rawQuery(query, null)

        cursor.moveToFirst()
        val travel = Travel(cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4))
        db.close()
        return travel
    }
    fun findTravelCount() : Int{
        val query = "SELECT COUNT(*) FROM $TRAVEL_LIST"
        val db = this.readableDatabase
        val cursor : Cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            println(cursor.getInt(0))
            val id = cursor.getInt(0)
            db.close()
            return id
        } else {
            return 0
        }
    }
    fun queryAllTravel(): ArrayList<Travel> {
        val query = "SELECT * FROM $TRAVEL_LIST"
        val db = this.readableDatabase
        val cursor : Cursor = db.rawQuery(query, null)

        var travelList : ArrayList<Travel> = ArrayList<Travel>()
        var travel : Travel? = null

        for (i in 0 until cursor.count) {
            if(i == 0) cursor.moveToFirst()
            val id = cursor.getInt(0)
            val title = cursor.getString(1)
            val name = cursor.getString(2)
            val start = cursor.getString(3)
            val end = cursor.getString(4)

            travel = Travel(id, title, name, start, end)
            travelList.add(travel)
            cursor.moveToNext()
        }
        db.close()
        return travelList
    }

    fun queryAllScheduleOfDay(fk : Int) : ArrayList<Schedule>{
        val query = "SELECT * FROM $SCHEDULE_LIST WHERE $SCH_FK = \"$fk\""
        val db = this.readableDatabase
        val cursor : Cursor = db.rawQuery(query, null)

        var schList : ArrayList<Schedule> = ArrayList<Schedule>()
        var schedule : Schedule?

        for(i in 0 until cursor.count){
            if(i == 0) cursor.moveToFirst()
            val sch_id = cursor.getInt(0)
            val sch_fk = cursor.getInt(1)
            val sch_title = cursor.getString(2)
            val sub_title = cursor.getString(3)
            val start_time_hour = cursor.getInt(4)
            val start_time_min = cursor.getInt(5)
            val end_time_hour = cursor.getInt(6)
            val end_time_min = cursor.getInt(7)
            val sch_date = cursor.getString(8)

            schedule = Schedule(sch_id, sch_fk, sch_title, sub_title, start_time_hour, start_time_min, end_time_hour, end_time_min, sch_date)
            schList.add(schedule)
            cursor.moveToNext()
        }
        db.close()
        return schList
    }

    fun queryAllPaymentOfDay(fk: Int): ArrayList<Payment>{
        val query = "SELECT * FROM ${PAYMENT_LIST} WHERE ${PAY_FK} = \"$fk\""
        val db = this.readableDatabase
        val cursor : Cursor = db.rawQuery(query, null)

        var payList : ArrayList<Payment> = ArrayList<Payment>()
        var payment : Payment?

        for(i in 0 until cursor.count){
            if(i == 0) cursor.moveToFirst()
            val pay_id = cursor.getInt(0)
            val pay_fk = cursor.getInt(1)
            val withdrawordeposit = cursor.getInt(2)
            val cashorcredit = cursor.getInt(3)
            val payment_title = cursor.getString(4)
            val payment_amount = cursor.getString(5)
            val pay_hour = cursor.getString(6)
            val pay_min = cursor.getString(7)
            val pay_date = cursor.getString(8)

            payment = Payment(pay_id, pay_fk, withdrawordeposit, cashorcredit, payment_title, payment_amount, pay_hour, pay_min, pay_date)
            payList.add(payment)
            cursor.moveToNext()
        }
        db.close()
        return payList
    }
}

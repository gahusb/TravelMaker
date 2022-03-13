package com.example.bgg89.travelmaker_project

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by bgg89 on 2018-11-22.
 */
class PreferenceManager(context: Context) {
    private val FIRST_LAUNCH = "firstLaunch"
    var MODE = 0
    private val PREFERENCE = "Javapapers"
    var sharedPreferences: SharedPreferences? = context.getSharedPreferences(PREFERENCE, MODE)
    var spEditor: SharedPreferences.Editor? = sharedPreferences?.edit()

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        spEditor?.putBoolean(FIRST_LAUNCH, isFirstTime)
        spEditor?.commit()
    }

    fun FirstLaunch(): Boolean {
        return sharedPreferences!!.getBoolean(FIRST_LAUNCH, true)
    }
}
//package com.example.bgg89.travelmaker_project.GPS
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.location.Criteria
//import android.location.Location
//import android.location.LocationListener
//import android.location.LocationManager
//import android.support.v4.app.ActivityCompat
//
///**
// * Created by bgg89 on 2018-11-21.
// */
//class GPSProvider(context: Context, mlocManager: LocationManager) {
//    var context: Context = context
//    var mlocManager: LocationManager = mlocManager
//    var mlocListener: LocationListener
//    var provider: String
//    var location: Location
//
//    var latitude: Double
//    var longitude: Double
//
//    var criteria: Criteria = Criteria()
//
//    criteria.setAccuracy(Criteria.ACCURACY_COARSE)
//    criteria.setPowerRequirement(Criteria.POWER_LOW)
//    criteria.setAltitudeRequired(false);
//    criteria.setBearingRequired(false);
//    criteria.setSpeedRequired(false);
//    criteria.setCostAllowed(true);
//
//    fun startGetMyLocation() {
//        latitude = 0.0
//        longitude = 0.0
//        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            return
//        }
//        // 정보 제공자를 통해 외치 업데이트를 한 다음
//        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener)
//        // 최종 위치 정보를 파악해내고
//        location = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//    }
//
//    fun setMlocListener(mlocListener: LocationListener) {
//        this.mlocListener = mlocListener
//    }
//
//    fun removeUpdate() {
//        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            return
//        }
//        if(mlocManager != null && mlocListener != null) {
//            mlocManager.removeUpdates(mlocListener)
//        }
//    }
//}
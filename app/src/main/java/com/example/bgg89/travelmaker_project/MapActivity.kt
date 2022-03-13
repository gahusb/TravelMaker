package com.example.bgg89.travelmaker_project

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList
import java.util.HashMap

import org.json.JSONObject

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.example.bgg89.travelmaker_project.Common.DirectionsJSONParser
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapActivity : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mCurrentLocation: Location
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var currentProvider: String? = null

    private lateinit var map: GoogleMap
    private var markerPoints: ArrayList<LatLng> = ArrayList()

    private val UPDATE_INTERVAL:Long = 10 * 1000
    private val FASTEST_INTERVAL:Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.direction_maps)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkPermission()
        }

        var btn_find: Button = findViewById(R.id.btn_find)
        btn_find.setOnClickListener {
            initGoogleMapLocation()
        }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapfrag) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        checkPermission()

        val kau = LatLng(37.598936882, 126.8641854672)
        map.addMarker(MarkerOptions()
                .position(kau)
                .title("KAU")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(kau, 16.0f))

        map.setOnMapClickListener(this)
    }

    override fun onMapClick(position: LatLng?) {
        Toast.makeText(this@MapActivity, "출발지점과 도착지점을 선택하세요.", Toast.LENGTH_SHORT).show()
        if (map != null) {
            // Enable MyLocation Button in the Map
            if(checkPermission())
                map.isMyLocationEnabled = true

            map.setOnMapClickListener { point ->
                // Already two locations
                if (markerPoints.size > 1) {
                    markerPoints.clear()
                    map.clear()
                }

                markerPoints.add(point)
                val options = MarkerOptions()
                options.position(point)

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (markerPoints.size == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                } else if (markerPoints.size == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                }

                map.addMarker(options)

                if (markerPoints.size >= 2) {
                    val origin = markerPoints[0]
                    val dest = markerPoints[1]

                    val url = getDirectionsUrl(origin, dest)
                    val downloadTask = DownloadTask()
                    println("marker " + url)
                    downloadTask.execute(url)
                }
            }
        }
        startLocationUpdates()
    }

    private  val mLocationCallback = object : LocationCallback(){
        /*
         *  성공적으로 위치정보와 넘어왔을때를 동작하는 Call back 함수
         */
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)

            mCurrentLocation = result!!.locations[0]

            val options = MarkerOptions()
            options.position(LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude))
            val icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
            options.icon(icon)
            val marker = map.addMarker(options)

            /*
             * 단말기 현재 위치로 이동한다
             */
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 16f))
            /*
             * 지속적으로 위치정보를 받으려면
             * mLocationRequest.numUpdates = 1을 주석처리하고
             * 밑에 코드 주석을 푼다
             */
            //mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        /*
         * 현재 콜백이 동작가능한지에 대한 여부
         */
        override fun onLocationAvailability(availability: LocationAvailability?) {
            //boolean isLocation = availability.isLocationAvailable();
        }
    }

    /*
     * 현재 위치
     */
    @SuppressLint("MissingPermission")
    private fun initGoogleMapLocation() {
        /*
         * FusedLocationProviderApi에서
         * 위치 업데이트를위한 서비스 품질등 다양한요청을
         * 설정하는데 사용하는 데이터객체인
         * LocationRequest를 획득
         */
        mLocationRequest = LocationRequest()
        /*
         *위치가 update되는 주기
         */
        mLocationRequest.interval = 10000
        /*
         * 위치 획득후 update되는 주기
         */
        mLocationRequest.fastestInterval = 10000

        /*
         * update되는 횟수 여기선 1번만 설정한다
         */
        mLocationRequest.numUpdates = 1

        if (currentProvider.equals(LocationManager.GPS_PROVIDER, ignoreCase = true)) {
            //배터리소모에 상관없이 정확도를 최우선으로 고려
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        } else {
            //배터리와 정확도의 밸런스를 고려하여 위치정보를 획득(정확도 다소 높음)
            mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
        /*
         * 위치서비스 설정 정보를 저장하기 위한 빌더객체획득
         */
        val builder = LocationSettingsRequest.Builder()
        /*
         * 현재 위치정보 Setting정보가 저장된 LocationRequest
         * 객체를 등록
         */
        builder.addLocationRequest(mLocationRequest)

        /*
         * 위치정보 요청을 수행하기 위해 단말기에서
         * 관련 시스템 설정(Gps,Network)이 활성화되었는지 확인하는 클래스인
         * SettingClient를 획득한다
         */

        val mSettingsClient = LocationServices.getSettingsClient(this)

        /*
         * 위치 서비스 유형을 저장하고
         * 위치 설정에도 사용하기위해
         * LocationSettingsRequest 객체를 획득
         */
        val mLocationSettingsRequest = builder.build()
        val locationResponse = mSettingsClient.checkLocationSettings(mLocationSettingsRequest)

        /*
         * 현재 위치제공자(Provider)와 상호작용하는 진입점인
         * FusedLocationProviderClient 객체를 획득
         */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        /*
         * 정상적으로 위치정보가 설정되었다면
         * 위치업데이트를 요구하고, 설정이 잘못되었다면
         * Log를 출력한다
         */
        with(locationResponse){
            //위치정보획득 성공시
            addOnSuccessListener{
                Log.d("Response", "Success!!")
                mFusedLocationClient?.requestLocationUpdates(
                        mLocationRequest, mLocationCallback, Looper.myLooper())
            }
            //위치정보 획득 실패시
            addOnFailureListener{
                when ((it as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        Log.e("onFailure", "위치환경체크")
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                        Log.e("onFailure", "위치설정체크요망")
                }
            }
        }
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        // Sensor enabled
        val sensor = "sensor=false"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor"
        // Output format
        val output = "json"

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            iStream = urlConnection.inputStream

            val br = BufferedReader(InputStreamReader(iStream!!))
            val sb = StringBuffer()
            var line : String?
            do {
                line = br.readLine()
                if (line == null)
                    break
                sb.append(line)
            } while (true)

            data = sb.toString()
            br.close()
        } catch (e: Exception) {
            Log.d("Exception ", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    private inner class DownloadTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg url: String): String {
            var data = ""
            try {
                data = downloadUrl(url[0])
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val parserTask = ParserTask()

            println("DownloadTask " + result)
            parserTask.execute(result)
        }
    }

    private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {
        override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jObject = JSONObject(jsonData[0])
                val parser = DirectionsJSONParser()
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
            var points: ArrayList<LatLng>? = null
            var lineOptions: PolylineOptions? = null
            val markerOptions = MarkerOptions()

            println("onPostExecute 들어옴" + result)
            for (i in result.indices) {
                println("for문 들어옴")
                points = ArrayList<LatLng>()
                lineOptions = PolylineOptions()
                val path = result[i]
                for (j in path.indices) {
                    val point = path[j]

                    val lat = java.lang.Double.parseDouble(point["lat"] as String)
                    val lng = java.lang.Double.parseDouble(point["lng"] as String)
                    val position = LatLng(lat, lng)

                    points?.add(position)
                }

                lineOptions.addAll(points)
                lineOptions.width(2f)
                lineOptions.color(Color.RED)
            }
            map.addPolyline(lineOptions)
        }
    }

//    /**
//     * on start lifecycle method
//     */
//    override fun onStart() {
//        startLocationUpdates()
//    }

    /**
     * method to initialize all necessary objects and listeners
     */
    protected fun startLocationUpdates() {
        // initialize location request object
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.run {
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            setInterval(UPDATE_INTERVAL)
            setFastestInterval(FASTEST_INTERVAL)
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient!!.checkLocationSettings(locationSettingsRequest)

        registerLocationListner()
    }

    /**
     * to register location listener
     */
    private fun registerLocationListner() {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.getLastLocation())
            }
        }
        // add permission if android version is greater then 23
        if(Build.VERSION.SDK_INT >= 23 && checkPermission()) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
        }
    }

    /**
     * @param Location, updated location object to show in toast
     */
    private fun onLocationChanged(location: Location) {
        var msg = "Updated Location: " + location.latitude  + " , " +location.longitude

        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
    }


    /**
     * @return, true if permission is already allowed for the app else it will return false
     */
    private fun checkPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            requestPermissions()
            return false
        }
    }



    /**
     * if application is not allowed for the ACCESS_FINE_LOCATION
     * then it will open pop-up to grant that permission
     */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf("Manifest.permission.ACCESS_FINE_LOCATION"),1)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1) {
//            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION ) {
//                registerLocationListner()
//            }
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true)
                    Log.i("locationPermission", "onRequestPermissionsResult: Dóna localització al permetre el permis ")
                }

            } else {
            }
            return
        }
    }

    override fun onStop() {
        super.onStop()
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }
}

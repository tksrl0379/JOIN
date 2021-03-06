@file:Suppress("DEPRECATION")

package com.example.join.Map

import android.annotation.SuppressLint

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location

import android.location.Location.distanceBetween
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Display
import android.view.View

import android.widget.TextView

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import com.example.join.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_record_map.*
import kotlinx.android.synthetic.main.fragment_record.*
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback
import org.jetbrains.anko.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer


// Fused Location Provider 활용 -> https://www.sphinfo.com/google-play-fused-location-provider/
// fusedLocationProviderClient.requestLocationUpdates() -> 위치 데이터 요청. (callback은 위치데이터를 받을 곳)
// public Task<Void> requestLocationUpdates (LocationRequest request, LocationCallback callback, Looper looper)

class RecordMapActivity : AppCompatActivity(), View.OnClickListener,
    MapFragment.OnConnectedListener,
    SensorEventListener {

    private var mMap: GoogleMap? = null
    private var REQUEST_ACCESS_FINE_LOCATION = 1000
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    //init_location
    private lateinit var locationCallback: MyLocationCallback

    //선을 그어주는 변수
    private val polylineOptions = PolylineOptions().width(7f).color(Color.RED)

    //private  static String FRAGMENT_TAG = "FRAGMENTB_TAG"
    private var detailTag: String = "DetailTag"

    private var time = -1
    private var timeTask: Timer? = null

    var mapfr: Fragment = MapFragment()
    var detailfr: Fragment = RecordFragment() //

    val builder = LatLngBounds.builder()
    //val extStorageDirectory: String =  Environment.getExternalStorageDirectory().toString()

    // 기록 시작 버튼 클릭 여부 확인
    var recordPressed = false
    // 기록 시작 허가
    var recordStart = false
    // 이전 기록
    var before_location = arrayOfNulls<Double>(2)
    // 누적 거리
    var total_distance: Double = 0.0
    // 시간초
    var total_sec: Int = 0

    // 고도
    var max_altitude: Double = 0.0
    // 위도, 경도 저장
    var latlngArray: ArrayList<Pair<Double, Double>> = ArrayList()
    // 속도 저장
    var averSpeed: String? = null

    //만보기
    var pedometer: Int = 0


    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    // TYPE_STEP_COUNTER 은 핸드폰을 재부팅해야만 0으로 초기화되므로 사용하지 않음
    override fun onSensorChanged(event: SensorEvent) {
        event.let {
            if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                Log.d("Sensor ", "Success")
                if (event.values[0] == 1.0f) {
                    pedometer += 1
                    recordPedometer.text = pedometer.toString()   //만보기 기능.
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty()) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addLocationListener()
                } else {
                    toast("Permission Denied.")
                }
                return
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_map)

        locationInit()
        supportFragmentManager.beginTransaction().add(R.id.mainFrame, mapfr).commit()

        recordStartFab.setOnClickListener(this)
        recordPauseFab.setOnClickListener(this)
        recordResumeFab.setOnClickListener(this)
        recordUploadFab.setOnClickListener(this)

        detailsFab?.setOnClickListener(this)
        mapFab?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        var id = view!!.id
        when (id) {
            R.id.recordStartFab -> StartFab()
            R.id.recordPauseFab -> PauseFab()
            R.id.detailsFab -> DetailsToMap()
            R.id.mapFab -> MapToDetails()
            R.id.recordResumeFab -> ResumeFab()

            R.id.recordUploadFab -> {

                if(latlngArray.size == 0){
                    toast("아직 이동하지 않으셨습니다.")
                }else{
                    startActivityForResult<UploadActivity>(
                        100,
                        "distance" to total_distance,
                        "time" to time,
                        "latlng" to latlngArray,
                        "max_altitude" to max_altitude,
                        "averSpeed" to averSpeed,
                        "pedometer" to pedometer
                    )
                }
            }
        }
    }

    // 시작 지점
    private fun StartFab() {

        sensorManager.registerListener( //센서기능 시작
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        recordStartFab.hide()   //시작버튼 기능 종료
        recordPauseFab.show()   //중지버튼 기능 시작.

        // 레코드 버튼 눌림 확인
        recordPressed = true

        //액티비티에서 시간을 시작하고 변경된 값을 DetailFragmt에 전송.
        startTimer()

        //시작을 눌렀을때 기능이 실행해야하므로 여기서 프래그먼트 add. ( RecordFragment)
        supportFragmentManager.beginTransaction().add(R.id.mainFrame, detailfr, detailTag)
            .hide(detailfr).commit()

        MapToDetails()
    }

    fun PauseFab() {
        //Todo: Resume버튼과 Finish 버튼을 만들고, Realm에 데이터를 넣는 상황을 멈춘다. 또한 디테일 프래그먼트(시간,속도,거리) 기능도 중지한다.
        // Polyline멈춤, Details멈춤.
        // 새로운 프래그먼트(현재까지의 시간, 속도,거리) 를 나타내는 맵 위에 걸쳐 붙쳐진 프래그먼트가 필요.


        sensorManager.unregisterListener(this)//   센서기능 정지.
        pauseTimer()
        recordPressed = false
        recordStart = false
        recordPauseFab.hide()
        recordResumeFab.show()
        recordUploadFab.show()

    }

    private fun ResumeFab() {
        //Todo: 다시 위도,경도를 저장하기 시작하며 거리를 계산하는 기능. StartFab 기능과 흡사함.


        sensorManager.registerListener( //센서기능 시작
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        startTimer() //중지했던 시간을 계속하여 측정한다.
        recordPressed = true
        //recordStart = true
        recordResumeFab.hide()
        recordUploadFab.hide()
        recordPauseFab.show()


    }

    /*private fun UploadFab() {

        //스냅샷 하기 이전에 현재까지 이동한 선들을 한 화면에 표시하기.
        //지금까지 그어진 폴리라인 선들을 한 화면에 볼 수 있게 함.
        val bounds = builder.build()
        mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

        val cache : File = cacheDir

        val file = File(cache, "mapPreview.png")    //파일명지정
        //val file = File(extStorageDirectory, "snapTest.png")    //파일명지정
        println("파일 이름: "+file)
        var outputStream: OutputStream = FileOutputStream(file)


        val snapshotReadyCallback =
            GoogleMap.SnapshotReadyCallback {  //mMap.snapshot누를시 호출 되는 함수로 여기서 화면 캡쳐 기능 구현.
                    bitmap: Bitmap -> Bitmap.createBitmap(1080, 400, Bitmap.Config.ARGB_8888)

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

                outputStream.flush()
                outputStream.close()

                //var snapshotUri = Uri.fromFile(File("/sdcard/snapTest.png"))

                //println(snapshotUri)
            }
        val snapshotMap = mMap?.snapshot(snapshotReadyCallback)   //구글맵 스크린샷.
        if(snapshotMap != null){       //저장되었는지 확인.
            toast("성공")
            println()
        }else{
            toast("실패")
        }
        startActivityForResult<UploadActivity>(100,
            "distance" to total_distance, "time" to time, "latlng" to latlngArray, "max_altitude" to max_altitude,
            "averSpeed" to averSpeed, "pedometer" to pedometer)
    }*/

    private fun DetailsToMap() {

        // fragment 전환 및 애니메이션 효과(setCustomAnimations)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.from_right, R.anim.to_left).hide(detailfr).commit()

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.from_right, R.anim.to_left).show(mapfr).commit()

        mapFab.show()
        detailsFab.hide()

    }

    private fun MapToDetails() {

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.from_left, R.anim.to_right).hide(mapfr).commit()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.from_left, R.anim.to_right).show(detailfr).commit()


        detailsFab.show()
        mapFab.hide()
    }


    override fun onResume() {
        super.onResume()
        permissionCheck(cancel = { showPermissionInfoDialog() },
            ok = { addLocationListener() })

    }

    override fun onPause() {    //onPause. 즉 액티비티가 보이지 않으면 실행되는 메소드.
        super.onPause()
    }

    // onDestroy에서 종료해줘야 Timer / LocationCallBack 이 정지됨.
    override fun onDestroy() {
        super.onDestroy()
        println("멈췄습니다")
        // onStop 호출때 마다 Timer / LocationCallBack 멈춰줌
        pauseTimer()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    //매인액티비티에서 위치정보를 얻고 난 뒤에 지도를 표시.
    private fun permissionCheck(cancel: () -> Unit, ok: () -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                cancel()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }
        } else {
            ok()
        }
    }


    private fun showPermissionInfoDialog() {
        alert("현재 위치 정보를 얻기 위해서는 위치 권한이 필요합니다.", "권한이 필요한 이유") {
            yesButton {
                ActivityCompat.requestPermissions(
                    this@RecordMapActivity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }
            noButton { }
        }.show()
    }

    @SuppressLint("MissingPermission")
    private fun addLocationListener() { //Init=false and record=true
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }


    private fun locationInit() {
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        locationCallback = MyLocationCallback()
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000  //10000time 후에 현재위치로 돌아가는 옵션.
        locationRequest.fastestInterval = 5000

    }


    //타이머 측정 함수  TODO: period를 10으로 두면 백그라운드에서 엄청 느리게감. 이유찾아보기
    private fun startTimer() {
        timeTask = timer(period = 1000) {

            time++  //절대적 시간초.  //이 time을 가지고 속도를 구할수 있다.
            // 총 초시간 저장
            total_sec = time

            var sec = time % 60
            var min = time / 60 % 60
            var hour = time / 3600

            // 밑의 TextView.text 들이 null 을 입력받는 시점이 있음. 이 때 ?을 넣어줘야함.
            // thread에선 view 못 건드리므로 runOnUiThread 사용하여 변경
            runOnUiThread {
                //UI를 갱신해주는 쓰레드

                secTextView?.text = sec.toString()
                minTextView?.text = min.toString()
                hourTextView?.text = hour.toString()

            }
        }
    }

    private fun pauseTimer() {

        timeTask?.cancel()  //Timer의 객체로써 null일수 있기에 timetask 옆에 ? 붙음.
    }


    //TODO : start 누르는 순간 기록 시작. upload 누를 시 firestore에 업로드
    inner class MyLocationCallback : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            val location = locationResult?.lastLocation
            //var circleOptions = CircleOptions()
            // let 은 객체를 블록의 인자로 넘겨서 it으로 사용 가능
            //run 은 객체를 블록의 리시버로 넘겨서 따로 객체 선언 없이 암시적으로 사용 가능.

            location?.run {

                val latLng = LatLng(
                    latitude,
                    longitude
                )    //interval되는 순간마다 latitud와 lonitude를 array배열로 저장해야함

                Log.d("MapActivity", "lan:$latitude, long:$longitude")


                //latitude,longitude를 builder에 넣어 나중에 모든 경로에 대해 알맞게 카메라 조정을 할 수 있음.
                builder.include(LatLng(latitude, longitude))


                if (recordStart) {

                    if (altitude > max_altitude)
                        max_altitude = altitude

                    println("고도: " + max_altitude)
                    //latitude,longitude를 builder에 넣어 나중에 모든 경로에 대해 알맞게 카메라 조정을 할 수 있음.
                    //builder.include(LatLng(latitude, longitude))

                    // 위도, 경도 저장
                    latlngArray.add(Pair(latitude, longitude))

                    val arrayex = FloatArray(1)
                    distanceBetween(
                        latitude,
                        longitude,
                        before_location[0]!!,
                        before_location[1]!!,
                        arrayex
                    )

                    total_distance += arrayex[0] / 1000

                    // 거리 표시
                    distanceKm.text = String.format("%.2f", total_distance)
                    // 시속 표시
                    averSpeed = String.format("%.2f", total_distance * (3600 / total_sec))
                    averageSpeed.text = averSpeed

                    println("거리:" + total_distance)
                    println("초:" + total_sec)
                    println("시속: " + (total_distance * (3600 / total_sec)) + "km")


                    // Latlng(latitude,longitude) 을 이용하여 지도에 선 그리기
                    polylineOptions.add(latLng)
                    polylineOptions.width(13f)
                    polylineOptions.visible(true)   // 선이 보여질지/안보여질지 옵션.

                    mMap?.addPolyline(polylineOptions)
                }
            }

            if (recordPressed) {
                recordStart = true
                // 이전 기록 저장
                before_location[0] = location!!.latitude
                before_location[1] = location!!.longitude
            }
        }
    }

    // MapFragment 의 mMap 객체와 연결
    override fun onConnect(map: GoogleMap) {
        mMap = map
    }


    // UploadActivity에서 upload_success 신호를 보내면 finish()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK)
            if (requestCode == 100) {
                if (data!!.getStringExtra("result").equals("upload_success"))
                    finish()
            }
    }
}


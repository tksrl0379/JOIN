package com.example.join.Map

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.Location.distanceBetween
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.join.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_record_map.*
import kotlinx.android.synthetic.main.fragment_record.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

// Fused Location Provider 활용 -> https://www.sphinfo.com/google-play-fused-location-provider/
// fusedLocationProviderClient.requestLocationUpdates() -> 위치 데이터 요청. (callback은 위치데이터를 받을 곳)
// public Task<Void> requestLocationUpdates (LocationRequest request, LocationCallback callback, Looper looper)
class RecordMapActivity : AppCompatActivity(), View.OnClickListener, MapFragment.OnConnectedListener{

    //private lateinit var mainfrgmt: Fragment
    private var mMap: GoogleMap? = null
    private var REQUEST_ACCESS_FINE_LOCATION = 1000
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    //init_location
    private lateinit var locationCallback: MyLocationCallback

    //선을 그어주는 변수
    private val polylineOptions = PolylineOptions().width(7f).color(Color.YELLOW)

    //private  static String FRAGMENT_TAG = "FRAGMENTB_TAG"
    private var detailTag: String = "DetailTag"


    private var time = 0
    private var timeTask: Timer? = null

    var mapfr: Fragment = MapFragment()       //MapFrgmt()
    var detailfr: Fragment = RecordFragment() //

    // 기록 시작 버튼 클릭 여부 확인
    var recordPressed = false
    // 기록 시작 허가
    var recordStart = false
    // 이전 기록
    var before_location = arrayOfNulls<Double>(2)
    // 누적 거리
    var total_distance: Double = 0.0




    //var startOrstop : Bundle = Bundle() //프래그먼트의 기능을 실행할지 멈출지 하는 번들.
    var startOrStop: Boolean = false //map,detail의 저장기능을 시작할지 말지

    //위치정보를 구현하기위한 메소드


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
//        val frgmtManiger : FragmentManager
        //초기화


        if (savedInstanceState == null) {

        }


        //val recordUploadFab2: FloatingActionButton
        //        = findViewById(R.id.recordUploadFab) as FloatingActionButton


        //recordUploadFab2.setOnClickListener { println("dd") }

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
            //R.id.recordUploadFab -> startActivity<ResultActivity>()
        }

    }

    // 시작 지점
    private fun StartFab() {

        //Todo:이곳에서 RealmData에 저장, Polyline실행

        recordStartFab.hide()   //시작버튼 기능 종료
        recordPauseFab.show()   //중지버튼 기능 시작.

        // 레코드 버튼 눌림 확인
        recordPressed = true


        startTimer() //액티비티에서 시간을 시작하고 변경된 값을 DetailFragmt에 전송.

        //map에서도 poly라인 선이 그어지기 시작해야하므로



        //시작을 눌렀을때 기능이 실행해야하므로 여기서 프래그먼트 add.
        supportFragmentManager.beginTransaction().add(R.id.mainFrame, detailfr, detailTag).commit()


        //Todo:MapFr와 detailFr한테 시작하라는 기능을 전달해줘야한다.


        MapToDetails()
    }

    fun PauseFab() {
        //Todo: Resume버튼과 Finish 버튼을 만들고, Realm에 데이터를 넣는 상황을 멈춘다. 또한 디테일 프래그먼트(시간,속도,거리) 기능도 중지한다.
        // Polyline멈춤, Details멈춤.
        // 새로운 프래그먼트(현재까지의 시간, 속도,거리) 를 나타내는 맵 위에 걸쳐 붙쳐진 프래그먼트가 필요.


        pauseTimer()
        recordPauseFab.hide()
        recordResumeFab.show()
        recordUploadFab.show()


    }

    private fun ResumeFab() {
        //Todo: 다시 위도,경도를 저장하기 시작하며 거리를 계산하는 기능. StartFab 기능과 흡사함.


        startTimer() //중지했던 시간을 계속하여 측정한다.

        recordResumeFab.hide()
        recordUploadFab.hide()
        recordPauseFab.show()


    }

    private fun UploadFab() {
        //Todo: 다음 액티비티에 지금까지의 위도,경도를 가지고 계산한 거리, 시간, 속도를 인텐트로 넘겨준다.
        //startActivity<ResultActivity>()
    }

    private fun DetailsToMap() {
        //Todo: 이 함수가 불리면 현재 타임랩(시간,거리) 프래그먼트에서 맵 프래그먼트로 이동.

        supportFragmentManager.beginTransaction().hide(detailfr).commit()
        supportFragmentManager.beginTransaction().show(mapfr).commit()

        mapFab.show()
        detailsFab.hide()

    }

    private fun MapToDetails() {
        //Todo: 이 함수가 불리면 현재 맵 프래그먼트에서 타임랩(시간,거리) 프래그먼트로 이동.

        supportFragmentManager.beginTransaction().hide(mapfr).commit()
        supportFragmentManager.beginTransaction().show(detailfr).commit()

        detailsFab.show()
        mapFab.hide()
    }


    // 위치정보 요청 및 처리하는 작업은 배터리 소모가 크므로
    // 화면이 보이는 시점인 onResume에서 작업하고 화면이 안보이는 onPause에서 콜백 리스너 해제
    //TODO -> 그럼 해제하면 안되지 않는지? 확인해보기
    override fun onResume() {
        super.onResume()
        permissionCheck(cancel = { showPermissionInfoDialog() }, ok = { addLocationListener() })
    }
    override fun onPause() {    //onPause. 즉 액티비티가 보이지 않으면 실행되는 메소드.
        super.onPause()
        removeLocationListener()        //위치정보가 업데이트되지않음.
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

            //permission이 완료되었을경우 이때 MapFrgmt를 표시한다. 이유는 MapFrgmt안에 현재위치를 표시하는 메소드가 퍼미션이 되기전에 실행되어 에러가 나기때문에
            //supportFragmentManager.beginTransaction().replace(R.id.mainFrame, mapfr).commit() //<-잘못된 생각, floating button을 이용.

            supportFragmentManager.beginTransaction().add(R.id.mainFrame, mapfr).commit()
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

    private fun removeLocationListener() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
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


    //타이머 측정 함수
    private fun startTimer() {
        timeTask = timer(period = 10) {


            time++  //절대적 시간초.  <-Todo:이 time을 가지고 속도를 구할수 있다.
            var sec = time / 100 % 60
            var min = time / 6000 % 60
            var hour = time / 360000

            runOnUiThread {
                //UI를 갱신해주는 쓰레드
                secTextView.text = sec.toString()
                minTextView.text = min.toString()
                hourTextView.text = hour.toString()
            }
        }
    }

    private fun pauseTimer() {

        timeTask?.cancel()  //Timer의 객체로써 null일수 있기에 timetask 옆에 ? 붙음.
    }


    fun distanceCal(){



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



                if (recordStart) {

                    val arrayex = FloatArray(1)
                    distanceBetween(latitude, longitude, before_location[0]!!, before_location[1]!!, arrayex)

                    total_distance += arrayex[0]
                    println(total_distance)

                    //insertLatlng(latitude,longitude)
                    polylineOptions.add(latLng)
                    polylineOptions.width(13f)
                    polylineOptions.visible(true)   // 선이 보여질지/안보여질지 옵션. startBtn시 visible을 True로 하고 visibile이 True시 Realm에 데이터 저장.

                    mMap?.addPolyline(polylineOptions)


                }
            }

            if(recordPressed) {
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
}


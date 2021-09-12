package com.example.weatherapiproject.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapiproject.R
import com.example.weatherapiproject.adapter.RecyclerViewAdapter
import com.example.weatherapiproject.databinding.ActivityMainBinding
import com.example.weatherapiproject.utils.MyEventBusEvents
import com.example.weatherapiproject.viewmodel.MainViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {


    val mainViewModel by viewModel<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    var locationManager:LocationManager? = null
    var locationListener:LocationListener? = null

    var sifirlamaYapildiMi = false
    var oncekiDegerLatt = 0.0
    var sonrakiDegerLatt = 0.0
    var oncekiDegerLong = 0.0
    var sonrakiDegerLong = 0.0

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("tag_flow", "MainActivity => onCreate")


        //ViewModeldeki değişkenin degeri bir kez hesaplanmalı
        mainViewModel.checkPermissionForLocation(this)


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun continueToActivity() {

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = MyLocationListener()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener!!
        )

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun requestPermission() {

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        Log.i("tag_permission", "Tüm izinler verildi")
                        continueToActivity()
                    } else {
                        // Tüm izinler geçmediyse eğer en az biri reddedilmiş demektir

                        if (p0!!.isAnyPermissionPermanentlyDenied) {
                            // eger bidaha gösterme denilen bir izin varsa doğrudan ayarlara yönlendirilmeli
                            Log.i("tag_permission", "İzinlerden birine birdaha gösterme denilmiş.")

                            AlertDialog.Builder(this@MainActivity)
                                .setTitle(getString(R.string.gps_kullanim_izni_gerekli))    //text : "GPS Kullanım İzni Gerekli !"
                                .setMessage(getString(R.string.programi_kullanmaya_devam))    //text: "Programı kullanmaya devam etmek için Ayalar kısmından manuel olarak izin vermeniz gerekli"
                                .setPositiveButton(
                                    getString(R.string.ayarlara_git)  //text : "Ayarlara Git"
                                ) { dialog, which ->
                                    dialog!!.cancel()
                                    val intent =
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package", packageName, null)
                                    intent.data = uri
                                    startActivity(intent)
//                                            finish()
                                }
                                .setNegativeButton(
                                    getString(R.string.iptal) //text: "İptal"
                                ) { dialog, which ->
                                    dialog!!.cancel()
                                    finish()
                                }
                                .setCancelable(false)
                                .show()

                        } else {
                            // yok eğer bir izine red verilmişse, dialog ile tekrar hatıraltma yapabiliriz

                            AlertDialog.Builder(this@MainActivity)
                                .setTitle(getString(R.string.gps_kullanim_izni_gerekli))    //text : "GPS Kullanım İzni Gerekli !"
                                .setMessage(getString(R.string.lokasyon_bilignize_ihtiyac))     //text: "Programı kullanmaya devam etmek için lokasyon bilginize ihtiyacımız var."
                                .setPositiveButton(
                                    getString(R.string.izin_ver)    //text: "İzin Ver"
                                ) { dialog, which ->
                                    dialog!!.cancel()
                                    requestPermission()
                                }
                                .setNegativeButton(
                                    getString(R.string.iptal) //text: "İptal"
                                ) { dialog, which ->
                                    dialog!!.cancel()
                                    finish()
                                }
                                .setCancelable(false)
                                .show()


                        }

                    }


                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?, p1: PermissionToken?
                ) {
                    Log.i("tag_permisson", "izinlerden biri reddedildi")
                    p1!!.continuePermissionRequest() // her defasında yinede sorar
//                    p1!!.cancelPermissionRequest() // bidaha hiç bir zaman sormaz

                }

            })
            .withErrorListener { p0 -> Log.i("tag_permisson", "Error : ${p0.toString()}") }
            .check()

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.i("tag_flow", "MyLocationListener => onLocationChanged")

            // ilk calıstıgında eski konum bilgisini gonderiyor.
            // Yanlış oluyor.Bu yüzden hafızada kalan veriyi sıfıra eşitliyorum öncelikle
            if(!sifirlamaYapildiMi){
                Log.i("tag_location", "en sonki veri sıfırlandı")
                location.latitude = 0.0
                location.longitude = 0.0
                sifirlamaYapildiMi = true
            }


            binding.tvLatt.text = location.latitude.toString()
            binding.tvLong.text = location.longitude.toString()


            if(location.latitude != 0.0 && location.longitude != 0.0){

                oncekiDegerLatt = sonrakiDegerLatt
                oncekiDegerLong = sonrakiDegerLong
                sonrakiDegerLatt = location.latitude
                sonrakiDegerLong = location.longitude
                if(sonrakiDegerLatt-oncekiDegerLatt<0.1 && sonrakiDegerLong-oncekiDegerLong<0.1){
                    Log.i("tag_location","lokasyonlar ust uste benzer geldi ve sıfır degiller. yaklasik konum alinabilir")
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.ivCheck.visibility = View.VISIBLE
                    binding.textView.text = getString(R.string.konum_bilgisi_bulundu)   //text: "Konum Bilgisi Bulundu"

                    locationManager!!.removeUpdates(locationListener!!) // metot tekrar cagrılmasını engellemek ıcın

                    Log.i(
                        "tag_location",
                        "lokasyon tespit edildi. Yonlendiriliyor....."
                    )

                    mainViewModel.goToSecondActivityWithLattLong(
                        this@MainActivity,
                        location.latitude.toString(),
                        location.longitude.toString()
                    )


                }

            }


        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(getApplicationContext(), getString(R.string.gps_kapatildi), Toast.LENGTH_SHORT).show()
            Log.i("tag_flow", "MyLocationListener => onProviderDisabled")
        }

        override fun onProviderEnabled(provider: String) {
            Toast.makeText(getApplicationContext(), getString(R.string.gps_acildi), Toast.LENGTH_SHORT).show()
            Log.i("tag_flow", "MyLocationListener => onProviderEnabled")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.i("tag_flow", "MyLocationListener => onProviderEnabled")
//            Not : Api 29 sonra deprecate edildi . bu metot çagrılmıyor artık. ici bos kalacak
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onStart() {
        super.onStart()
        Log.i("tag_flow", "MainActivity => onStart")

        // öncelikle GPS izini gözlemlenmeli
        observeLiveDataAboutGPS()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(locationListener != null) locationListener = null
        if(locationManager != null) locationManager = null



    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun observeLiveDataAboutGPS() {

        mainViewModel.GPSizni.observe(this, Observer { boolean ->

            boolean?.let {
                if (boolean) continueToActivity()
                else requestPermission()
            }

        })


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}
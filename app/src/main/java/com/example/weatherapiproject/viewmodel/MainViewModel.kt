package com.example.weatherapiproject.viewmodel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapiproject.model.Parent
import com.example.weatherapiproject.service.WeatherAPI
import com.example.weatherapiproject.service.WeatherAPI.Companion.BASE_URL
import com.example.weatherapiproject.utils.MyEventBusEvents
import com.example.weatherapiproject.view.MainActivity
import com.example.weatherapiproject.view.SecondActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel(){


    ////////////////////////////////////////////////////////////////////////////////////////////////

    val GPSizni: MutableLiveData<Boolean> by lazy() {
        MutableLiveData<Boolean>()
    }

    // Lokasyon izni kontrol etme
    fun checkPermissionForLocation(context: Context){

        GPSizni.value = (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED
//                &&        //şimdilik arka plandaki izne ihtiyac yok, zaten Q öncesi izni coarse yerine background istenilmeli, araştır
//                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED
                )

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    var MAIN_HANDLER: Handler? = Handler(Looper.getMainLooper())
    fun goToSecondActivityWithLattLong(mainActivity: MainActivity, latt: String, long: String){

        EventBus.getDefault().postSticky(MyEventBusEvents.lattVeLongDegerleriniGonder(latt, long))
        MAIN_HANDLER!!.postDelayed(Runnable {
            val intent = Intent(mainActivity, SecondActivity::class.java)
            mainActivity.startActivity(intent)
            mainActivity.finish()
        }, 2000) // Tesbit edilen konum ekrana yazdırıldı, kullanıcı görebilsin diye 2 sn bekleme verildi

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}


package com.example.weatherapiproject.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapiproject.model.Parent
import com.example.weatherapiproject.service.WeatherAPI
import com.example.weatherapiproject.utils.MyEventBusEvents
import com.example.weatherapiproject.view.MainActivity
import com.example.weatherapiproject.view.SecondActivity
import com.example.weatherapiproject.view.ThirdActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class SecondViewModel : ViewModel() {

    private val disposable = CompositeDisposable()

    val dataSuccess = MutableLiveData<List<Parent>>()
    val dataError = MutableLiveData<Boolean>()
    val dataLoading = MutableLiveData<Boolean>()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun refreshdata(latitude:String, longitude:String){

//        getParentsFromApiIncludeText("ankara") // test amacli
        getParentsFromApiMostClose("$latitude,$longitude")

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun getParentsFromApiMostClose(lattLong : String){

        dataLoading.value= true

        val api = Retrofit.Builder()
            .baseUrl(WeatherAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(WeatherAPI::class.java)

        disposable.add(
            api.getLocationsMostClose("location/search/?lattlong=$lattLong")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Parent>>(){
                    override fun onSuccess(t: List<Parent>) {
                        dataSuccess.value=t
                        dataError.value=false
                        dataLoading.value=false

                    }

                    override fun onError(e: Throwable) {
                        dataLoading.value=false
                        dataError.value=true
                        e.printStackTrace()

                    }

                })
        )

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun getParentsFromApiIncludeText(includeText : String){

        dataLoading.value= true

        val api = Retrofit.Builder()
            .baseUrl(WeatherAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(WeatherAPI::class.java)

        disposable.add(
            api.getLocationsIncludeText("location/search/?query=$includeText")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Parent>>(){
                    override fun onSuccess(t: List<Parent>) {
                        dataSuccess.value=t
                        dataError.value=false
                        dataLoading.value=false

                    }

                    override fun onError(e: Throwable) {
                        dataLoading.value=false
                        dataError.value=true
                        e.printStackTrace()

                    }

                })
        )

    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun goToThirdActivityWithWoeid(context: Context, woeid:String, title:String){

        EventBus.getDefault().postSticky(MyEventBusEvents.woeidDegeriniGonder(woeid,title))
        val intent = Intent(context, ThirdActivity::class.java)
        context.startActivity(intent)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun backPressEvent(secondActivity: SecondActivity) {

        val intent = Intent(secondActivity, MainActivity::class.java)
        secondActivity.startActivity(intent)
        secondActivity.finish()

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun performSearch(includeText : String){

        // api search islemi
        getParentsFromApiIncludeText(includeText)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}
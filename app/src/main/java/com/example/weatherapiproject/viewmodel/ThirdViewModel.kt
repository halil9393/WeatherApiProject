package com.example.weatherapiproject.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapiproject.model.ConsolidatedWeather
import com.example.weatherapiproject.model.WeatherModel
import com.example.weatherapiproject.service.WeatherAPI
import com.example.weatherapiproject.service.WeatherAPI.Companion.BASE_URL
import com.example.weatherapiproject.view.MainActivity
import com.example.weatherapiproject.view.SecondActivity
import com.example.weatherapiproject.view.ThirdActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ThirdViewModel : ViewModel() {

    private val disposable = CompositeDisposable()

    val dataSuccess = MutableLiveData<List<ConsolidatedWeather>>()
    val dataError = MutableLiveData<Boolean>()
    val dataLoading = MutableLiveData<Boolean>()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun refreshdata(woeid: String) {

        getWeatherOfLocationFromApi(woeid)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun getWeatherOfLocationFromApi(woeid: String) {

        dataLoading.value = true

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(WeatherAPI::class.java)

        disposable.add(
            api.getWeatherOfLocation("location/$woeid")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherModel>() {
                    override fun onSuccess(t: WeatherModel) {
                        dataSuccess.value = t.consolidatedWeather
                        dataError.value = false
                        dataLoading.value = false

                    }

                    override fun onError(e: Throwable) {
                        dataLoading.value = false
                        dataError.value = true
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

    fun backPressEvent(thirdActivity: ThirdActivity) {

        val intent = Intent(thirdActivity, SecondActivity::class.java)
        thirdActivity.startActivity(intent)
        thirdActivity.finish()

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}
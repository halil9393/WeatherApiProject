package com.example.weatherapiproject.service

import com.example.weatherapiproject.model.Parent
import com.example.weatherapiproject.model.WeatherModel
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface WeatherAPI {

    companion object{

        const val BASE_URL = "https://www.metaweather.com/api/"

    }

//    @GET("location/44418")
//    @GET
//    suspend fun getWeatherOfLocation(@Url woeid:String): Call<WeatherModel>

//    @GET  // bunu coroutine le beraber kullanırken yapıoruz
//    suspend fun getWeatherOfLocation(@Url woeid:String): Response<WeatherModel>

//    @GET
//    fun getLocationsMostClose(@Url latt_long:String): Call<List<Parent>>

//    @GET
//    fun getLocationsIncludeText(@Url text:String): Call<List<Parent>>

    @GET
    fun getWeatherOfLocation(@Url woeid:String): Single<WeatherModel>

    @GET
    fun getLocationsMostClose(@Url latt_long:String): Single<List<Parent>>

    @GET
    fun getLocationsIncludeText(@Url text:String): Single<List<Parent>>


}
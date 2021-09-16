package com.example.weatherapiproject.service

import com.example.weatherapiproject.model.Parent
import com.example.weatherapiproject.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface WeatherAPI {

    companion object {

        const val BASE_URL = "https://www.metaweather.com/api/"

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

//    // Normal retrofit cagrisinda call kullanıyoruz
//    @GET
//    fun getWeatherOfLocation(@Url woeid:String): Call<WeatherModel>
//
//    @GET
//    fun getLocationsMostClose(@Url latt_long:String): Call<List<Parent>>
//
//    @GET
//    fun getLocationsIncludeText(@Url text:String): Call<List<Parent>>

    ///////////////////////////////////////////////////////////////////////////////////////////////

//    // Live Data ile kullanılırken Single kullanılıyor
//    @GET
//    fun getWeatherOfLocation(@Url woeid:String): Single<WeatherModel>
//
//    @GET
//    fun getLocationsMostClose(@Url latt_long:String): Single<List<Parent>>
//
//    @GET
//    fun getLocationsIncludeText(@Url text:String): Single<List<Parent>>

    ///////////////////////////////////////////////////////////////////////////////////////////////

    // Coroutines ile kullanılırken suspend ekliyoruz ve Response donuyoruz
    @GET
    suspend fun getWeatherOfLocation(@Url woeid: String): Response<WeatherModel>

    @GET
    suspend fun getLocationsMostClose(@Url latt_long:String): Response<List<Parent>>

    @GET
    suspend fun getLocationsIncludeText(@Url text: String): Response<List<Parent>>
}
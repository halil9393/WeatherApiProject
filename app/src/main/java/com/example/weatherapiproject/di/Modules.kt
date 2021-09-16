package com.example.weatherapiproject

import com.example.weatherapiproject.service.WeatherAPI
import com.example.weatherapiproject.viewmodel.MainViewModel
import com.example.weatherapiproject.viewmodel.SecondViewModel
import com.example.weatherapiproject.viewmodel.ThirdViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val appModule = module {

//        single { WeatherAPI }

}


val viewModelModules = module {
    viewModel { MainViewModel() }

    viewModel { SecondViewModel(get()) }

    viewModel { ThirdViewModel(get()) }

}

val networkModule = module {
    factory { provideRetrofit() }
    single { provideNetworkApi(get()) }
}

fun provideRetrofit(): Retrofit {
//    return Retrofit.Builder()
//        .baseUrl(NetworkConstant.BASE_URL)
//        .addConverterFactory(MoshiConverterFactory.create())
//        .client(OkHttpClient.Builder().build())
//        .build()

    return Retrofit.Builder()
        .baseUrl(WeatherAPI.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()


}

fun provideNetworkApi(retrofit: Retrofit): WeatherAPI =
    retrofit.create(WeatherAPI::class.java)
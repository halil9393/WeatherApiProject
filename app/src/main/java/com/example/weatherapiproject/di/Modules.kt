package com.example.weatherapiproject

import com.example.weatherapiproject.viewmodel.MainViewModel
import com.example.weatherapiproject.viewmodel.SecondViewModel
import com.example.weatherapiproject.viewmodel.ThirdViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

//        single { MainViewModel() }

}


val viewModelModules = module {
    viewModel { MainViewModel() }

    viewModel { SecondViewModel() }

    viewModel { ThirdViewModel() }
}
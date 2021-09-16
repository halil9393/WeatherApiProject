package com.example.weatherapiproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapiproject.model.WeatherModel
import com.example.weatherapiproject.service.WeatherAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ThirdViewModel(private val weatherAPI: WeatherAPI) : ViewModel() {

    // LiveData kullanılarak yapılırsa =>

//    private val disposable = CompositeDisposable()
//    val dataSuccess = MutableLiveData<List<ConsolidatedWeather>>()
//    val dataError = MutableLiveData<Boolean>()
//    val dataLoading = MutableLiveData<Boolean>()
//
//
//    private fun getWeatherOfLocationFromApi(woeid: String) {
//
//        dataLoading.value = true
//
//        val api = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .build()
//            .create(WeatherAPI::class.java)
//
//        disposable.add(
//            api.getWeatherOfLocation("location/$woeid")
//                .subscribeOn(Schedulers.newThread())      // burası icin retrofit donus tipini degis!!
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(object : DisposableSingleObserver<WeatherModel>() {
//                    override fun onSuccess(t: WeatherModel) {
//                        dataSuccess.value = t.consolidatedWeather
//                        dataError.value = false
//                        dataLoading.value = false
//
//                    }
//
//                    override fun onError(e: Throwable) {
//                        dataLoading.value = false
//                        dataError.value = true
//                        e.printStackTrace()
//
//                    }
//
//                })
//        )
//
//
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        disposable.clear()
//    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Flow ile yapılırsa =>

    private val _dataListState = MutableStateFlow<UiState>(UiState.Empty)
    var dataListState: StateFlow<UiState> = _dataListState

    sealed class UiState {

        object Empty : UiState()

        class Success(val weather: WeatherModel) : UiState()
        class Fail(val exeption: Exception) : UiState()
        class Loading(val showLoading: Boolean) : UiState()
    }

    private fun getWeatherOfLocationFromApi(woeid: String) {

        viewModelScope.launch(Dispatchers.IO) {

            _dataListState.value = UiState.Loading(true)

            try {
                val response = weatherAPI.getWeatherOfLocation("location/$woeid")
                _dataListState.value = UiState.Success(response.body()!!)
            } catch (exception: Exception) {
                _dataListState.value = UiState.Fail(exception)
            }


        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private val _backPressState = MutableStateFlow<Boolean>(false)
    var backPressState: StateFlow<Boolean> = _backPressState

    fun backPressEvent() {

        _backPressState.value = true

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun refreshdata(woeid: String) {

        getWeatherOfLocationFromApi(woeid)

    }
}
package com.example.weatherapiproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapiproject.model.Parent
import com.example.weatherapiproject.service.WeatherAPI
import com.example.weatherapiproject.utils.MyEventBusEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class SecondViewModel(private val weatherAPI: WeatherAPI) : ViewModel() {

    // LiveData kullanılarak yapılırsa =>

//    private val disposable = CompositeDisposable()
//    val dataSuccess = MutableLiveData<List<Parent>>()
//    val dataError = MutableLiveData<Boolean>()
//    val dataLoading = MutableLiveData<Boolean>()
//
//
//    private fun getParentsFromApiMostClose(lattLong : String){
//
//        dataLoading.value= true
//
//        val api = Retrofit.Builder()
//            .baseUrl(WeatherAPI.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .build()
//            .create(WeatherAPI::class.java)
//
//        disposable.add(
//            api.getLocationsMostClose("location/search/?lattlong=$lattLong")
//                .subscribeOn(Schedulers.newThread())                   // burası icin retrofit donus tipini degis!!
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(object : DisposableSingleObserver<List<Parent>>(){
//                    override fun onSuccess(t: List<Parent>) {
//                        dataSuccess.value=t
//                        dataError.value=false
//                        dataLoading.value=false
//
//                    }
//
//                    override fun onError(e: Throwable) {
//                        dataLoading.value=false
//                        dataError.value=true
//                        e.printStackTrace()
//
//                    }
//
//                })
//        )
//
//    }
//
//    private fun getParentsFromApiIncludeText(includeText : String){
//
//        dataLoading.value= true
//
//        val api = Retrofit.Builder()
//            .baseUrl(WeatherAPI.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .build()
//            .create(WeatherAPI::class.java)
//
//        disposable.add(
//            api.getLocationsIncludeText("location/search/?query=$includeText")// burası icin retrofit donus tipini degis!!
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(object : DisposableSingleObserver<List<Parent>>(){
//                    override fun onSuccess(t: List<Parent>) {
//                        dataSuccess.value=t
//                        dataError.value=false
//                        dataLoading.value=false
//
//                    }
//
//                    override fun onError(e: Throwable) {
//                        dataLoading.value=false
//                        dataError.value=true
//                        e.printStackTrace()
//
//                    }
//
//                })
//        )
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

    private val _thirdActivityState = MutableStateFlow<Boolean>(false)
    var thirdActivityState: StateFlow<Boolean> = _thirdActivityState

    sealed class UiState {

        object Empty : UiState()

        class Success(val parents: List<Parent>) : UiState()
        class Fail(val exeption: Exception) : UiState()
        class Loading(val showLoading: Boolean) : UiState()
    }

    private fun getParentsFromApiMostClose(lattLong: String) {

        viewModelScope.launch(Dispatchers.IO) {

            _dataListState.value = UiState.Loading(true)

            try {
                val response =
                    weatherAPI.getLocationsMostClose("location/search/?lattlong=$lattLong")
                _dataListState.value = UiState.Success(response.body()!!)
            } catch (exception: Exception) {
                _dataListState.value = UiState.Fail(exception)
            }


        }
    }


    private fun getParentsFromApiIncludeText(includeText: String) {

        viewModelScope.launch(Dispatchers.IO) {

            _dataListState.value = UiState.Loading(true)

            try {
                val response = weatherAPI.getLocationsIncludeText("location/search/?query=$includeText")
                _dataListState.value = UiState.Success(response.body()!!)
            } catch (exception: Exception) {
                _dataListState.value = UiState.Fail(exception)
            }


        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun sehirSecildi(woeid: String, title: String) {

        EventBus.getDefault().postSticky(MyEventBusEvents.woeidDegeriniGonder(woeid, title))
        _thirdActivityState.value = true

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private val _backPressState = MutableStateFlow<Boolean>(false)
    var backPressState: StateFlow<Boolean> = _backPressState

    fun backPressEvent() {
        _backPressState.value = true
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun performSearch(includeText: String) {

        // api search islemi
        getParentsFromApiIncludeText(includeText)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun refreshdata(latitude: String, longitude: String) {

        getParentsFromApiMostClose("$latitude,$longitude")

    }


}
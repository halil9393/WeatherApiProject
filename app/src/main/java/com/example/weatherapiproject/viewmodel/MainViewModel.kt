package com.example.weatherapiproject.viewmodel


import androidx.lifecycle.ViewModel
import com.example.weatherapiproject.utils.MyEventBusEvents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.greenrobot.eventbus.EventBus

class MainViewModel : ViewModel() {


    private val _secondActivityState = MutableStateFlow<Boolean>(false)
    var secondActivityState: StateFlow<Boolean> = _secondActivityState

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun konumBulundu(latt: String, long: String) {
        // gelen konumu EventBus ile yayın yapıyoruz, 2. aktivity de kullanacagiz
        EventBus.getDefault().postSticky(MyEventBusEvents.lattVeLongDegerleriniGonder(latt, long))

        _secondActivityState.value = true

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


}


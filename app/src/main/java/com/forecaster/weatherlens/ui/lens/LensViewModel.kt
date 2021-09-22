package com.forecaster.weatherlens.ui.lens

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forecaster.weatherlens.data.Forecast
import com.forecaster.weatherlens.network.NetworkService
import com.forecaster.weatherlens.network.viewmodelservice.OWMService
import kotlinx.coroutines.launch

class LensViewModel: ViewModel() {

    var weatherData: MutableLiveData<Forecast> = MutableLiveData()

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            val service = NetworkService(URL).retrofit
            val owmService: OWMService by lazy {
                service.create(OWMService::class.java)
            }
            val data = owmService.getForecastData("onecall?lat=${LensFragment.location.latitude}" +
                    "&lon=${LensFragment.location.longitude}&appid=${API_KEY}&exclude=current,hourly&cnt=16" +
                    "&units=${if(LensFragment.METRIC) "metric" else "imperial"}")
            val biggerData = mutableListOf<Forecast.DailyForecast>()
            data.daily?.let {  daily ->
                repeat(7){
                    biggerData.addAll(daily)
                }
            }
            data.daily = biggerData
            weatherData.value = data
        }
    }

    companion object {
        private const val API_KEY = "82476bb8ad65dfc3ea8df29cf0b2b5a6"
        private const val URL: String = "https://api.openweathermap.org/data/2.5/"

    }
}
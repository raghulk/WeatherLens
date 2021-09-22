package com.forecaster.weatherlens.network.viewmodelservice

import com.forecaster.weatherlens.data.Forecast
import retrofit2.http.GET
import retrofit2.http.Url

interface OWMService {
    @GET
    suspend fun getForecastData(@Url url: String): Forecast

}


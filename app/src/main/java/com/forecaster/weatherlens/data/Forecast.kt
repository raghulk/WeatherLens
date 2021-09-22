package com.forecaster.weatherlens.data

import com.squareup.moshi.Json

data class Forecast(
    val lat: Double?,
    val lon: Double?,
    val timezone: String?,
    @Json(name = "timezone_offset")
    val timezoneOffset: Int?,
    var daily: List<DailyForecast>?,
) {
    data class DailyForecast(
        val dt: Long?,
        val sunrise: Long?,
        val sunset: Long?,
        val moonrise: Long?,
        @Json(name = "moonset")
        val moonSet: Long?,
        @Json(name = "moon_phase")
        val moonPhase: Double?,
        val temp: Temperature?,
        @Json(name = "feels_like")
        val feelsLike: FeelsLike?,
        val pressure: Int?,
        val humidity: Int?,
        @Json(name = "dew_point")
        val dewPoint: Double?,
        @Json(name = "wind_speed")
        val windSpeed: Double?,
        @Json(name = "wind_deg")
        val windDeg: Int?,
        @Json(name = "wind_gust")
        val windGust: Double?,
        val weather: List<Weather>?,
        val clouds: Int?,
        val pop: Double?,
        val rain: Double?,
        val uvi: Double?,
    ) {
        data class Temperature(
            val day: Double?,
            val night: Double?,
            val eve: Double?,
            val morn: Double?,
            val min: Double?,
            val max: Double?,
        )
        data class FeelsLike(
            val day: Double?,
            val night: Double?,
            val eve: Double?,
            val morn: Double?,
        )
        data class Weather(
            val id: Int?,
            val main: String?,
            val description: String?,
            val icon: String?,
        )
    }
}


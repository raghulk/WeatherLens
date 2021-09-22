package com.forecaster.weatherlens.ui.lens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.forecaster.weatherlens.R
import com.forecaster.weatherlens.data.Forecast
import com.forecaster.weatherlens.ui.ad.AdHelper
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class LensAdapter(
    private var weather: MutableList<Any> = mutableListOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        return when(viewType) {
            0, 1, 2 -> {
                CardViewHolder(inflater.inflate(R.layout.lens_card_item, parent, false))
            }
            else -> {
                AdViewHolder(inflater.inflate(R.layout.lens_ad_item, parent, false))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position % 4
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0, 1, 2 -> {
                val dailyWeather = weather[position] as Forecast.DailyForecast
                val calcTemperature = getTemperature(dailyWeather.temp)
                val sharedPref = (context as Activity).getPreferences(Context.MODE_PRIVATE)
                var metrics = false
                sharedPref?.let {
                    metrics = it.getBoolean(context.getString(R.string.metric), false)
                }
                (holder as CardViewHolder).apply {
                    date.text = setDate(dailyWeather.dt)
                    temperature.text = "${calcTemperature}°${if(metrics) Units.METRIC.temp else Units.IMPERIAL.temp}" // create enum
                    description.text =
                        dailyWeather.weather?.first()?.description.toString().capitalizeWords()
                    humidity.text = "${dailyWeather.humidity.toString()}%"
                    pressure.text = "${dailyWeather.pressure.toString()} ${if(metrics) Units.METRIC.pressure else Units.IMPERIAL.pressure}"
                    uv.text = dailyWeather.uvi.toString()
                    setBackground(card, temperature, description.text.toString())
                }
            }
            3 -> {
//                (holder as CardViewHolder).card.visibility = View.GONE
                AdHelper(context).loadAds(holder as AdViewHolder)
            }
        }
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }

    private fun setBackground(card: CardView, temp: TextView, keyWords: String) {
        val imgUrl = getImageUrl(keyWords)
        Glide.with(context)
            .asDrawable()
            .load(imgUrl)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(16)))
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    card.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDate(timestamp: Long?): String {
        return try {
            val dateFormat = SimpleDateFormat("dd MMM y")
            val date = LocalDateTime.ofInstant(
                timestamp?.let { Instant.ofEpochSecond(it) },
                ZoneId.systemDefault()
            )
            dateFormat.format(Date.from(date.atZone(ZoneId.systemDefault()).toInstant())).toString()
        } catch (e: Exception) {
            ""
        }
    }

    private fun getTemperature(temperature: Forecast.DailyForecast.Temperature?): Int {
        return try {
            val avg = (temperature?.min!! + temperature.max!!) / 2F
            avg.toInt()
        } catch (e: Exception) {
            23
        }
    }

    override fun getItemCount(): Int {
        return weather.size
    }

    fun setWeather(weather: MutableList<Any>?) {
        weather?.let {
            this.weather = it
        }
    }

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.card)
        val temperature: TextView = view.findViewById(R.id.temperature)
        val date: TextView = view.findViewById(R.id.forecast_date)
        val description: TextView = view.findViewById(R.id.description)
        val humidity: TextView = view.findViewById(R.id.humidity_value)
        val pressure: TextView = view.findViewById(R.id.pressure_value)
        val uv: TextView = view.findViewById(R.id.uv_value)
    }

    class AdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val adFrame: FrameLayout = view.findViewById(R.id.ad_frame)
    }

    private fun getImageUrl(keyWords: String): String {
        return "$UNSPLASH_BASE_URL$keyWords&${Math.random()}" // to get new images everytime and to prevent caching from glide
    }

    companion object {
        private const val UNSPLASH_BASE_URL = "https://source.unsplash.com/600x800/?"
    }

    enum class Units(val temp: String, val pressure: String){
        IMPERIAL("F", "hPa"),
        METRIC("C", "hPa")
    }
}
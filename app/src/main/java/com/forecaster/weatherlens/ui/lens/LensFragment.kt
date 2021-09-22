package com.forecaster.weatherlens.ui.lens

import android.app.WallpaperManager
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.forecaster.weatherlens.R
import com.forecaster.weatherlens.data.Forecast
import com.forecaster.weatherlens.databinding.FragmentLensBinding
import com.forecaster.weatherlens.ui.ad.AdItem
import com.google.android.gms.ads.MobileAds
import com.yuyakaido.android.cardstackview.*

class LensFragment : Fragment(), CardStackListener {

    private lateinit var lensViewModel: LensViewModel
    private lateinit var cardStackView: CardStackView
    private lateinit var manager: CardStackLayoutManager
    private lateinit var listSize: Any
    private var _binding: FragmentLensBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        sharedPref?.let {
            METRIC = it.getBoolean(getString(R.string.metric), false)
        }

        MobileAds.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lensViewModel =
            ViewModelProvider(this).get(LensViewModel::class.java)

        _binding = FragmentLensBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cardStackView = binding.weatherCardsStack
        val lensAdapter = LensAdapter()
        cardStackView.adapter = lensAdapter
        manager = CardStackLayoutManager(requireContext(), this)
        manager.setSwipeableMethod(SwipeableMethod.Manual)
        manager.setCanScrollVertical(false)
        manager.setCanScrollHorizontal(true)
        manager.setDirections(Direction.HORIZONTAL)
        cardStackView.layoutManager = manager


        lensViewModel.weatherData.observe(viewLifecycleOwner, {
            it.daily?.let { dailyWeather ->
                val list = prepareList(dailyWeather.toMutableList())
                lensAdapter.setWeather(list)
                listSize = list.size
                lensAdapter.notifyItemRangeChanged(0, listSize as Int - 1)
            }
        })

        binding.setWallpaper.setOnClickListener{
            val img = (cardStackView.findViewHolderForAdapterPosition(manager.topPosition) as LensAdapter.CardViewHolder).card.background
            val wallpaperManager = WallpaperManager.getInstance(context)
            wallpaperManager.setBitmap(img.toBitmap())

        // Different ways to access the adapter view
//            Log.d("LensFragment", cardStackView.get(manager.topPosition).findViewById<TextView>(R.id.temperature).tag.toString())
//        (cardStackView.findViewHolderForAdapterPosition(manager.topPosition) as LensAdapter.CardViewHolder).card.tag
//        manager.findViewByPosition(manager.topPosition).findViewById<>()
//        (manager.findViewByPosition(manager.topPosition) as CardStackView)?

    }
        return root
    }

    private fun prepareList(dailyForecast: MutableList<Forecast.DailyForecast>): MutableList<Any> {
        val dataset: MutableList<Any> = mutableListOf()
        dataset.addAll(dailyForecast)
        val adItem = AdItem()
        repeat(dailyForecast.size / 4) {
            if (it > 0) {
                dataset.add(4 * it - 1, adItem)
            }
        }
        return dataset
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {
        when(direction){
            Direction.Left -> {
                cardStackView.rewind()
                checkAndSetVisibilityOfWallpaperButton(manager.topPosition)
            }
            Direction.Right -> {
                cardStackView.swipe()
                checkAndSetVisibilityOfWallpaperButton(manager.topPosition)
            }
            Direction.Top -> {
                cardStackView.swipe()
                checkAndSetVisibilityOfWallpaperButton(manager.topPosition)
            }
            Direction.Bottom -> {
                cardStackView.rewind()
                checkAndSetVisibilityOfWallpaperButton(manager.topPosition)
            }
        }
    }

    private fun checkAndSetVisibilityOfWallpaperButton(position: Int) {
        if((position + 1) % 4 == 0 || listSize as Int == position){
            binding.setWallpaper.visibility = View.GONE
        }
        else binding.setWallpaper.visibility = View.VISIBLE
    }

    override fun onCardRewound() {
        cardStackView.rewind()
    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }

    companion object {
        var METRIC = false
        var location: Location = Location("")
    }
}


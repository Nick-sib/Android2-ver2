package com.nickolay.android2ver2.main

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nickolay.android2ver2.R
import com.nickolay.android2ver2.model.GlobalViewModel
import com.nickolay.android2ver2.model.WeatherData
import com.nickolay.android2ver2.service.CommonWeather
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.city_weather.*


class CityWeather : Fragment()/*, OnItemListClick*/ {

    lateinit var viewModel: GlobalViewModel

    var currID = 0
    val sHumidity : String by lazy { resources.getString(R.string.t_humidity) }
    val sWind : String by lazy { resources.getString(R.string.t_wind) }
    private lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(activity!!).get(GlobalViewModel::class.java)
        viewModel.weatherData.observe(
            this,
            androidx.lifecycle.Observer {
                compliteData(it)
            })

        currID = Bundle()
            .getInt(ARG_SECTION_NUMBER, 0)//GlobalViewModel.DEFAULT_ID)
        Log.d("myLOG", "onCreate: currID = $currID")
        CommonWeather.getData(currID, viewModel)
    }



    private fun compliteData(data: WeatherData) {
        currID = data.id
        Log.d("myLOG", "compliteData: currID = $currID")
        this.apply {
            arguments = Bundle().apply {
                putInt(ARG_SECTION_NUMBER, currID)
            }
        }
        val uri = Uri.parse("https://openweathermap.org/img/wn/${data.icon}@4x.png")
        Picasso.get()!!
            .load(uri)
            .into(root.findViewById<ImageView>(R.id.ivCloudiness), object : Callback {
                override fun onSuccess() {
                    Log.d("myLOG", "success")
                }

                override fun onError(e: Exception?) {
                    Log.d("myLOG", "error")
                    Log.d("myLOG", e.toString())
                }
            })
        tvCityName.text = data.cityName
        tvDayOfWeek.text = data.dayWeek
        tvCloudiness.text = data.overcast
        tvTemperature.text = data.temp.toString()
        tvHumidity.text = String.format(sHumidity, data.humidity, "%")
        tvWind.text = String.format(sWind, data.wind)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.city_weather, container, false)


        return root
    }


    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        @JvmStatic
        fun newInstance(sectionNumber: Int): CityWeather {
            return CityWeather().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

//    override fun onSelectItem(weatherData: WeatherData) {
//        if (weatherData.isLoaded) {
//            compliteData(weatherData)
//        } else CommonWeather.getData(this, Handler(), weatherData.id)
//
//    }

}
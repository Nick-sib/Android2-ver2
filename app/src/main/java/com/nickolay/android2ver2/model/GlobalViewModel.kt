package com.nickolay.android2ver2.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nickolay.android2ver2.adapter.CityListAdapter

import com.nickolay.android2ver2.database.CityData
import com.nickolay.android2ver2.database.CityDataRepository
import com.nickolay.android2ver2.database.CityDataRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GlobalViewModel (application: Application) : AndroidViewModel(application) {
    private val dataRepository: CityDataRepository
    val databaseCitys: LiveData<List<CityData>>

    val adapter = CityListAdapter()
    val weatherData = MutableLiveData<WeatherData>()

    init {
        val cityDataDao = CityDataRoomDatabase.getDatabase(application, viewModelScope).cityDataDao()
        dataRepository =
            CityDataRepository(cityDataDao)
        databaseCitys = dataRepository.allCitys
//        Log.d("myLOG", "${allCitys.value!!.size}: ")
    }


    private fun insertWeatherData(cityData: CityData) = viewModelScope.launch(Dispatchers.IO) {
        dataRepository.insert(cityData)
    }

    fun setWeatherData(weatherRequest: WeatherRequest) {
        //сохраняем результат запроса в БД
        insertWeatherData(CityData(weatherRequest.id, weatherRequest.name, weatherRequest.main.temp, Date().time))

        val today = Calendar.getInstance().time
        weatherData.value = adapter.setData(weatherRequest, SimpleDateFormat("EEEE").format(today).capitalize())
    }

    companion object {
        const val DEFAULT_ID = 1496747
    }
}
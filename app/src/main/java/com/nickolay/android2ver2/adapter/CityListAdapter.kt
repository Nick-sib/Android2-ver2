package com.nickolay.android2ver2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nickolay.android2ver2.R
import com.nickolay.android2ver2.adapters.CityViewHolder
import com.nickolay.android2ver2.adapters.OnItemListClick
import com.nickolay.android2ver2.database.CityData
import com.nickolay.android2ver2.model.WeatherData
import com.nickolay.android2ver2.model.WeatherRequest


import java.util.*
import kotlin.collections.ArrayList


class CityListAdapter: RecyclerView.Adapter<CityViewHolder>() {
    private val errorData = WeatherData("Request",0)

    private var databaseList = emptyList<CityData>()
    private var workList: ArrayList<WeatherData> = arrayListOf()
    private var fullList: ArrayList<WeatherData> = ArrayList(workList)

    private var onItemListClickListener: OnItemListClick? = null

    private var showedCity = -1

    fun setLists(inArray: List<String>, defaultId: Int){
        if (workList.size == 0) {
            for (inData in inArray){
                val s = inData.split(',')
                workList.add(WeatherData(s[0], s[1].toInt()))
                workList[workList.size-1].apply {
                    isCheck = id == defaultId
                    tmpCheck = isCheck
                }
            }
            fullList = ArrayList(workList)}
    }

    fun setData(request: WeatherRequest, dayWeek: String): WeatherData {
        if (fullList.size == 0) {
            val data = WeatherData("Новосибирск", request.id)
            //это быстрая заглушка для пустого листа
                data.isLoaded = true
                data.dayWeek = dayWeek
                data.overcast = request.weather[0].description.capitalize()
                data.temp = request.main.temp.toInt()
                data.humidity = request.main.humidity
                data.wind = request.wind.speed
                data.icon = "${request.weather[0].icon}"
                return data
        }
        for (data in fullList)
            if (data.id == request.id) {
                data.isLoaded = true
                data.dayWeek = dayWeek
                data.overcast = request.weather[0].description.capitalize()
                data.temp = request.main.temp.toInt()
                data.humidity = request.main.humidity
                data.wind = request.wind.speed
                data.icon = "${request.weather[0].icon}"
                return data
            }
        return errorData
    }

    fun applyFilter(value: String) {
        workList =
            if (value.length > 1)
                fullList.filter {
                    it.cityName.toLowerCase(Locale.ROOT).contains(value.toLowerCase(Locale.ROOT))
                } as ArrayList<WeatherData> else ArrayList(fullList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.select_city_item, parent, false)
        return CityViewHolder(view, onItemListClickListener)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(workList[position])
    }

    override fun getItemCount(): Int = workList.size

    fun getFirst(): WeatherData {
        for (i in 0 until fullList.size) {
            if (fullList[i].isCheck) {
                showedCity = i
                return fullList[i]
            }
        }
        showedCity = -1
        return errorData
    }

    fun getNext(): WeatherData {
        for (i in (showedCity+1) until (fullList.size)){
            if (fullList[i].isCheck) {
                showedCity = i
                return fullList[i]
            }
        }
        for (i in 0..showedCity){
            if (fullList[i].isCheck) {
                showedCity = i
                return fullList[i]
            }
        }
        showedCity = -1
        return errorData
    }

    fun applyCheck() {
        for (data in workList) {
            data.isCheck = data.tmpCheck
        }
        notifyDataSetChanged()
    }

    fun cancelCheck() {
        for (data in workList) {
            data.tmpCheck = data.isCheck
        }
        notifyDataSetChanged()
    }

    internal fun setDatabaseList(citys: List<CityData>) {
        databaseList = citys
        Log.d("myLOG", "setDatabaseList: ${databaseList.size}")
        //notifyDataSetChanged()
    }

}
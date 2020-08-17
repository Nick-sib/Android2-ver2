package com.nickolay.android2ver2.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.nickolay.android2ver2.database.CityData
import com.nickolay.android2ver2.database.CityDataDao

class CityDataRepository (private val cityDataDao: CityDataDao) {

    val allCitys: LiveData<List<CityData>> = cityDataDao.getAlphabetizedCitys()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(cityData: CityData) {
        cityDataDao.insert(cityData)
    }
}
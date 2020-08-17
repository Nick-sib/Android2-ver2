package com.nickolay.android2ver2.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nickolay.android2ver2.database.CityData

@Dao
interface CityDataDao {
    @Query("SELECT * from city_table ORDER BY city_name ASC")
    fun getAlphabetizedCitys(): LiveData<List<CityData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cityData: CityData)

    @Query("DELETE FROM city_table")
    fun deleteAll()
}
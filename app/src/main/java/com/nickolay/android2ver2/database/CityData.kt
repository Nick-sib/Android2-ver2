package com.nickolay.android2ver2.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_table")
data class CityData(
        @PrimaryKey
        @ColumnInfo(name = "_id")
        val id: Int,
        @ColumnInfo(name = "city_name")
        val cityName: String,
        @ColumnInfo(name = "city_tempricha")
        var cityTempricha: Float,
        @ColumnInfo(name = "city_data")
        var cityData: Int
    )

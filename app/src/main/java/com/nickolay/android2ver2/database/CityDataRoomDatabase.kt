package com.nickolay.android2ver2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(entities = [CityData::class], version = 1)
abstract class CityDataRoomDatabase : RoomDatabase() {

    abstract fun cityDataDao(): CityDataDao

    companion object {
        @Volatile
        private var INSTANCE: CityDataRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): CityDataRoomDatabase {
            return INSTANCE
                ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CityDataRoomDatabase::class.java,
                    "word_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(
                        CityDataDatabaseCallback(
                            scope
                        )
                    )
                    .build()
                INSTANCE = instance
                // return
                instance
            }
        }

        private class CityDataDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(
                            database.cityDataDao()
                        )
                    }
                }
            }
        }


        fun populateDatabase(cityDataDao: CityDataDao) {
            cityDataDao.deleteAll()
            var cityData = CityData(
                0,
                "TestCity",
                0.0f,
                0
            )
            cityDataDao.insert(cityData)
        }
    }
}
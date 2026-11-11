package com.example.noorapp.di

import android.content.Context
import androidx.room.Room
import com.example.noorapp.data.local.HadithDatabaseHelper
import com.example.noorapp.data.local.IslamicAppDatabase
import com.example.noorapp.data.local.QuranDatabaseHelper
import com.example.noorapp.data.local.dao.PrayerAlarmDao
import com.example.noorapp.data.local.dao.SalahRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * User data database: contains user alarms and salah tracker records.
     * Physically separated from scripture content to prevent any data loss on updates.
     */
    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): IslamicAppDatabase {
        return Room.databaseBuilder(
            context,
            IslamicAppDatabase::class.java,
            "islamic_prayers_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providePrayerAlarmDao(database: IslamicAppDatabase): PrayerAlarmDao {
        return database.prayerAlarmDao
    }

    @Provides
    @Singleton
    fun provideSalahRecordDao(database: IslamicAppDatabase): SalahRecordDao {
        return database.salahRecordDao
    }

    /**
     * Dedicated Offline Quran Content Database Manager (read-only SQLite).
     */
    @Provides
    @Singleton
    fun provideQuranDatabaseHelper(@ApplicationContext context: Context): QuranDatabaseHelper {
        return QuranDatabaseHelper(context)
    }

    /**
     * Dedicated Offline Hadith Content Database Manager (read-only SQLite).
     */
    @Provides
    @Singleton
    fun provideHadithDatabaseHelper(@ApplicationContext context: Context): HadithDatabaseHelper {
        return HadithDatabaseHelper(context)
    }
}

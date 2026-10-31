package com.ne.muslimprayers.qiblafinder.di

import android.content.Context
import androidx.room.Room
import com.ne.muslimprayers.qiblafinder.data.local.IslamicAppDatabase
import com.ne.muslimprayers.qiblafinder.data.local.dao.PrayerAlarmDao
import com.ne.muslimprayers.qiblafinder.data.local.dao.SalahRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): IslamicAppDatabase {
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
}

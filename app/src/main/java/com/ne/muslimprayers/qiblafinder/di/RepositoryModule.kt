package com.ne.muslimprayers.qiblafinder.di

import com.ne.muslimprayers.qiblafinder.data.repository.DuaRepositoryImpl
import com.ne.muslimprayers.qiblafinder.data.repository.HadithRepositoryImpl
import com.ne.muslimprayers.qiblafinder.data.repository.PrayerAlarmRepositoryImpl
import com.ne.muslimprayers.qiblafinder.data.repository.PrayerScheduleRepositoryImpl
import com.ne.muslimprayers.qiblafinder.data.repository.QiblaRepositoryImpl
import com.ne.muslimprayers.qiblafinder.data.repository.QuranRepositoryImpl
import com.ne.muslimprayers.qiblafinder.domain.repository.DuaRepository
import com.ne.muslimprayers.qiblafinder.domain.repository.HadithRepository
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerAlarmRepository
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerScheduleRepository
import com.ne.muslimprayers.qiblafinder.domain.repository.QiblaRepository
import com.ne.muslimprayers.qiblafinder.domain.repository.QuranRepository
import com.ne.muslimprayers.qiblafinder.data.repository.SalahTrackerRepositoryImpl
import com.ne.muslimprayers.qiblafinder.domain.repository.SalahTrackerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds data repository implementations to domain repository interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPrayerScheduleRepository(
        impl: PrayerScheduleRepositoryImpl
    ): PrayerScheduleRepository

    @Binds
    @Singleton
    abstract fun bindQuranRepository(
        impl: QuranRepositoryImpl
    ): QuranRepository

    @Binds
    @Singleton
    abstract fun bindHadithRepository(
        impl: HadithRepositoryImpl
    ): HadithRepository

    @Binds
    @Singleton
    abstract fun bindDuaRepository(
        impl: DuaRepositoryImpl
    ): DuaRepository

    @Binds
    @Singleton
    abstract fun bindPrayerAlarmRepository(
        impl: PrayerAlarmRepositoryImpl
    ): PrayerAlarmRepository

    @Binds
    @Singleton
    abstract fun bindQiblaRepository(
        impl: QiblaRepositoryImpl
    ): QiblaRepository

    @Binds
    @Singleton
    abstract fun bindSalahTrackerRepository(
        impl: SalahTrackerRepositoryImpl
    ): SalahTrackerRepository
}

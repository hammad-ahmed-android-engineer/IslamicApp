package com.example.noorapp.di

import com.example.noorapp.data.repository.AlarmSchedulerRepositoryImpl
import com.example.noorapp.data.repository.AudioPreviewRepositoryImpl
import com.example.noorapp.data.repository.CompassRepositoryImpl
import com.example.noorapp.data.repository.DuaRepositoryImpl
import com.example.noorapp.data.repository.HadithRepositoryImpl
import com.example.noorapp.data.repository.LocationRepositoryImpl
import com.example.noorapp.data.repository.PrayerAlarmRepositoryImpl
import com.example.noorapp.data.repository.PrayerScheduleRepositoryImpl
import com.example.noorapp.data.repository.PrayerSettingsRepositoryImpl
import com.example.noorapp.data.repository.QiblaRepositoryImpl
import com.example.noorapp.data.repository.QuranAudioRepositoryImpl
import com.example.noorapp.data.repository.QuranPreferencesRepositoryImpl
import com.example.noorapp.data.repository.QuranRepositoryImpl
import com.example.noorapp.data.repository.SalahTrackerRepositoryImpl
import com.example.noorapp.domain.repository.AlarmSchedulerRepository
import com.example.noorapp.domain.repository.AudioPreviewRepository
import com.example.noorapp.domain.repository.CompassRepository
import com.example.noorapp.domain.repository.DuaRepository
import com.example.noorapp.domain.repository.HadithRepository
import com.example.noorapp.domain.repository.LocationRepository
import com.example.noorapp.domain.repository.PrayerAlarmRepository
import com.example.noorapp.domain.repository.PrayerScheduleRepository
import com.example.noorapp.domain.repository.PrayerSettingsRepository
import com.example.noorapp.domain.repository.QiblaRepository
import com.example.noorapp.domain.repository.QuranAudioRepository
import com.example.noorapp.domain.repository.QuranPreferencesRepository
import com.example.noorapp.domain.repository.QuranRepository
import com.example.noorapp.domain.repository.SalahTrackerRepository
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

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindPrayerSettingsRepository(
        impl: PrayerSettingsRepositoryImpl
    ): PrayerSettingsRepository

    @Binds
    @Singleton
    abstract fun bindCompassRepository(
        impl: CompassRepositoryImpl
    ): CompassRepository

    @Binds
    @Singleton
    abstract fun bindAlarmSchedulerRepository(
        impl: AlarmSchedulerRepositoryImpl
    ): AlarmSchedulerRepository

    @Binds
    @Singleton
    abstract fun bindQuranPreferencesRepository(
        impl: QuranPreferencesRepositoryImpl
    ): QuranPreferencesRepository

    @Binds
    abstract fun bindQuranAudioRepository(
        impl: QuranAudioRepositoryImpl
    ): QuranAudioRepository

    @Binds
    abstract fun bindAudioPreviewRepository(
        impl: AudioPreviewRepositoryImpl
    ): AudioPreviewRepository
}

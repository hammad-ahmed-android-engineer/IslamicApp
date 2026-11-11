package com.example.noorapp.di

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
import com.example.noorapp.domain.usecase.GetIslamicCalendarUseCase
import com.example.noorapp.domain.usecase.GetPrayerAlarmsUseCase
import com.example.noorapp.domain.usecase.GetPrayerScheduleUseCase
import com.example.noorapp.domain.usecase.SchedulePrayerAlarmUseCase
import com.example.noorapp.domain.usecase.SyncPrayerAlarmsUseCase
import com.example.noorapp.domain.usecase.TogglePrayerAlarmUseCase
import com.example.noorapp.domain.usecase.audio.PlayAudioPreviewUseCase
import com.example.noorapp.domain.usecase.dua.GetDuasUseCase
import com.example.noorapp.domain.usecase.hadith.GetHadithCollectionUseCase
import com.example.noorapp.domain.usecase.location.GetCurrentLocationUseCase
import com.example.noorapp.domain.usecase.qibla.GetQiblaDirectionUseCase
import com.example.noorapp.domain.usecase.qibla.ObserveCompassReadingUseCase
import com.example.noorapp.domain.usecase.quran.GetAyahTafseerUseCase
import com.example.noorapp.domain.usecase.quran.GetJuzVersesUseCase
import com.example.noorapp.domain.usecase.quran.GetQuranSurahsUseCase
import com.example.noorapp.domain.usecase.quran.GetSurahVersesUseCase
import com.example.noorapp.domain.usecase.quran.ManageQuranAudioUseCase
import com.example.noorapp.domain.usecase.quran.ManageQuranPreferencesUseCase
import com.example.noorapp.domain.usecase.salah.GetDailySalahUseCase
import com.example.noorapp.domain.usecase.settings.ObservePrayerSettingsUseCase
import com.example.noorapp.domain.usecase.settings.UpdatePrayerSettingsUseCase
import com.example.noorapp.domain.usecase.zakat.CalculateZakatUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing Domain Use Cases across feature areas.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetPrayerScheduleUseCase(
        repository: PrayerScheduleRepository
    ): GetPrayerScheduleUseCase = GetPrayerScheduleUseCase(repository)

    @Provides
    @Singleton
    fun provideGetIslamicCalendarUseCase(
        repository: PrayerScheduleRepository
    ): GetIslamicCalendarUseCase = GetIslamicCalendarUseCase(repository)

    @Provides
    @Singleton
    fun provideGetPrayerAlarmsUseCase(
        repository: PrayerAlarmRepository
    ): GetPrayerAlarmsUseCase = GetPrayerAlarmsUseCase(repository)

    @Provides
    @Singleton
    fun provideTogglePrayerAlarmUseCase(
        repository: PrayerAlarmRepository
    ): TogglePrayerAlarmUseCase = TogglePrayerAlarmUseCase(repository)

    @Provides
    @Singleton
    fun provideGetQuranSurahsUseCase(
        repository: QuranRepository
    ): GetQuranSurahsUseCase = GetQuranSurahsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetSurahVersesUseCase(
        repository: QuranRepository
    ): GetSurahVersesUseCase = GetSurahVersesUseCase(repository)

    @Provides
    @Singleton
    fun provideGetJuzVersesUseCase(
        repository: QuranRepository
    ): GetJuzVersesUseCase = GetJuzVersesUseCase(repository)

    @Provides
    @Singleton
    fun provideGetHadithCollectionUseCase(
        repository: HadithRepository
    ): GetHadithCollectionUseCase = GetHadithCollectionUseCase(repository)

    @Provides
    @Singleton
    fun provideGetDuasUseCase(
        repository: DuaRepository
    ): GetDuasUseCase = GetDuasUseCase(repository)

    @Provides
    @Singleton
    fun provideGetQiblaDirectionUseCase(
        repository: QiblaRepository
    ): GetQiblaDirectionUseCase = GetQiblaDirectionUseCase(repository)

    @Provides
    @Singleton
    fun provideCalculateZakatUseCase(): CalculateZakatUseCase = CalculateZakatUseCase()

    @Provides
    @Singleton
    fun provideGetWeeklySalahUseCase(
        repository: com.example.noorapp.domain.repository.SalahTrackerRepository
    ): com.example.noorapp.domain.usecase.salah.GetWeeklySalahUseCase =
        com.example.noorapp.domain.usecase.salah.GetWeeklySalahUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdatePrayerStatusUseCase(
        repository: com.example.noorapp.domain.repository.SalahTrackerRepository
    ): com.example.noorapp.domain.usecase.salah.UpdatePrayerStatusUseCase =
        com.example.noorapp.domain.usecase.salah.UpdatePrayerStatusUseCase(repository)

    @Provides
    @Singleton
    fun provideSetAllPrayersStatusUseCase(
        repository: com.example.noorapp.domain.repository.SalahTrackerRepository
    ): com.example.noorapp.domain.usecase.salah.SetAllPrayersStatusUseCase =
        com.example.noorapp.domain.usecase.salah.SetAllPrayersStatusUseCase(repository)

    @Provides
    @Singleton
    fun provideGetDailySalahUseCase(
        repository: com.example.noorapp.domain.repository.SalahTrackerRepository
    ): GetDailySalahUseCase = GetDailySalahUseCase(repository)

    @Provides
    @Singleton
    fun provideSyncPrayerAlarmsUseCase(
        repository: PrayerAlarmRepository
    ): SyncPrayerAlarmsUseCase = SyncPrayerAlarmsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAyahTafseerUseCase(
        repository: QuranRepository
    ): GetAyahTafseerUseCase = GetAyahTafseerUseCase(repository)

    @Provides
    @Singleton
    fun provideGetCurrentLocationUseCase(
        repository: LocationRepository
    ): GetCurrentLocationUseCase = GetCurrentLocationUseCase(repository)

    @Provides
    @Singleton
    fun provideObservePrayerSettingsUseCase(
        repository: PrayerSettingsRepository
    ): ObservePrayerSettingsUseCase = ObservePrayerSettingsUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdatePrayerSettingsUseCase(
        repository: PrayerSettingsRepository
    ): UpdatePrayerSettingsUseCase = UpdatePrayerSettingsUseCase(repository)

    @Provides
    @Singleton
    fun provideObserveCompassReadingUseCase(
        repository: CompassRepository
    ): ObserveCompassReadingUseCase = ObserveCompassReadingUseCase(repository)

    @Provides
    @Singleton
    fun provideSchedulePrayerAlarmUseCase(
        repository: AlarmSchedulerRepository
    ): SchedulePrayerAlarmUseCase = SchedulePrayerAlarmUseCase(repository)

    @Provides
    @Singleton
    fun provideManageQuranPreferencesUseCase(
        repository: QuranPreferencesRepository
    ): ManageQuranPreferencesUseCase = ManageQuranPreferencesUseCase(repository)

    @Provides
    fun provideManageQuranAudioUseCase(
        repository: QuranAudioRepository
    ): ManageQuranAudioUseCase = ManageQuranAudioUseCase(repository)

    @Provides
    fun providePlayAudioPreviewUseCase(
        repository: AudioPreviewRepository
    ): PlayAudioPreviewUseCase = PlayAudioPreviewUseCase(repository)
}

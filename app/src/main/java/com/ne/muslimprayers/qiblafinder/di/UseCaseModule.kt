package com.ne.muslimprayers.qiblafinder.di

import com.ne.muslimprayers.qiblafinder.domain.repository.DuaRepository
import com.ne.muslimprayers.qiblafinder.domain.repository.HadithRepository
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerAlarmRepository
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerScheduleRepository
import com.ne.muslimprayers.qiblafinder.domain.repository.QiblaRepository
import com.ne.muslimprayers.qiblafinder.domain.repository.QuranRepository
import com.ne.muslimprayers.qiblafinder.domain.usecase.GetIslamicCalendarUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.GetPrayerAlarmsUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.GetPrayerScheduleUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.TogglePrayerAlarmUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.dua.GetDuasUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.hadith.GetHadithCollectionUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.qibla.GetQiblaDirectionUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.quran.GetQuranSurahsUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.quran.GetSurahVersesUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.zakat.CalculateZakatUseCase
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
        repository: com.ne.muslimprayers.qiblafinder.domain.repository.SalahTrackerRepository
    ): com.ne.muslimprayers.qiblafinder.domain.usecase.salah.GetWeeklySalahUseCase =
        com.ne.muslimprayers.qiblafinder.domain.usecase.salah.GetWeeklySalahUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdatePrayerStatusUseCase(
        repository: com.ne.muslimprayers.qiblafinder.domain.repository.SalahTrackerRepository
    ): com.ne.muslimprayers.qiblafinder.domain.usecase.salah.UpdatePrayerStatusUseCase =
        com.ne.muslimprayers.qiblafinder.domain.usecase.salah.UpdatePrayerStatusUseCase(repository)

    @Provides
    @Singleton
    fun provideSetAllPrayersStatusUseCase(
        repository: com.ne.muslimprayers.qiblafinder.domain.repository.SalahTrackerRepository
    ): com.ne.muslimprayers.qiblafinder.domain.usecase.salah.SetAllPrayersStatusUseCase =
        com.ne.muslimprayers.qiblafinder.domain.usecase.salah.SetAllPrayersStatusUseCase(repository)
}

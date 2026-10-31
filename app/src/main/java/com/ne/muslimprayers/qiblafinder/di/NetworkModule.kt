package com.ne.muslimprayers.qiblafinder.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ne.muslimprayers.qiblafinder.data.remote.api.DuaApiService
import com.ne.muslimprayers.qiblafinder.data.remote.api.HadithApiService
import com.ne.muslimprayers.qiblafinder.data.remote.api.PrayerApiService
import com.ne.muslimprayers.qiblafinder.data.remote.api.QuranApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun providePrayerApiService(okHttpClient: OkHttpClient, gson: Gson): PrayerApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(PrayerApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuranApiService(okHttpClient: OkHttpClient, gson: Gson): QuranApiService {
        val quranClient = okHttpClient.newBuilder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "IslamicApp/1.0 (Android; QuranClient)")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.alquran.cloud/v1/")
            .client(quranClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(QuranApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideHadithApiService(okHttpClient: OkHttpClient, gson: Gson): HadithApiService {
        return Retrofit.Builder()
            .baseUrl("https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(HadithApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDuaApiService(okHttpClient: OkHttpClient, gson: Gson): DuaApiService {
        return Retrofit.Builder()
            .baseUrl("https://cdn.jsdelivr.net/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(DuaApiService::class.java)
    }
}

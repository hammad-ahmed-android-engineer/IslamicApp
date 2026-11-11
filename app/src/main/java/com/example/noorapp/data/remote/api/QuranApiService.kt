package com.example.noorapp.data.remote.api

import com.example.noorapp.data.remote.dto.QuranDotComVersesResponse
import com.example.noorapp.data.remote.dto.QuranSurahListResponse
import com.example.noorapp.data.remote.dto.QuranSurahResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {
    @GET("surah")
    suspend fun getSurahs(): QuranSurahListResponse

    @GET("surah/{surahNumber}/editions/quran-uthmani,en.sahih")
    suspend fun getSurahVerses(
        @Path("surahNumber") surahNumber: Int
    ): QuranSurahResponse

    @GET("https://api.quran.com/api/v4/verses/by_chapter/{chapterNumber}?words=false&translations=20,97&fields=text_uthmani,text_indopak&per_page=300")
    suspend fun getQuranDotComVerses(
        @Path("chapterNumber") chapterNumber: Int
    ): QuranDotComVersesResponse

    @GET("https://api.quran.com/api/v4/tafsirs/{tafsirId}/by_ayah/{verseKey}")
    suspend fun getAyahTafseer(
        @Path("tafsirId") tafsirId: Int,
        @Path("verseKey") verseKey: String
    ): com.example.noorapp.data.remote.dto.QuranDotComTafsirResponse
}

package com.example.noorapp.data.repository

import com.example.noorapp.core.common.Resource
import com.example.noorapp.data.local.HadithDatabaseHelper
import com.example.noorapp.data.remote.api.HadithApiService
import com.example.noorapp.domain.model.Hadith
import com.example.noorapp.domain.repository.HadithRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [HadithRepository] supporting 100% offline access first via [HadithDatabaseHelper],
 * with transparent network fallback and automated local caching via [HadithApiService].
 */
@Singleton
class HadithRepositoryImpl @Inject constructor(
    private val hadithApiService: HadithApiService,
    private val hadithDbHelper: HadithDatabaseHelper
) : HadithRepository {

    override fun getHadithList(edition: String, section: Int): Flow<Resource<List<Hadith>>> = flow {
        emit(Resource.Loading(true))

        // 1. Check offline prepackaged and cached database first
        val localHadiths = hadithDbHelper.getHadiths(edition, section)
        if (localHadiths.isNotEmpty()) {
            emit(Resource.Success(localHadiths))
            return@flow
        }

        // 2. If not found in offline DB, fetch from remote API and auto-cache
        try {
            val response = hadithApiService.getHadithSection(edition, section)
            val dbSectionTitle = hadithDbHelper.getSectionTitle(edition, section)
            val chapterName = dbSectionTitle
                ?: response.metadata?.sections?.get(section.toString())
                ?: response.metadata?.section?.get(section.toString())
                ?: response.metadata?.name
                ?: "Chapter $section"

            val bookName = response.metadata?.name ?: when (edition) {
                "eng-bukhari" -> "Sahih al-Bukhari"
                "eng-muslim" -> "Sahih Muslim"
                "eng-abudawud" -> "Sunan Abu Dawud"
                "eng-tirmidhi" -> "Jami' at-Tirmidhi"
                "eng-nasai" -> "Sunan an-Nasa'i"
                "eng-ibnmajah" -> "Sunan Ibn Majah"
                else -> "Sahih Collection"
            }

            val list = response.hadiths.mapIndexed { index, item ->
                val hadithNum = when (val num = item.hadithNumber) {
                    is Number -> num.toLong().toString()
                    is String -> num.substringBefore(".")
                    else -> "${index + 1}"
                }
                Hadith(
                    id = index + 1,
                    chapter = "$bookName • $chapterName",
                    narrator = "Hadith #$hadithNum",
                    arabicText = "",
                    englishText = item.text.trim(),
                    source = "$bookName #$hadithNum"
                )
            }

            if (list.isNotEmpty()) {
                // Auto-cache into offline database for permanent offline reading
                hadithDbHelper.insertHadiths(edition, section, list)
            }

            emit(Resource.Success(list))
        } catch (e: Exception) {
            val sectionTitle = hadithDbHelper.getSectionTitle(edition, section)
            val errorMsg = if (sectionTitle != null) {
                "No offline Hadiths saved for $sectionTitle yet. Connect to the internet to load and auto-cache this chapter."
            } else {
                "No Hadiths available offline for this section. Please connect to the internet."
            }
            emit(Resource.Error(errorMsg))
        }
    }

    override suspend fun getSectionTitle(edition: String, section: Int): String? {
        return hadithDbHelper.getSectionTitle(edition, section)
    }
}

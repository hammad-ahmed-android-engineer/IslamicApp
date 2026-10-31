package com.ne.muslimprayers.qiblafinder.data.repository

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.data.remote.api.HadithApiService
import com.ne.muslimprayers.qiblafinder.domain.model.Hadith
import com.ne.muslimprayers.qiblafinder.domain.repository.HadithRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [HadithRepository] communicating with [HadithApiService].
 */
@Singleton
class HadithRepositoryImpl @Inject constructor(
    private val hadithApiService: HadithApiService
) : HadithRepository {

    override fun getHadithList(edition: String, section: Int): Flow<Resource<List<Hadith>>> = flow {
        emit(Resource.Loading(true))
        try {
            val response = hadithApiService.getHadithSection(edition, section)
            val chapterName = response.metadata?.sections?.get(section.toString())
                ?: response.metadata?.section?.get(section.toString())
                ?: response.metadata?.name
                ?: "Chapter $section"

            val list = response.hadiths.mapIndexed { index, item ->
                val hadithNum = when (val num = item.hadithNumber) {
                    is Number -> num.toLong().toString()
                    is String -> num.substringBefore(".")
                    else -> "${index + 1}"
                }
                val bookName = response.metadata?.name ?: "Sahih Collection"
                Hadith(
                    id = index + 1,
                    chapter = "$bookName • $chapterName",
                    narrator = "Hadith #$hadithNum",
                    arabicText = "",
                    englishText = item.text.trim(),
                    source = "$bookName #$hadithNum"
                )
            }
            emit(Resource.Success(list))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch Hadiths from API"))
        }
    }
}

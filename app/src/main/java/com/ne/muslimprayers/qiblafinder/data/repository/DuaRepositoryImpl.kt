package com.ne.muslimprayers.qiblafinder.data.repository

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.data.remote.api.DuaApiService
import com.ne.muslimprayers.qiblafinder.domain.model.DuaItem
import com.ne.muslimprayers.qiblafinder.domain.repository.DuaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [DuaRepository] communicating with [DuaApiService].
 */
@Singleton
class DuaRepositoryImpl @Inject constructor(
    private val duaApiService: DuaApiService
) : DuaRepository {

    override fun getDuaList(): Flow<Resource<List<DuaItem>>> = flow {
        emit(Resource.Loading(true))
        try {
            val response = duaApiService.getHisnMuslimDuas()
            val chapters = response.chapters.orEmpty()

            val allDuas = mutableListOf<DuaItem>()
            var duaIndex = 1

            for (ch in chapters) {
                val categoryName = ch.title?.trim().orEmpty().ifBlank { "Hisnul Muslim" }
                val items = ch.texts.orEmpty()
                for (item in items) {
                    val transliteration = item.transliteration?.trim().orEmpty()
                    val translation = item.translatedText?.trim().orEmpty().ifBlank { transliteration }
                    val arabic = item.arabicText?.trim().orEmpty()
                    if (arabic.isNotBlank()) {
                        allDuas.add(
                            DuaItem(
                                id = duaIndex++,
                                title = categoryName,
                                category = categoryName,
                                arabicText = arabic,
                                transliteration = transliteration,
                                translation = translation,
                                reference = "Fortress of the Muslim (Hisnul Muslim)"
                            )
                        )
                    }
                }
            }

            emit(Resource.Success(allDuas))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch Duas from API"))
        }
    }
}

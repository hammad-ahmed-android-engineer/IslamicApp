package com.ne.muslimprayers.qiblafinder.domain.usecase.dua

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.DuaItem
import com.ne.muslimprayers.qiblafinder.domain.repository.DuaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to fetch daily Fortress of the Muslim (Hisnul Muslim) Duas.
 */
class GetDuasUseCase @Inject constructor(
    private val duaRepository: DuaRepository
) {
    operator fun invoke(): Flow<Resource<List<DuaItem>>> {
        return duaRepository.getDuaList()
    }
}

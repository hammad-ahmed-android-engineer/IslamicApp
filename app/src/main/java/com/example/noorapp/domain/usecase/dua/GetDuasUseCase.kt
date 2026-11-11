package com.example.noorapp.domain.usecase.dua

import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.model.DuaItem
import com.example.noorapp.domain.repository.DuaRepository
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

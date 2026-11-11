package com.example.noorapp.domain.repository

import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.model.DuaItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for fetching Fortress of the Muslim (Hisnul Muslim) Duas.
 */
interface DuaRepository {
    /**
     * Fetches all categorization and items of daily Islamic supplications (Duas).
     */
    fun getDuaList(): Flow<Resource<List<DuaItem>>>
}

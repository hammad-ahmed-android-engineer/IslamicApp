package com.ne.muslimprayers.qiblafinder.domain.repository

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.DuaItem
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

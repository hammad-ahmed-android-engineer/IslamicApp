package com.ne.muslimprayers.qiblafinder.data.remote.api

import com.ne.muslimprayers.qiblafinder.data.remote.dto.HadithSectionResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface HadithApiService {
    @GET("editions/{edition}/sections/{section}.json")
    suspend fun getHadithSection(
        @Path("edition") edition: String,
        @Path("section") section: Int
    ): HadithSectionResponse
}

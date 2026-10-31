package com.ne.muslimprayers.qiblafinder.data.remote.api

import com.ne.muslimprayers.qiblafinder.data.remote.dto.HisnMuslimResponse
import retrofit2.http.GET

interface DuaApiService {
    @GET("gh/wafaaelmaandy/Hisn-Muslim-Json@master/husn_en.json")
    suspend fun getHisnMuslimDuas(): HisnMuslimResponse
}

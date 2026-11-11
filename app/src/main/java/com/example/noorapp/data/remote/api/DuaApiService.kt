package com.example.noorapp.data.remote.api

import com.example.noorapp.data.remote.dto.HisnMuslimResponse
import retrofit2.http.GET

interface DuaApiService {
    @GET("gh/wafaaelmaandy/Hisn-Muslim-Json@master/husn_en.json")
    suspend fun getHisnMuslimDuas(): HisnMuslimResponse
}

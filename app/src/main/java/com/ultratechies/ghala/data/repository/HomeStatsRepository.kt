package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.home.HomeStatsResponse
import javax.inject.Inject

interface HomeStatsRepository {
    suspend fun getStats(id: Int): APIResource<HomeStatsResponse>
}

class HomeStatsRepositoryImpl @Inject constructor(
    private val homeApi: HomeStatsApi) : HomeStatsRepository, BaseRepo()
{
    override suspend fun getStats(id: Int) = safeApiCall {
        homeApi.getStats(id)
    }
}
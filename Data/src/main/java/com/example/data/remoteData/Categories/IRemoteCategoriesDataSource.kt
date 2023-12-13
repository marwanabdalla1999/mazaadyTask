package com.example.data.remoteData.pets

import com.example.data.responses.pets.PetsResponse
import com.example.domain.apiStates.CategoriesApiStates
import retrofit2.Response

interface IRemotePetsDataSource {



    suspend fun getCategories(): Response<CategoriesApiStates>
}
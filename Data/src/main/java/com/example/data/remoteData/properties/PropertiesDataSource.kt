package com.example.data.remoteData.Categories

import com.example.data.apisEndPoints.ApiService
import com.example.data.utils.Constants
import com.example.domain.models.categories.categories
import com.example.domain.models.properties.properties
import retrofit2.Response


class PropertiesPetsDataSource (private val apiService: ApiService): IRemotePropertiesDataSource {


    override suspend fun getProperties(id : Int): Response<properties> {

          return  apiService.getProperties(Constants.Credentials.PrivateKey,id)


    }


}
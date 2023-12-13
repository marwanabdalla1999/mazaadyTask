package com.example.domain.useCases.getPets

import com.example.domain.apiStates.CategoriesApiStates
import com.example.domain.reposoitories.ICategoriesRepository

class CategoriesUseCase(
    private val categories: ICategoriesRepository
) : ICategoriesUseCase {

    override suspend fun getCategories(): CategoriesApiStates {


        return categories.getCategories()


    }




}
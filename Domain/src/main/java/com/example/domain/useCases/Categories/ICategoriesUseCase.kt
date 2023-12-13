package com.example.domain.useCases.getPets

import com.example.domain.apiStates.CategoriesApiStates

interface ICategoriesUseCase {


   suspend fun getCategories(): CategoriesApiStates
}
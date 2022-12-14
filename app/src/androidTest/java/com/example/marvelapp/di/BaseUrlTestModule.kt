package com.example.marvelapp.di

import com.example.marvelapp.framework.di.qualifier.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/*
* Pelo fato de ter desisntalado o modulo de produção no teste, ele já vai reconhecer esse na
*  hierarquia de arquivos do teste
* */

@Module
@InstallIn(SingletonComponent::class)
object BaseUrlTestModule {

    @BaseUrl
    @Provides
    fun provideBaseUrl(): String = "http://localhost:8080/"
}
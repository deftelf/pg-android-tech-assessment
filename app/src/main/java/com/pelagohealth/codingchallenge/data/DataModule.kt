package com.pelagohealth.codingchallenge.data

import com.pelagohealth.codingchallenge.data.datasource.rest.FactsRestApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Hilt module that provides dependencies for the data layer.
 */
@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Provides
    fun provideFactsRestApi(retrofit: Retrofit): FactsRestApi =
        retrofit.create(FactsRestApi::class.java)

    @Provides
    fun provideRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    fun provideOkhttp(): OkHttpClient {
        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return client
    }

    @Provides
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .build()

    companion object {
        const val BASE_URL = "https://uselessfacts.jsph.pl/"
    }

}
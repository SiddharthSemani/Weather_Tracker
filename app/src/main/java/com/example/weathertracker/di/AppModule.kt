package com.example.weathertracker.di

import android.app.Application
import com.example.weathertracker.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideNetworkUtils(application: Application): NetworkUtils {
        return NetworkUtils(application)
    }
}

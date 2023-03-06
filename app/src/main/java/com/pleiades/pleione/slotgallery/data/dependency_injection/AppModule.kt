package com.pleiades.pleione.slotgallery.data.dependency_injection

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Resources
import com.pleiades.pleione.slotgallery.Config.Companion.PREFS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences = context.getSharedPreferences(PREFS, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideEditor(@ApplicationContext context: Context): SharedPreferences.Editor = context.getSharedPreferences(PREFS, MODE_PRIVATE).edit()

    @Provides
    @Singleton
    fun provideResources(@ApplicationContext context: Context): Resources = context.resources
}
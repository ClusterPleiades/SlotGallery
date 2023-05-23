package com.pleiades.pleione.slotgallery.data.dependencyinjection

import com.pleiades.pleione.slotgallery.data.repository.MediaRepositoryImpl
import com.pleiades.pleione.slotgallery.data.repository.SlotRepositoryImpl
import com.pleiades.pleione.slotgallery.data.repository.UtilRepositoryImpl
import com.pleiades.pleione.slotgallery.data.repository.WindowRepositoryImpl
import com.pleiades.pleione.slotgallery.domain.repository.MediaRepository
import com.pleiades.pleione.slotgallery.domain.repository.SlotRepository
import com.pleiades.pleione.slotgallery.domain.repository.UtilRepository
import com.pleiades.pleione.slotgallery.domain.repository.WindowRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        mediaRepositoryImpl: MediaRepositoryImpl
    ): MediaRepository

    @Binds
    @Singleton
    abstract fun bindSlotRepository(
        slotRepositoryImpl: SlotRepositoryImpl
    ): SlotRepository

    @Binds
    @Singleton
    abstract fun bindUtilRepository(
        utilRepositoryImpl: UtilRepositoryImpl
    ): UtilRepository

    @Binds
    @Singleton
    abstract fun bindWindowRepository(
        windowRepositoryImpl: WindowRepositoryImpl
    ): WindowRepository
}

package com.pleiades.pleione.slotgallery.data.dependency_injection

import com.pleiades.pleione.slotgallery.data.repository.DefaultMediaRepository
import com.pleiades.pleione.slotgallery.data.repository.DefaultSlotRepository
import com.pleiades.pleione.slotgallery.data.repository.DefaultUtilRepository
import com.pleiades.pleione.slotgallery.data.repository.DefaultWindowRepository
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
        defaultMediaRepository: DefaultMediaRepository
    ): MediaRepository

    @Binds
    @Singleton
    abstract fun bindSlotRepository(
        defaultSlotRepository: DefaultSlotRepository
    ): SlotRepository

    @Binds
    @Singleton
    abstract fun bindUtilRepository(
        defaultUtilRepository: DefaultUtilRepository
    ): UtilRepository

    @Binds
    @Singleton
    abstract fun bindWindowRepository(
        defaultWindowRepository: DefaultWindowRepository
    ): WindowRepository
}
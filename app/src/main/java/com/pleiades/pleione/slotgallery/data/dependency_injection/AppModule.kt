package com.pleiades.pleione.slotgallery.data.dependency_injection

import android.content.ContentResolver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Resources
import com.pleiades.pleione.slotgallery.Config.Companion.PREFS
import com.pleiades.pleione.slotgallery.domain.repository.MediaRepository
import com.pleiades.pleione.slotgallery.domain.repository.SlotRepository
import com.pleiades.pleione.slotgallery.domain.repository.UtilRepository
import com.pleiades.pleione.slotgallery.domain.repository.WindowRepository
import com.pleiades.pleione.slotgallery.domain.use_case.media.CopyDirectoryUseCase
import com.pleiades.pleione.slotgallery.domain.use_case.media.CopyMediaUseCase
import com.pleiades.pleione.slotgallery.domain.use_case.media.GetDirectoryListUseCase
import com.pleiades.pleione.slotgallery.domain.use_case.media.bundle.MediaUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.use_case.slot.*
import com.pleiades.pleione.slotgallery.domain.use_case.slot.bundle.SlotUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.use_case.util.GetDirectorySortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.use_case.util.GetMediaSortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.use_case.util.PutDirectorySortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.use_case.util.PutMediaSortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.use_case.util.bundle.UtilUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.use_case.window.GetWidthUseCase
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
    fun provideApplicationContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences = context.getSharedPreferences(PREFS, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideEditor(@ApplicationContext context: Context): SharedPreferences.Editor = context.getSharedPreferences(PREFS, MODE_PRIVATE).edit()

    @Provides
    @Singleton
    fun provideResources(@ApplicationContext context: Context): Resources = context.resources

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver = context.contentResolver

    @Provides
    @Singleton
    fun provideMediaUseCaseBundle(repository: MediaRepository): MediaUseCaseBundle =
        MediaUseCaseBundle(
            getDirectoryListUseCase = GetDirectoryListUseCase(repository),
            copyDirectoryUseCase = CopyDirectoryUseCase(repository),
            copyMediaUseCase = CopyMediaUseCase(repository)
        )

    @Provides
    @Singleton
    fun provideSlotUseCaseBundle(repository: SlotRepository): SlotUseCaseBundle =
        SlotUseCaseBundle(
            putSlotListUseCase = PutSlotListUseCase(repository),
            getSlotListUseCase = GetSlotListUseCase(repository),
            putSelectedSlotPositionUseCase = PutSelectedSlotPositionUseCase(repository),
            getSelectedSlotPositionUseCase = GetSelectedSlotPositionUseCase(repository)
        )

    @Provides
    @Singleton
    fun provideUtilUseCaseBundle(repository: UtilRepository): UtilUseCaseBundle =
        UtilUseCaseBundle(
            putDirectorySortOrderPositionUseCase = PutDirectorySortOrderPositionUseCase(repository),
            getDirectorySortOrderPositionUseCase = GetDirectorySortOrderPositionUseCase(repository),
            putMediaSortOrderPositionUseCase = PutMediaSortOrderPositionUseCase(repository),
            getMediaSortOrderPositionUseCase = GetMediaSortOrderPositionUseCase(repository)
        )

    @Provides
    @Singleton
    fun provideGetWidthUseCase(repository: WindowRepository) = GetWidthUseCase(repository)
}

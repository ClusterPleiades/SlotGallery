package com.pleiades.pleione.slotgallery.domain.usecase.util.bundle

import com.pleiades.pleione.slotgallery.domain.usecase.util.GetDirectorySortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.util.GetMediaSortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.util.PutDirectorySortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.util.PutMediaSortOrderPositionUseCase

data class UtilUseCaseBundle(
    val putDirectorySortOrderPositionUseCase: PutDirectorySortOrderPositionUseCase,
    val getDirectorySortOrderPositionUseCase: GetDirectorySortOrderPositionUseCase,
    val putMediaSortOrderPositionUseCase: PutMediaSortOrderPositionUseCase,
    val getMediaSortOrderPositionUseCase: GetMediaSortOrderPositionUseCase
)

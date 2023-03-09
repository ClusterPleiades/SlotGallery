package com.pleiades.pleione.slotgallery.domain.use_case.util.bundle

import com.pleiades.pleione.slotgallery.domain.use_case.util.GetDirectorySortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.use_case.util.GetMediaSortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.use_case.util.PutDirectorySortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.use_case.util.PutMediaSortOrderPositionUseCase

data class UtilUseCaseBundle(
    val putDirectorySortOrderPositionUseCase: PutDirectorySortOrderPositionUseCase,
    val getDirectorySortOrderPositionUseCase: GetDirectorySortOrderPositionUseCase,
    val putMediaSortOrderPositionUseCase: PutMediaSortOrderPositionUseCase,
    val getMediaSortOrderPositionUseCase: GetMediaSortOrderPositionUseCase
)
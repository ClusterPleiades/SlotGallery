package com.pleiades.pleione.slotgallery.domain.usecase.media.bundle

import com.pleiades.pleione.slotgallery.domain.usecase.media.CopyDirectoryUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.media.CopyMediaUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.media.GetDirectoryListUseCase

data class MediaUseCaseBundle(
    val getDirectoryListUseCase: GetDirectoryListUseCase,
    val copyDirectoryUseCase: CopyDirectoryUseCase,
    val copyMediaUseCase: CopyMediaUseCase
)

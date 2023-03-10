package com.pleiades.pleione.slotgallery.domain.use_case.media.bundle

import com.pleiades.pleione.slotgallery.domain.use_case.media.CopyDirectoryUseCase
import com.pleiades.pleione.slotgallery.domain.use_case.media.CopyMediaUseCase
import com.pleiades.pleione.slotgallery.domain.use_case.media.GetDirectoryListUseCase

data class MediaUseCaseBundle(
    val getDirectoryListUseCase: GetDirectoryListUseCase,
    val copyDirectoryUseCase: CopyDirectoryUseCase,
    val copyMediaUseCase: CopyMediaUseCase
)

package com.pleiades.pleione.slotgallery.domain.usecase.media.bundle

import com.pleiades.pleione.slotgallery.domain.usecase.media.CopyDirectoryUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.media.CopyMediaUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.media.GetDirectoryListUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.media.RenameMediaUseCase

data class MediaUseCaseBundle(
    val getDirectoryListUseCase: GetDirectoryListUseCase,
    val copyDirectoryUseCase: CopyDirectoryUseCase,
    val copyMediaUseCase: CopyMediaUseCase,
    val renameMediaUseCase: RenameMediaUseCase,
)

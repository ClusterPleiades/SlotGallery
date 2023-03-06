package com.pleiades.pleione.slotgallery.domain.use_case.media

import com.pleiades.pleione.slotgallery.domain.model.Media
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.repository.MediaRepository

class CopyMediaUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(
        fromDirectory: Directory,
        toDirectory: Directory,
        mediaSet: Set<Media>
    ) = repository.copyMedia(
        fromDirectory = fromDirectory,
        toDirectory = toDirectory,
        mediaSet = mediaSet
    )
}
package com.pleiades.pleione.slotgallery.domain.usecase.media

import com.pleiades.pleione.slotgallery.domain.model.Media
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.repository.MediaRepository

class CopyMediaUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(
        mediaList: List<Media>,
        toDirectory: Directory,
        setMaxProgress: (Int) -> Unit,
        setProgress: () -> Unit
    ) = repository.copyMedia(
        mediaList = mediaList,
        toDirectory = toDirectory,
        setMaxProgress = setMaxProgress,
        setProgress = setProgress
    )
}

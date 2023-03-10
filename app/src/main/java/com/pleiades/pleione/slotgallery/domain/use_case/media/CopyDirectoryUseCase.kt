package com.pleiades.pleione.slotgallery.domain.use_case.media

import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.repository.MediaRepository

class CopyDirectoryUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(
        fromDirectoryList: List<Directory>,
        toDirectory: Directory,
        setMaxProgress: (Int) -> Unit,
        setProgress: () -> Unit
    ) = repository.copyDirectory(
        fromDirectoryList = fromDirectoryList,
        toDirectory = toDirectory,
        setMaxProgress = setMaxProgress,
        progress = setProgress
    )
}

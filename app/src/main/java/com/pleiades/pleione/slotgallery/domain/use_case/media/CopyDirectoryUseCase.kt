package com.pleiades.pleione.slotgallery.domain.use_case.media

import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.repository.MediaRepository

class CopyDirectoryUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(
        fromDirectoryList: List<Directory>,
        toDirectory: Directory
    ) = repository.copyDirectory(
        fromDirectoryList = fromDirectoryList,
        toDirectory = toDirectory
    )
}
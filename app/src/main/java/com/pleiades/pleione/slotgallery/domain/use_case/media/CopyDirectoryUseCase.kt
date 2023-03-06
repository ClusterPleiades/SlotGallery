package com.pleiades.pleione.slotgallery.domain.use_case.media

import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.repository.MediaRepository

class CopyDirectoryUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(
        fromDirectorySet: Set<Directory>,
        toDirectory: Directory
    ) = repository.copyDirectory(
        fromDirectorySet = fromDirectorySet,
        toDirectory = toDirectory
    )
}
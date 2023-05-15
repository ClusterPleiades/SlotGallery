package com.pleiades.pleione.slotgallery.domain.usecase.media

import com.pleiades.pleione.slotgallery.domain.model.Media
import com.pleiades.pleione.slotgallery.domain.repository.MediaRepository

class RenameMediaUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(
        media: Media,
        toName: String
    ) = repository.renameMedia(
        media = media,
        toName = toName
    )
}

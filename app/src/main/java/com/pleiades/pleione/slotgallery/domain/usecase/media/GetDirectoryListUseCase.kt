package com.pleiades.pleione.slotgallery.domain.usecase.media

import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.repository.MediaRepository

class GetDirectoryListUseCase(private val repository: MediaRepository) {
    operator fun invoke(selectedSlot: Slot) = repository.getDirectoryList(selectedSlot)
}

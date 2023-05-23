package com.pleiades.pleione.slotgallery.domain.usecase.slot

import com.pleiades.pleione.slotgallery.domain.repository.SlotRepository

class GetSelectedSlotPositionUseCase(private val repository: SlotRepository) {
    operator fun invoke() = repository.getSelectedSlotPosition()
}

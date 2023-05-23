package com.pleiades.pleione.slotgallery.domain.usecase.slot

import com.pleiades.pleione.slotgallery.domain.repository.SlotRepository

class PutSelectedSlotPositionUseCase(private val repository: SlotRepository) {
    operator fun invoke(position: Int) = repository.putSelectedSlotPosition(position)
}

package com.pleiades.pleione.slotgallery.domain.use_case.slot

import com.pleiades.pleione.slotgallery.domain.repository.SlotRepository

class GetSelectedSlotUseCase(private val repository: SlotRepository) {
    operator fun invoke() = repository.getSelectedSlotInfo()
}
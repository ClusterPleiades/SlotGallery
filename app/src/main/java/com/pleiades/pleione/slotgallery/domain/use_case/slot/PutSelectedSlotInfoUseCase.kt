package com.pleiades.pleione.slotgallery.domain.use_case.slot

import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.repository.SlotRepository

class PutSelectedSlotInfoUseCase(private val repository: SlotRepository) {
    operator fun invoke(slot: Slot) = repository.putSelectedSlotInfo(slot)
}
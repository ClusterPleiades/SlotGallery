package com.pleiades.pleione.slotgallery.domain.usecase.slot

import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.repository.SlotRepository

class PutSlotListUseCase(private val repository: SlotRepository) {
    operator fun invoke(slotList: List<Slot>) = repository.putSlotList(slotList)
}

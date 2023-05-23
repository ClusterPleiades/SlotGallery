package com.pleiades.pleione.slotgallery.domain.usecase.slot.bundle

import com.pleiades.pleione.slotgallery.domain.usecase.slot.GetSelectedSlotPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.GetSlotListUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.PutSelectedSlotPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.PutSlotListUseCase

data class SlotUseCaseBundle(
    val putSlotListUseCase: PutSlotListUseCase,
    val getSlotListUseCase: GetSlotListUseCase,
    val putSelectedSlotPositionUseCase: PutSelectedSlotPositionUseCase,
    val getSelectedSlotPositionUseCase: GetSelectedSlotPositionUseCase
)

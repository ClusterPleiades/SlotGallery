package com.pleiades.pleione.slotgallery.domain.use_case.slot.bundle

import com.pleiades.pleione.slotgallery.domain.use_case.slot.*

data class SlotUseCaseBundle(
    val putSlotListUseCase: PutSlotListUseCase,
    val getSlotListUseCase: GetSlotListUseCase,
    val putSelectedSlotPositionUseCase: PutSelectedSlotPositionUseCase,
    val getSelectedSlotPositionUseCase: GetSelectedSlotPositionUseCase,
    val putSelectedSlotInfoUseCase: PutSelectedSlotInfoUseCase,
    val getSelectedSlotInfoUseCase: GetSelectedSlotInfoUseCase
)
package com.pleiades.pleione.slotgallery.presentation.setting

import com.pleiades.pleione.slotgallery.domain.model.Slot

data class SettingState(
    val selectedSlotPosition: Int = 0,
    val slotList: List<Slot> = emptyList()
)

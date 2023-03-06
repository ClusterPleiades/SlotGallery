package com.pleiades.pleione.slotgallery.domain.repository

import com.pleiades.pleione.slotgallery.domain.model.Slot

interface SlotRepository {
    fun putSlotList(slotList: List<Slot>)

    fun getSlotList(): List<Slot>

    fun putSelectedSlotPosition(position: Int)

    fun getSelectedSlotPosition(): Int

    fun putSelectedSlotInfo(slot: Slot)

    fun getSelectedSlotInfo(): Slot?
}
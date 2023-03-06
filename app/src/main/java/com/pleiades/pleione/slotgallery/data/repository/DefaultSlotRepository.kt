package com.pleiades.pleione.slotgallery.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.repository.SlotRepository
import java.util.*
import javax.inject.Inject

class DefaultSlotRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) : SlotRepository {
    override fun putSlotList(slotList: List<Slot>) {
        editor
            .putString(Config.KEY_SLOT_LIST, Gson().toJson(slotList))
            .apply()
    }

    override fun getSlotList(): List<Slot> {
        sharedPreferences.getString(Config.KEY_SLOT_LIST, null)?.let {
            return Gson().fromJson(
                it,
                object : TypeToken<LinkedList<Slot>>() {}.type
            )
        }
        return emptyList()
    }

    override fun putSelectedSlotPosition(position: Int) {
        editor
            .putInt(Config.KEY_SELECTED_SLOT_POSITION, position)
            .apply()
    }

    override fun getSelectedSlotPosition() = sharedPreferences.getInt(Config.KEY_SELECTED_SLOT_POSITION, 0)

    override fun putSelectedSlotInfo(slot: Slot) {
        val slotMutableList = getSlotList().toMutableList()
        val selectedSlotPosition = getSelectedSlotPosition()
        slotMutableList[selectedSlotPosition] = slot
        putSlotList(slotMutableList)
    }

    override fun getSelectedSlotInfo(): Slot? {
        getSlotList().let {
            return if (it.isEmpty()) null
            else it[getSelectedSlotPosition()]
        }
    }
}
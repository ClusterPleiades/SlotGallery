package com.pleiades.pleione.slotgallery.controller

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_SELECTED_SLOT_POSITION
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_SLOT_LIST
import com.pleiades.pleione.slotgallery.Config.Companion.PREFS
import com.pleiades.pleione.slotgallery.domain.model.Slot
import java.util.*

class SlotController(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS, MODE_PRIVATE)
    private val editor = prefs.edit()

    fun putSlotInfoLinkedList(slotLinkedList: LinkedList<Slot>) {
        editor.putString(KEY_SLOT_LIST, Gson().toJson(slotLinkedList))
        editor.apply()
    }

    fun getSlotInfoLinkedList(): LinkedList<Slot> {
        val gson = Gson()
        val json: String? = prefs.getString(KEY_SLOT_LIST, null)

        return if (json == null)
            LinkedList()
        else {
            val type = object : TypeToken<LinkedList<Slot>>() {}.type
            gson.fromJson(json, type)
        }
    }

    fun putSelectedSlotInfoPosition(position: Int) {
        editor.putInt(KEY_SELECTED_SLOT_POSITION, position)
        editor.apply()
    }

    fun getSelectedSlotInfoPosition(): Int {
        return prefs.getInt(KEY_SELECTED_SLOT_POSITION, 0)
    }

    fun putSelectedSlotInfo(slot: Slot) {
        val slotInfoLinkedList = getSlotInfoLinkedList()
        val selectedSlotPosition = getSelectedSlotInfoPosition()
        slotInfoLinkedList[selectedSlotPosition] = slot
        putSlotInfoLinkedList(slotInfoLinkedList)
    }

    fun getSelectedSlot(): Slot? {
        val slotLinkedList = getSlotInfoLinkedList()
        return if (slotLinkedList.size == 0) null else slotLinkedList[getSelectedSlotInfoPosition()]
    }
}
package com.pleiades.pleione.slotgallery.slot

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_SLOT_LIST
import com.pleiades.pleione.slotgallery.Config.Companion.PREFS
import java.util.*

class SlotController(private val context: Context) {
    fun putSlotLinkedList(slotLinkedList: LinkedList<Slot>) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(KEY_SLOT_LIST, Gson().toJson(slotLinkedList))
        editor.apply()
    }

    fun getSlotLinkedList(): LinkedList<Slot> {
        val prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE)
        val gson = Gson()
        val json: String? = prefs.getString(KEY_SLOT_LIST, null)

        return if (json == null)
            LinkedList()
        else {
            val type = object : TypeToken<LinkedList<Slot>>() {}.type
            gson.fromJson(json, type)
        }
    }

    class Slot(var name: String) {
        var directoryLinkedList: LinkedList<String> = LinkedList()
    }
}
package com.pleiades.pleione.slotgallery.slot

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_SELECTED_SLOT_POSITION
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_SLOT_LIST
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_CAMERA
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_DOWNLOAD
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_SCREENSHOTS
import com.pleiades.pleione.slotgallery.Config.Companion.PREFS
import java.util.*

class SlotController(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS, MODE_PRIVATE)
    private val editor = prefs.edit()

    fun putSlotLinkedList(slotLinkedList: LinkedList<Slot>) {
        editor.putString(KEY_SLOT_LIST, Gson().toJson(slotLinkedList))
        editor.apply()
    }

    fun getSlotLinkedList(): LinkedList<Slot> {
        val gson = Gson()
        val json: String? = prefs.getString(KEY_SLOT_LIST, null)

        return if (json == null)
            LinkedList()
        else {
            val type = object : TypeToken<LinkedList<Slot>>() {}.type
            gson.fromJson(json, type)
        }
    }

    fun putSelectedSlotPosition(position: Int) {
        editor.putInt(KEY_SELECTED_SLOT_POSITION, position)
        editor.apply()
    }

    fun getSelectedSlotPosition(): Int {
        return prefs.getInt(KEY_SELECTED_SLOT_POSITION, 0)
    }

    fun putSelectedSlot(slot: Slot) {
        val slotLinkedList = getSlotLinkedList()
        val selectedSlotPosition = getSelectedSlotPosition()
        slotLinkedList[selectedSlotPosition] = slot
        putSlotLinkedList(slotLinkedList)
    }

    fun getSelectedSlot(): Slot? {
        val slotLinkedList = getSlotLinkedList()
        return if (slotLinkedList.size == 0) null else slotLinkedList[getSelectedSlotPosition()]
    }

    class Slot(var name: String) {
        var directoryLinkedList: LinkedList<Directory> = LinkedList()

        init {
            directoryLinkedList.add(Directory(PATH_DOWNLOAD))
            directoryLinkedList.add(Directory(PATH_CAMERA))
            directoryLinkedList.add(Directory(PATH_SCREENSHOTS))
        }
    }

    class Directory {
        var treeUriString: String?
        var lastPathSegment: String
        var isVisible = true

        constructor(treeUri: Uri) {
            this.treeUriString = treeUri.toString()
            lastPathSegment = treeUri.lastPathSegment!!
        }

        constructor(lastPathSegment: String) {
            this.treeUriString = null
            this.lastPathSegment = lastPathSegment
        }
    }
}
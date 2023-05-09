package com.pleiades.pleione.slotgallery.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pleiades.pleione.slotgallery.Config.Companion.PREFS_KEY_SELECTED_SLOT_POSITION
import com.pleiades.pleione.slotgallery.Config.Companion.PREFS_KEY_SLOT_LIST
import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.repository.SlotRepository
import javax.inject.Inject

class SlotRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor,
) : SlotRepository {
    override fun putSlotList(slotList: List<Slot>) =
        editor.putString(PREFS_KEY_SLOT_LIST, Gson().toJson(slotList)).apply()

    override fun getSlotList(): List<Slot> {
        sharedPreferences.getString(PREFS_KEY_SLOT_LIST, null)?.let {
            return Gson().fromJson(it, object : TypeToken<List<Slot>>() {}.type)
        } ?: return emptyList()
    }

    override fun putSelectedSlotPosition(position: Int) =
        editor.putInt(PREFS_KEY_SELECTED_SLOT_POSITION, position).apply()

    override fun getSelectedSlotPosition() =
        sharedPreferences.getInt(PREFS_KEY_SELECTED_SLOT_POSITION, 0)
}

package com.pleiades.pleione.slotgallery.data.repository

import android.content.SharedPreferences
import com.pleiades.pleione.slotgallery.Config.Companion.PREFS_KEY_SORT_ORDER_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.PREFS_KEY_SORT_ORDER_DIRECTORY_INSIDE
import com.pleiades.pleione.slotgallery.domain.repository.UtilRepository
import javax.inject.Inject

class UtilRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor,
) : UtilRepository {
    override fun putDirectorySortOrderPosition(position: Int) =
        editor.putInt(PREFS_KEY_SORT_ORDER_DIRECTORY, position).apply()

    override fun getDirectorySortOrderPosition(): Int =
        sharedPreferences.getInt(PREFS_KEY_SORT_ORDER_DIRECTORY, 0)

    override fun putMediaSortOrderPosition(position: Int) =
        editor.putInt(PREFS_KEY_SORT_ORDER_DIRECTORY_INSIDE, position).apply()

    override fun getMediaSortOrderPosition(): Int =
        sharedPreferences.getInt(PREFS_KEY_SORT_ORDER_DIRECTORY_INSIDE, 0)
}

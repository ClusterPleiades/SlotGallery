package com.pleiades.pleione.slotgallery.data.repository

import android.content.SharedPreferences
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_MEDIA_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_SORT_ORDER
import com.pleiades.pleione.slotgallery.domain.repository.UtilRepository
import javax.inject.Inject

class DefaultUtilRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) : UtilRepository {
    override fun putDirectorySortOrderPosition(position: Int) = editor.putInt(KEY_DIRECTORY_SORT_ORDER, position).apply()

    override fun getDirectorySortOrderPosition(): Int = sharedPreferences.getInt(KEY_DIRECTORY_SORT_ORDER, 0)

    override fun putMediaSortOrderPosition(position: Int) = editor.putInt(KEY_MEDIA_SORT_ORDER, position).apply()

    override fun getMediaSortOrderPosition(): Int = sharedPreferences.getInt(KEY_MEDIA_SORT_ORDER, 0)
}
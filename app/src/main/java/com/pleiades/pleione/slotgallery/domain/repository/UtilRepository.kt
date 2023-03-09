package com.pleiades.pleione.slotgallery.domain.repository

interface UtilRepository {
    fun putDirectorySortOrderPosition(position: Int)

    fun getDirectorySortOrderPosition(): Int

    fun putMediaSortOrderPosition(position: Int)

    fun getMediaSortOrderPosition(): Int
}
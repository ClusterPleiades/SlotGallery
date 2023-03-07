package com.pleiades.pleione.slotgallery.domain.model

import com.pleiades.pleione.slotgallery.Config.Companion.PATH_PRIMARY

data class Directory(val directoryOverview: DirectoryOverview) {
    val name = directoryOverview.lastPath.substringAfter(PATH_PRIMARY).substringAfterLast("/")
    val mediaMutableList: MutableList<Media> = mutableListOf()
    val date: Long
        get() = mediaMutableList.maxOf { it.date }
}
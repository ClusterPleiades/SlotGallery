package com.pleiades.pleione.slotgallery.domain.model

data class Directory(val directoryOverview: DirectoryOverview) {
    val name = directoryOverview.toString()
    val mediaMutableList: MutableList<Media> = mutableListOf()
    val date: Long
        get() = mediaMutableList.maxOf { it.date }
}

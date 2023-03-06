package com.pleiades.pleione.slotgallery.domain.model

import com.pleiades.pleione.slotgallery.Config.Companion.PATH_PRIMARY

class Directory(val directoryOverview: DirectoryOverview) {
    val name = directoryOverview.lastPath.substringAfter(PATH_PRIMARY).substringAfterLast("/")
    val contentArrayList: ArrayList<Content> = ArrayList()
    var date = 0L

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false

        val otherDirectory = other as Directory
        return directoryOverview == otherDirectory.directoryOverview
                && date == otherDirectory.date
                && contentArrayList == otherDirectory.contentArrayList
    }

    override fun hashCode(): Int {
        var result = directoryOverview.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + contentArrayList.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }

    fun refreshDate() {
        date = contentArrayList.maxOf { it.date }
    }
}
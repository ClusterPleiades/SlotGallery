package com.pleiades.pleione.slotgallery.domain.model

import android.net.Uri
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_PRIMARY

class Directory(val directoryPath: Slot.DirectoryPath) {
    val name = directoryPath.lastPath.substringAfter(PATH_PRIMARY).substringAfterLast("/")
    val contentArrayList: ArrayList<Content> = ArrayList()
    var date = 0L

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false

        val otherDirectory = other as Directory
        return directoryPath == otherDirectory.directoryPath
                && date == otherDirectory.date
                && contentArrayList == otherDirectory.contentArrayList
    }

    override fun hashCode(): Int {
        var result = directoryPath.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + contentArrayList.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }

    fun refreshDate() {
        date = contentArrayList.maxOf { it.date }
    }
}
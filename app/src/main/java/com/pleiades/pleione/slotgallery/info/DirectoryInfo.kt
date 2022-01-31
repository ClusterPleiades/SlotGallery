package com.pleiades.pleione.slotgallery.info

import android.view.ContentInfo
import java.util.*

class DirectoryInfo(val isDefault: Boolean, val name: String) {
    val contentInfoLinkedList: LinkedList<ContentInfo> = LinkedList()

    class ContentInfo(
        val isVideo: Boolean,
        val name: String,
        val path: String,
        val size: String,
        val width: String,
        val height: String,
        val date: String
    )
}
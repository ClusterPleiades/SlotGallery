package com.pleiades.pleione.slotgallery.info

import java.util.*

class Directory(val path: String) {
    val name = path.substringAfterLast("/")
    val contentLinkedList: LinkedList<Content> = LinkedList()

    class Content(
        val isVideo: Boolean,
        val bucketId: String,
        val name: String,
        val size: String,
        val width: String,
        val height: String,
        val date: String
    )
}
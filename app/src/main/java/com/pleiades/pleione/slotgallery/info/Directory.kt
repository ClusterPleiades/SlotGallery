package com.pleiades.pleione.slotgallery.info

import java.util.*
import kotlin.collections.ArrayList

class Directory(val path: String) {
    val name = path.substringAfterLast("/")
    val contentArrayList: ArrayList<Content> = ArrayList()
    var date = 0L

    class Content(
        val isVideo: Boolean,
        val bucketId: String,
        val name: String,
        val size: String,
        val width: String,
        val height: String,
        val date: Long
    )
}
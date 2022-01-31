package com.pleiades.pleione.slotgallery.info

import android.net.Uri
import com.pleiades.pleione.slotgallery.Config
import java.util.*

class SlotInfo(var name: String) {
    val directoryPathInfoLinkedList: LinkedList<DirectoryPathInfo> = LinkedList()

    init {
        directoryPathInfoLinkedList.add(DirectoryPathInfo(Config.PATH_DOWNLOAD))
        directoryPathInfoLinkedList.add(DirectoryPathInfo(Config.PATH_CAMERA))
        directoryPathInfoLinkedList.add(DirectoryPathInfo(Config.PATH_SCREENSHOTS))
    }

    class DirectoryPathInfo {
        val treeUriString: String?
        val lastPathSegment: String
        var isVisible = true

        constructor(treeUri: Uri) {
            this.treeUriString = treeUri.toString()
            lastPathSegment = treeUri.lastPathSegment!!
        }

        constructor(lastPathSegment: String) {
            this.treeUriString = null
            this.lastPathSegment = lastPathSegment
        }
    }
}
package com.pleiades.pleione.slotgallery.domain.model

import com.pleiades.pleione.slotgallery.Config.Companion.PATH_CAMERA
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_DOWNLOAD
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_SCREENSHOTS
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_SNAPSEED
import java.util.*

class Slot(var name: String) {
    val directoryPathLinkedList: LinkedList<DirectoryPath> = LinkedList()
    val isVisible = arrayOf(true, true, true, true) // download, snapseed, camera, screenshot

    init {
        directoryPathLinkedList.add(DirectoryPath(lastPath = PATH_DOWNLOAD))
        directoryPathLinkedList.add(DirectoryPath(lastPath = PATH_SNAPSEED))
        directoryPathLinkedList.add(DirectoryPath(lastPath = PATH_CAMERA))
        directoryPathLinkedList.add(DirectoryPath(lastPath = PATH_SCREENSHOTS))
    }

    data class DirectoryPath(
        val rootUriString: String? = null,
        val lastPath: String
    )
}
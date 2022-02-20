package com.pleiades.pleione.slotgallery.info

import com.pleiades.pleione.slotgallery.Config.Companion.PATH_CAMERA
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_DOWNLOAD
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_SCREENSHOTS
import java.util.*

class Slot(var name: String) {
    val directoryPathLinkedList: LinkedList<String> = LinkedList()
    val isVisible = arrayOf(true, true, true) // download, camera, screenshot

    init {
        directoryPathLinkedList.add(PATH_DOWNLOAD)
        directoryPathLinkedList.add(PATH_CAMERA)
        directoryPathLinkedList.add(PATH_SCREENSHOTS)
    }
}
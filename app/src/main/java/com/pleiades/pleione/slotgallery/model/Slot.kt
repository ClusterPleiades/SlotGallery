package com.pleiades.pleione.slotgallery.model

import com.pleiades.pleione.slotgallery.Config
import java.util.*

class Slot(var name: String) {
    var directoryLinkedList: LinkedList<Directory> = LinkedList()

    init {
        directoryLinkedList.add(Directory(Config.PATH_DOWNLOAD))
        directoryLinkedList.add(Directory(Config.PATH_CAMERA))
        directoryLinkedList.add(Directory(Config.PATH_SCREENSHOTS))
    }
}
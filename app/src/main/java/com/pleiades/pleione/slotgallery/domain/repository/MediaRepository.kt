package com.pleiades.pleione.slotgallery.domain.repository

import com.pleiades.pleione.slotgallery.domain.model.Media
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.model.Slot

interface MediaRepository {
    fun getDirectoryList(selectedSlot: Slot): List<Directory>

    suspend fun copyDirectory(
        fromDirectoryList: List<Directory>,
        toDirectory: Directory,
        setMaxProgress: (Int) -> Unit,
        progress: () -> Unit
    )

    suspend fun copyMedia(
        fromDirectory: Directory,
        toDirectory: Directory,
        mediaSet: Set<Media>
    )
}
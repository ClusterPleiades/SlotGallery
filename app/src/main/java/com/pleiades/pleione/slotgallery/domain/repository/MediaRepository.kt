package com.pleiades.pleione.slotgallery.domain.repository

import com.pleiades.pleione.slotgallery.domain.model.Media
import com.pleiades.pleione.slotgallery.domain.model.Directory

interface MediaRepository {
    fun getDirectoryList(): List<Directory>

    suspend fun copyDirectory(
        fromDirectorySet: Set<Directory>,
        toDirectory: Directory
    )

    suspend fun copyMedia(
        fromDirectory: Directory,
        toDirectory: Directory,
        mediaSet: Set<Media>
    )
}
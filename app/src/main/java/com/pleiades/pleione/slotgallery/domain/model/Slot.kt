package com.pleiades.pleione.slotgallery.domain.model

import com.pleiades.pleione.slotgallery.Config.Companion.PATH_CAMERA
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_DOWNLOAD
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_SCREENSHOTS
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_SNAPSEED

data class Slot(val name: String) {
    val directoryPathList: List<DirectoryPath> =
        listOf(
            DirectoryPath(lastPath = PATH_DOWNLOAD, isVisible = true),
            DirectoryPath(lastPath = PATH_SNAPSEED, isVisible = true),
            DirectoryPath(lastPath = PATH_CAMERA, isVisible = true),
            DirectoryPath(lastPath = PATH_SCREENSHOTS, isVisible = true)
        )

    data class DirectoryPath(
        val rootUriString: String? = null,
        val lastPath: String,
        val isVisible: Boolean = true
    )
}
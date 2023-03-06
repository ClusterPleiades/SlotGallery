package com.pleiades.pleione.slotgallery.domain.model

import com.pleiades.pleione.slotgallery.Config.Companion.PATH_CAMERA
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_DOWNLOAD
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_SCREENSHOTS
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_SNAPSEED

data class Slot(val name: String) {
    val directoryOverviewLists: List<DirectoryOverview> =
        listOf(
            DirectoryOverview(
                lastPath = PATH_DOWNLOAD,
                isVisible = true
            ),
            DirectoryOverview(
                lastPath = PATH_SNAPSEED,
                isVisible = true
            ),
            DirectoryOverview(
                lastPath = PATH_CAMERA,
                isVisible = true
            ),
            DirectoryOverview(
                lastPath = PATH_SCREENSHOTS,
                isVisible = true
            )
        )
}
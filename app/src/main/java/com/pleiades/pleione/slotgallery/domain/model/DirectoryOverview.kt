package com.pleiades.pleione.slotgallery.domain.model

import com.pleiades.pleione.slotgallery.Config.Companion.URI_DEFAULT_DIRECTORY

data class DirectoryOverview(
    val uri: String = URI_DEFAULT_DIRECTORY,
    val lastPath: String,
    var isVisible: Boolean = true
)

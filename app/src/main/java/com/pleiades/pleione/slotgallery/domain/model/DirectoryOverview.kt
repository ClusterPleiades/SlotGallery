package com.pleiades.pleione.slotgallery.domain.model

import android.os.Parcelable
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.Config.Companion.URI_DEFAULT_DIRECTORY
import kotlinx.parcelize.Parcelize

@Parcelize
data class DirectoryOverview(
    val uri: String = URI_DEFAULT_DIRECTORY,
    val lastPath: String,
    var isVisible: Boolean = true
) : Parcelable {
    override fun toString(): String = lastPath.substringAfter(Config.PATH_PRIMARY).substringAfterLast("/")
}

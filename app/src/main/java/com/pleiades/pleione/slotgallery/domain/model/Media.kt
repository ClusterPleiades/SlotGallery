package com.pleiades.pleione.slotgallery.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Media(
    val isVideo: Boolean,
    val id: String,

    var name: String,
    val size: String,
    val width: Int,
    val height: Int,
    var date: Long,
    val relativePath: String,

    val uri: Uri,
    val duration: Long
) : Parcelable

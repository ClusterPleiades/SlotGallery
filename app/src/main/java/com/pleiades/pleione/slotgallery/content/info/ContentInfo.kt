package com.pleiades.pleione.slotgallery.content.info

import android.media.MediaMetadataRetriever

class ContentInfo(
    private val isVideo: Boolean,
    private val name: String,
    private val path: String,
    private val size: String,
    private val width: String,
    private val height: String,
    private val date: String
) {
    private val directoryPath = path.substringBeforeLast("/")
    private val directoryName = directoryPath.substringAfterLast("/")
}
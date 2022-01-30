package com.pleiades.pleione.slotgallery.model

import android.net.Uri

class Directory {
    var treeUriString: String?
    var lastPathSegment: String
    var isVisible = true

    constructor(treeUri: Uri) {
        this.treeUriString = treeUri.toString()
        lastPathSegment = treeUri.lastPathSegment!!
    }

    constructor(lastPathSegment: String) {
        this.treeUriString = null
        this.lastPathSegment = lastPathSegment
    }
}
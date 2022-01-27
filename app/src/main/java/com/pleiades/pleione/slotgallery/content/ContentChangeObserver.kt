package com.pleiades.pleione.slotgallery.content

import android.database.ContentObserver
import android.os.Handler

class ContentChangeObserver(handler: Handler?) : ContentObserver(handler) {
    companion object {
        var isContentChanged = false
    }

    override fun onChange(selfChange: Boolean) {
        isContentChanged = selfChange
        super.onChange(selfChange)
    }
}
package com.pleiades.pleione.slotgallery

import android.database.ContentObserver
import android.os.Handler

class ContentChangeObserver(handler: Handler?) : ContentObserver(handler) {
    companion object {
        var isContentChanged = true
    }

    override fun onChange(selfChange: Boolean) {
        isContentChanged = selfChange
        super.onChange(selfChange)
    }
}
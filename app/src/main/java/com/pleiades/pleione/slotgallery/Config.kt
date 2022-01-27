package com.pleiades.pleione.slotgallery

import android.Manifest

class Config {
    companion object {
        // permission
        val PERMISSION_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        // dialog
        const val DIALOG_TYPE_PERMISSION = 0

        // request code
        const val REQUEST_CODE_PERMISSION = 1000
    }
}
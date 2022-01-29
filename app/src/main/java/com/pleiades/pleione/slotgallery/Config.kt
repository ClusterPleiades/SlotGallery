package com.pleiades.pleione.slotgallery

import android.Manifest

class Config {
    companion object {
        // permission
        val PERMISSION_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        // dialog
        const val DIALOG_TYPE_PERMISSION = 0

        // request code
        const val REQUEST_CODE_PERMISSION = 1000

        // fragment
        const val KEY_STACK = "stack"

        // setting
        const val SETTING_POSITION_SLOT = 0
        const val SETTING_POSITION_DIRECTORY = 1

        // directory
        const val PATH_DOWNLOAD = "Download"
        const val PATH_CAMERA = "DCIM/Camera"
        const val PATH_SCREENSHOTS = "Pictures/ScreenShots"
        const val COUNT_DEFAULT_DIRECTORY = 3

        // prefs
        const val PREFS = "prefs"
        const val KEY_SLOT_LIST = "slot_list"
        const val KEY_SELECTED_SLOT_POSITION = "selected_slot"
    }
}
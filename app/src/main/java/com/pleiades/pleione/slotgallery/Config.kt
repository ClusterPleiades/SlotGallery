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

        // recycler view
        const val KEY_SCROLL_POSITION = "scroll_position"
        const val SPAN_COUNT_DIRECTORY = 2

        // setting
        const val SETTING_POSITION_SLOT = 0
        const val SETTING_POSITION_DIRECTORY = 1

        // prefs
        const val PREFS = "prefs"
        const val KEY_SLOT_LIST = "slot_list"
        const val KEY_SELECTED_SLOT_POSITION = "selected_slot"
    }
}
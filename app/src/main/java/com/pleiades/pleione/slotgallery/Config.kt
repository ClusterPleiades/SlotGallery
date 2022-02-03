package com.pleiades.pleione.slotgallery

import android.Manifest

class Config {
    companion object {
        // permission
        val PERMISSION_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        // request code
        const val REQUEST_CODE_PERMISSION = 1000

        // dialog
        const val DIALOG_TYPE_PERMISSION = 0
        const val DIALOG_TYPE_SORT_DIRECTORY = 10
        const val DIALOG_TYPE_SORT_IMAGE = 11
        const val DIALOG_WIDTH_PERCENTAGE_DEFAULT = 0.85
        const val DIALOG_WIDTH_PERCENTAGE_RECYCLER = 0.65

        // fragment
        const val KEY_STACK = "stack"
        const val KEY_PARCELABLE = "parcelable"

        // recycler view
        const val SPAN_COUNT_DIRECTORY = 2

        // setting
        const val SETTING_POSITION_SLOT = 0
        const val SETTING_POSITION_DIRECTORY = 1
        const val PATH_DOWNLOAD = "Download"
        const val PATH_CAMERA = "DCIM/Camera"
        const val PATH_SCREENSHOTS = "Pictures/Screenshots"
        const val COUNT_DEFAULT_DIRECTORY = 3

        // prefs
        const val PREFS = "prefs"
        const val KEY_SLOT_LIST = "slot_list"
        const val KEY_SELECTED_SLOT_POSITION = "selected_slot"
        const val KEY_DIRECTORY_SORT_ORDER = "directory_sort_order"
        const val SORT_POSITION_BY_NAME = 0
        const val SORT_POSITION_BY_NEWEST = 1
        const val SORT_POSITION_BY_OLDEST = 2
    }
}
package com.pleiades.pleione.slotgallery

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

class Config {
    companion object {
        // permission
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val PERMISSION_IMAGES_VIDEOS = arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        val PERMISSION_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        // request code
        const val REQUEST_CODE_PERMISSION_STORAGE = 1000
        const val REQUEST_CODE_PERMISSION_IMAGES_VIDEOS = 1001

        // dialog
        const val DIALOG_TYPE_PERMISSION = 0
        const val DIALOG_TYPE_SORT_DIRECTORY = 10
        const val DIALOG_TYPE_SORT_MEDIA = 11
        const val DIALOG_TYPE_INFORMATION = 12
        const val DIALOG_TYPE_COPY_DIRECTORY = 13
        const val DIALOG_WIDTH_PERCENTAGE_DEFAULT = 0.85
        const val DIALOG_WIDTH_PERCENTAGE_RECYCLER = 0.65

        // activity
        const val INTENT_EXTRA_POSITION_DIRECTORY = "position_directory"
        const val INTENT_EXTRA_POSITION_MEDIA = "position_media"
        const val INTENT_EXTRA_DIRECTORY_OVERVIEW_URI = "directory_overview_uri"
        const val INTENT_EXTRA_DIRECTORY_OVERVIEW_LAST_PATH = "directory_overview_last_path"
        const val INTENT_EXTRA_NAME = "name"
        const val INTENT_EXTRA_URI = "uri"

        // fragment
        const val KEY_STACK = "stack"
        const val KEY_DIRECTORY_POSITION = "position_directory"
        const val KEY_COPY_COMPLETE = "copy_complete"
        const val ACTIVITY_CODE_MAIN = 0
        const val ACTIVITY_CODE_SETTING = 1
        const val ACTIVITY_CODE_IMAGE = 2
        const val ACTIVITY_CODE_VIDEO = 3
        const val ACTIVITY_CODE_CHOICE = 4

        // recycler view
        const val SPAN_COUNT_DIRECTORY = 2
        const val SPAN_COUNT_MEDIA = 3
        const val INFORMATION_POSITION_NAME = 0
        const val INFORMATION_POSITION_DATE = 1
        const val INFORMATION_POSITION_TIME = 2
        const val INFORMATION_POSITION_SIZE = 3
        const val INFORMATION_POSITION_WIDTH = 4
        const val INFORMATION_POSITION_HEIGHT = 5
        const val INFORMATION_POSITION_PATH = 6

        // setting
        const val SETTING_POSITION_SLOT = 0
        const val SETTING_POSITION_DIRECTORY = 1
        const val PATH_PRIMARY = "primary:"
        const val PATH_DOWNLOAD = "Download"
        const val PATH_SNAPSEED = "Snapseed"
        const val PATH_CAMERA = "DCIM/Camera"
        const val PATH_SCREENSHOTS = "Pictures/Screenshots"
        const val COUNT_DEFAULT_DIRECTORY = 4

        // prefs
        const val PREFS = "prefs"
        const val KEY_SLOT_LIST = "slot_list"
        const val KEY_SELECTED_SLOT_POSITION = "selected_slot"
        const val KEY_DIRECTORY_SORT_ORDER = "directory_sort_order"
        const val KEY_MEDIA_SORT_ORDER = "media_sort_order"
        const val VALUE_SORT_POSITION_BY_NAME = 0
        const val VALUE_SORT_POSITION_BY_NEWEST = 1
        const val VALUE_SORT_POSITION_BY_OLDEST = 2

        // others
        const val MIME_TYPE_IMAGE = "image/*"
        const val MIME_TYPE_VIDEO = "video/*"
        const val MIME_TYPE_ALL = "*/*"
        const val FORMAT_DATE = "yyyy-MM-dd"
        const val FORMAT_TIME = "HH:mm"
        const val NAME_DUMMY = "empty.png"

        const val PACKAGE_NAME_EDIT = "com.niksoftware.snapseed"
        const val STORE_URL_EDIT = "https://play.google.com/store/apps/details?id=com.niksoftware.snapseed"

        const val URI_DEFAULT_DIRECTORY = "default_uri"
    }
}

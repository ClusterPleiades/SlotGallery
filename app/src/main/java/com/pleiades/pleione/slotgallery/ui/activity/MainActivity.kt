package com.pleiades.pleione.slotgallery.ui.activity

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_PERMISSION
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_STORAGE
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.content.ContentChangeObserver
import com.pleiades.pleione.slotgallery.content.ContentController
import com.pleiades.pleione.slotgallery.ui.fragment.dialog.DefaultDialogFragment

class MainActivity : AppCompatActivity() {
    private lateinit var contentChangeObserver: ContentChangeObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set navigation color
        window.navigationBarColor = Color.WHITE

        // register content change observer
        val handler = Handler(Looper.getMainLooper())
        contentChangeObserver = ContentChangeObserver(handler)
        contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentChangeObserver)
        contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, contentChangeObserver)
    }

    override fun onResume() {
        // check permission
        if (isPermissionGranted()) {
            initialize()
        } else {
            val defaultDialogFragment = DefaultDialogFragment(DIALOG_TYPE_PERMISSION)
            defaultDialogFragment.show(supportFragmentManager, DIALOG_TYPE_PERMISSION.toString())
        }

        super.onResume()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initialize()
        }
    }

    override fun onDestroy() {
        contentResolver.unregisterContentObserver(contentChangeObserver)
        super.onDestroy()
    }

    private fun isPermissionGranted(): Boolean {
        for (permission in PERMISSION_STORAGE)
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false
        return true
    }

    private fun initialize() {
        ContentController(this).initialize()
    }
}
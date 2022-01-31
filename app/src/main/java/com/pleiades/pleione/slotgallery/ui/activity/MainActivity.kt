package com.pleiades.pleione.slotgallery.ui.activity

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_PERMISSION
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_STORAGE
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.ContentChangeObserver
import com.pleiades.pleione.slotgallery.ui.fragment.main.DirectoryFragment
import com.pleiades.pleione.slotgallery.ui.fragment.dialog.DefaultDialogFragment

class MainActivity : AppCompatActivity() {
    private lateinit var contentChangeObserver: ContentChangeObserver
    private var isInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set title
        title = ""

        // set navigation color
        window.navigationBarColor = Color.WHITE

        // initialize appbar
        val appbar = findViewById<View>(R.id.appbar_main)
        val toolbar: Toolbar = appbar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        // check permission
        if (checkSelfPermission(PERMISSION_STORAGE[0]) == PackageManager.PERMISSION_GRANTED) {
            initialize()
        } else {
            // request permission
            val defaultDialogFragment = DefaultDialogFragment(DIALOG_TYPE_PERMISSION)
            defaultDialogFragment.show(supportFragmentManager, DIALOG_TYPE_PERMISSION.toString())
        }

        super.onResume()
    }

    override fun onDestroy() {
        contentResolver.unregisterContentObserver(contentChangeObserver)
        super.onDestroy()
    }

    override fun onBackPressed() {
        var result = false
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_main) as DirectoryFragment?
        if (fragment != null) {
            result = fragment.onBackPressed()
        }
        if (!result) super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initialize()
        }
    }

    private fun initialize() {
        // check is initialized
        if (isInitialized)
            return

        // add fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_main, DirectoryFragment.newInstance()).commit()

        // register content change observer
        val handler = Handler(Looper.getMainLooper())
        contentChangeObserver = ContentChangeObserver(handler)
        contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentChangeObserver)
        contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, contentChangeObserver)

        // set is initialized true
        isInitialized = true
    }
}
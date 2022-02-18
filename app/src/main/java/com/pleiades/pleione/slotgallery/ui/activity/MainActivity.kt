package com.pleiades.pleione.slotgallery.ui.activity

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_MAIN
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_PERMISSION
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_STORAGE
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.ui.fragment.dialog.DefaultDialogFragment
import com.pleiades.pleione.slotgallery.ui.fragment.main.ContentFragment
import com.pleiades.pleione.slotgallery.ui.fragment.main.DirectoryFragment

class MainActivity : AppCompatActivity() {
    companion object {
        var lastResumedActivityCode = ACTIVITY_CODE_MAIN
    }

    private var isFragmentAdded = false

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

    override fun onStart() {
        // check permission
        if (checkSelfPermission(PERMISSION_STORAGE[0]) == PackageManager.PERMISSION_GRANTED) {
            // add fragment
            if (!isFragmentAdded)
                addFragment()

            // initialize fragment
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_main)

            when (lastResumedActivityCode) {
                // refresh fragment
                ACTIVITY_CODE_MAIN -> {
                    when (fragment) {
                        is DirectoryFragment -> fragment.refresh()
                        is ContentFragment -> fragment.refresh()
                    }
                }
                // notify item set changed (content change applied)
                ACTIVITY_CODE_IMAGE -> {
                    (fragment as ContentFragment).notifyDataSetChanged()
                }
            }
        } else {
            // request permission
            val defaultDialogFragment = DefaultDialogFragment(DIALOG_TYPE_PERMISSION)
            defaultDialogFragment.show(supportFragmentManager, DIALOG_TYPE_PERMISSION.toString())
        }

        super.onStart()
    }

    override fun onResume() {
        // set last resumed activity code
        lastResumedActivityCode = ACTIVITY_CODE_MAIN

        super.onResume()
    }

    override fun onBackPressed() {
        var isSelecting = false

        // initialize fragment
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_main)

        if (fragment is DirectoryFragment)
            isSelecting = fragment.onBackPressed()
        else if (fragment is ContentFragment)
            isSelecting = fragment.onBackPressed()

        if (!isSelecting)
            super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addFragment()
        }
    }

    private fun addFragment() {
        // add fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_main, DirectoryFragment.newInstance()).commit()

        // set is initialized true
        isFragmentAdded = true
    }
}
package com.pleiades.pleione.slotgallery.ui.activity

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_PERMISSION
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_STORAGE
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.ui.fragment.dialog.DefaultDialogFragment
import com.pleiades.pleione.slotgallery.ui.fragment.main.ContentFragment
import com.pleiades.pleione.slotgallery.ui.fragment.main.DirectoryFragment

class MainActivity : AppCompatActivity() {
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

    override fun onResume() {
        Log.d("test", "onresume")
        // check permission
        if (checkSelfPermission(PERMISSION_STORAGE[0]) == PackageManager.PERMISSION_GRANTED) {
            // add fragment
            if (!isFragmentAdded)
                AddFragment()

            // initialize fragment
            when (val fragment = supportFragmentManager.findFragmentById(R.id.fragment_main)) {
                is DirectoryFragment -> fragment.refresh()
                is ContentFragment -> fragment.refresh()
            }
        } else {
            // request permission
            val defaultDialogFragment = DefaultDialogFragment(DIALOG_TYPE_PERMISSION)
            defaultDialogFragment.show(supportFragmentManager, DIALOG_TYPE_PERMISSION.toString())
        }

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
            AddFragment()
        }
    }

    private fun AddFragment() {
        // add fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_main, DirectoryFragment.newInstance()).commit()

        // set is initialized true
        isFragmentAdded = true
    }
}
package com.pleiades.pleione.slotgallery.presentation.main

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_MAIN
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_SETTING
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_PERMISSION
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_USER_LAST_VERSION_CODE
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_IMAGES_VIDEOS
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_STORAGE
import com.pleiades.pleione.slotgallery.Config.Companion.PREFS
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.ActivityMainBinding
import com.pleiades.pleione.slotgallery.presentation.dialog.MessageDialogFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        var lastResumedActivityCode = ACTIVITY_CODE_MAIN
    }

    private var isFragmentAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set title
        title = ""

        // initialize appbar
        setSupportActionBar(binding.appbarMain.toolbar)

        // update app version
        updateAppVersion()
    }

    override fun onStart() {
        // check permission
        val isGranted =
            if (Build.VERSION.SDK_INT >= 33)
                checkSelfPermission(PERMISSION_IMAGES_VIDEOS[0]) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_IMAGES_VIDEOS[1]) == PackageManager.PERMISSION_GRANTED
            else
                checkSelfPermission(PERMISSION_STORAGE[0]) == PackageManager.PERMISSION_GRANTED

        // case permission granted
        if (isGranted) {
            // add fragment
            if (!isFragmentAdded)
                addFragment()

            // initialize fragment
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_main)

            when (lastResumedActivityCode) {
                // refresh fragment
                ACTIVITY_CODE_MAIN,
                ACTIVITY_CODE_SETTING -> {
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
            val messageDialogFragment = MessageDialogFragment(DIALOG_TYPE_PERMISSION)
            messageDialogFragment.show(supportFragmentManager, DIALOG_TYPE_PERMISSION.toString())
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

    // TODO check
    private fun updateAppVersion() {
        try {
            val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
            val editor = prefs.edit()

            // initialize version code
            val existVersionCode = prefs.getInt(KEY_USER_LAST_VERSION_CODE, 1)
            val latestVersionCode = packageManager.getPackageInfo(packageName, 0).versionCode

            // case version code is not latest
            if (existVersionCode != latestVersionCode) {
                // case prev 1.2.0 (add snapseed)
                if (existVersionCode < 5) {
                    editor.clear()
                    editor.apply()
                }

                // apply app version
                editor.putInt(KEY_USER_LAST_VERSION_CODE, latestVersionCode)
                editor.apply()
            }
        } catch (e: Exception) {
            // ignore exception block
        }
    }
}
package com.pleiades.pleione.slotgallery.presentation.main

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_PERMISSION
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_IMAGES_VIDEOS
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_STORAGE
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.ActivityMainBinding
import com.pleiades.pleione.slotgallery.presentation.main.dialog.message.MessageDialogFragment
import com.pleiades.pleione.slotgallery.presentation.main.directory.DirectoryFragment
import com.pleiades.pleione.slotgallery.presentation.main.directory.inside.DirectoryInsideFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val activityViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // title
        title = ""

        // action bar
        setSupportActionBar(binding.appbar.toolbar)
    }

    override fun onStart() {
        val isPermissionGranted =
            if (Build.VERSION.SDK_INT >= 33) {
                checkSelfPermission(PERMISSION_IMAGES_VIDEOS[0]) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_IMAGES_VIDEOS[1]) == PackageManager.PERMISSION_GRANTED
            } else {
                checkSelfPermission(PERMISSION_STORAGE[0]) == PackageManager.PERMISSION_GRANTED
            }

        if (isPermissionGranted) {
            if (!activityViewModel.isFragmentAdded) addFragment()
        } else {
            MessageDialogFragment(DIALOG_TYPE_PERMISSION)
                .show(supportFragmentManager, DIALOG_TYPE_PERMISSION.toString())
        }

        super.onStart()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (!activityViewModel.isFragmentAdded) addFragment()
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (fragment is DirectoryFragment) {
            if (!fragment.onBackPressed()) super.onBackPressed()
        } else if (fragment is DirectoryInsideFragment) {
            if (!fragment.onBackPressed()) super.onBackPressed()
        }
    }

    private fun addFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, DirectoryFragment())
            .commit()

        // set is initialized true
        activityViewModel.isFragmentAdded = true
    }
}

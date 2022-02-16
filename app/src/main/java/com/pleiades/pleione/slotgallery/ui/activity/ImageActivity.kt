package com.pleiades.pleione.slotgallery.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_POSITION_CONTENT
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_POSITION_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController
import com.pleiades.pleione.slotgallery.ui.fragment.main.ImageFragment

class ImageActivity : AppCompatActivity() {
    private var directoryPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        // set navigation color
        window.navigationBarColor = Color.WHITE

        // initialize appbar
        val appbar = findViewById<View>(R.id.appbar_image)
        val toolbar: Toolbar = appbar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // get intent extra
        directoryPosition = intent.getIntExtra(INTENT_POSITION_DIRECTORY, 0)
        val contentPosition = intent.getIntExtra(INTENT_POSITION_CONTENT, 0)

        // initialize view pager
        val viewPager = findViewById<ViewPager2>(R.id.pager_image)
        val contentsPagerAdapter = ImageFragmentStateAdapter(supportFragmentManager, lifecycle)
        viewPager.offscreenPageLimit = 5
        viewPager.adapter = contentsPagerAdapter
        viewPager.setCurrentItem(contentPosition, false)
    }

    override fun onResume() {
        // set last resumed activity code
        MainActivity.lastResumedActivityCode = ACTIVITY_CODE_IMAGE

        super.onResume()
    }

    inner class ImageFragmentStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun createFragment(position: Int): Fragment {
            return ImageFragment.newInstance(directoryPosition, position)
        }

        override fun getItemCount(): Int {
            return ContentController.directoryArrayList[directoryPosition].contentArrayList.size
        }
    }
}
package com.pleiades.pleione.slotgallery.presentation.setting

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // action bar
        setSupportActionBar(binding.appbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // fragment
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, SettingFragment())
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
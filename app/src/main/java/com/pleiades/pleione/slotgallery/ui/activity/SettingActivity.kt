package com.pleiades.pleione.slotgallery.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_SETTING
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.ActivityImageBinding
import com.pleiades.pleione.slotgallery.databinding.ActivitySettingBinding
import com.pleiades.pleione.slotgallery.ui.fragment.setting.SettingFragment

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize appbar
        setSupportActionBar(binding.appbarSetting.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // add fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_setting, SettingFragment.newInstance()).commit()
    }

    override fun onResume() {
        // set last resumed activity code
        MainActivity.lastResumedActivityCode = ACTIVITY_CODE_SETTING

        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
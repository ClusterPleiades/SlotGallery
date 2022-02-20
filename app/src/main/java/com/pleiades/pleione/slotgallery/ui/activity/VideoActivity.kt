package com.pleiades.pleione.slotgallery.ui.activity

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_VIDEO
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_NAME
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_URI
import com.pleiades.pleione.slotgallery.R

class VideoActivity : AppCompatActivity() {
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // set navigation color
        window.navigationBarColor = Color.WHITE

        // initialize appbar
        val appbar = findViewById<View>(R.id.appbar_video)
        val toolbar: Toolbar = appbar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra(INTENT_EXTRA_NAME)

        // initialize uri from intent extra
        val uri = Uri.parse(intent.getStringExtra(INTENT_EXTRA_URI))

        // initialize player
        playerView = findViewById(R.id.player_video);
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        playerView.player = exoPlayer

        // play
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onResume() {
        // set last resumed activity code
        MainActivity.lastResumedActivityCode = ACTIVITY_CODE_VIDEO

        super.onResume()
    }

    override fun onPause() {
        // pause player
        exoPlayer.pause()
        exoPlayer.playWhenReady = true

        super.onPause()
    }

    override fun onStop() {
        // stop player
        exoPlayer.stop()
        exoPlayer.playWhenReady = false

        super.onStop()
    }

    override fun onDestroy() {
        // release player
        exoPlayer.release()

        super.onDestroy()
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
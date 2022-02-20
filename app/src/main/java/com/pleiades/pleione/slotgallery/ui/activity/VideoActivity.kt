package com.pleiades.pleione.slotgallery.ui.activity

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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

    private var isFull = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_video, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.full -> {
                if (isFull) {
//                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
//                    supportActionBar!!.show()
//                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                    val params = playerView.layoutParams as RelativeLayout.LayoutParams
//                    params.width = ViewGroup.LayoutParams.MATCH_PARENT
//                    params.height = (200 * applicationContext.resources.displayMetrics.density).toInt()
//                    playerView.layoutParams = params
//                    fullscreen = false
                } else {
                    // hide system UI
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    WindowInsetsControllerCompat(window, window.decorView.rootView).let { controller ->
                        controller.hide(WindowInsetsCompat.Type.systemBars())
                        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }

                    // hide action bar
                    supportActionBar!!.hide()

                    // set orientation
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

//                    val params = playerView.layoutParams as RelativeLayout.LayoutParams
//                    params.width = ViewGroup.LayoutParams.MATCH_PARENT
//                    params.height = ViewGroup.LayoutParams.MATCH_PARENT
//                    playerView.layoutParams = params

                    // set is full
                    isFull = true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
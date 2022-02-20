package com.pleiades.pleione.slotgallery.ui.activity

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_VIDEO
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_IS_PORTRAIT
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

        // set decor fits system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // initialize appbar
        val appbar = findViewById<View>(R.id.appbar_video)
        val toolbar: Toolbar = appbar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // set toolbar margin
        val statusBarHeightResId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val toolbarLayoutParams = toolbar.layoutParams as ViewGroup.MarginLayoutParams
        toolbarLayoutParams.topMargin = resources.getDimensionPixelSize(statusBarHeightResId)
        toolbar.layoutParams = toolbarLayoutParams


        // set title from intent extra
        title = intent.getStringExtra(INTENT_EXTRA_NAME)

        // initialize uri from intent extra
        val uri = Uri.parse(intent.getStringExtra(INTENT_EXTRA_URI))

        // initialize player
        playerView = findViewById(R.id.player_video)
        playerView.setOnClickListener { fullVideo() }
        val navigationBarHeightResId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val playerViewLayoutParams = playerView.layoutParams as ViewGroup.MarginLayoutParams
        playerViewLayoutParams.bottomMargin = resources.getDimensionPixelSize(navigationBarHeightResId)
        playerView.layoutParams = playerViewLayoutParams

        // initialize exoplayer
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(@Player.State state: Int) {
                if (state == Player.STATE_ENDED) {
                    // un full force
                    isFull = true
                    fullVideo()
                }
            }
        })
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))

        // set exoplayer as player
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
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fullVideo() {
        if (isFull) {
            // show system UI
//                WindowInsetsControllerCompat(window, window.decorView.rootView).show(WindowInsetsCompat.Type.systemBars())

            // show action bar
            supportActionBar!!.show()

            // set orientation
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            // hide system UI
//                WindowInsetsControllerCompat(window, window.decorView.rootView).let { controller ->
//                    controller.hide(WindowInsetsCompat.Type.systemBars())
//                    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//                }

            // hide action bar
            supportActionBar!!.hide()

            // set orientation
//                requestedOrientation = if (isPortrait) ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }

        // set is full
        isFull = !isFull
    }
}
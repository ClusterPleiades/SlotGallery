package com.pleiades.pleione.slotgallery.ui.activity

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_VIDEO
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_NAME
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_URI
import com.pleiades.pleione.slotgallery.R


class VideoActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarLayoutParams: ViewGroup.MarginLayoutParams
    private var statusBarHeight = 0

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: StyledPlayerView
    private lateinit var playerViewLayoutParams: ViewGroup.MarginLayoutParams
    private var navigationBarHeight = 0

    private var isFull = false
    private var isRotated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // set decor fits system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // initialize toolbar
        val appbar = findViewById<View>(R.id.appbar_video)
        toolbar = appbar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // initialize toolbar layout params
        toolbarLayoutParams = toolbar.layoutParams as ViewGroup.MarginLayoutParams

        // initialize status bar height
        val statusBarHeightResId = resources.getIdentifier("status_bar_height", "dimen", "android")
        statusBarHeight = resources.getDimensionPixelSize(statusBarHeightResId)

        // set toolbar margin
        setToolbarMargin(true)

        // set title from intent extra
        title = intent.getStringExtra(INTENT_EXTRA_NAME)

        // initialize uri from intent extra
        val uri = Uri.parse(intent.getStringExtra(INTENT_EXTRA_URI))

        // initialize player
        playerView = findViewById(R.id.player_video)
        playerView.setOnClickListener { fullVideo() }

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

        // initialize player view layout params
        playerViewLayoutParams = playerView.layoutParams as ViewGroup.MarginLayoutParams

        // initialize navigation bar height
        val navigationBarHeightResId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        navigationBarHeight = resources.getDimensionPixelSize(navigationBarHeightResId)

        // set player view margin
        setPlayerViewMargin(true)

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
            R.id.rotate -> {
                rotateVideo()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fullVideo() {
        if (isFull) {
            // show action bar
            supportActionBar!!.show()
        } else {
            // hide action bar
            supportActionBar!!.hide()
        }

        // set is full
        isFull = !isFull
    }

    private fun rotateVideo() {
        if (isRotated) {
            // show system UI
            WindowInsetsControllerCompat(window, window.decorView.rootView).show(WindowInsetsCompat.Type.systemBars())

            // set toolbar margin
            setToolbarMargin(true)

            // set player view margin
            setPlayerViewMargin(true)

            // set orientation
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            // hide system UI
            WindowInsetsControllerCompat(window, window.decorView.rootView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            // set toolbar margin
            setToolbarMargin(false)

            // set player view margin
            setPlayerViewMargin(false)

            // set orientation
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }

        // set is rotated
        isRotated = !isRotated
    }

    private fun setToolbarMargin(apply: Boolean) {
        toolbarLayoutParams.topMargin = if (apply) statusBarHeight else 0
        toolbar.layoutParams = toolbarLayoutParams
    }

    private fun setPlayerViewMargin(apply: Boolean) {
        playerViewLayoutParams.bottomMargin = if (apply) navigationBarHeight else 0
        playerView.layoutParams = playerViewLayoutParams
    }
}
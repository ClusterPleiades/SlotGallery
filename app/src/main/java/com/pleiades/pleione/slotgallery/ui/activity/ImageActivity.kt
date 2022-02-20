package com.pleiades.pleione.slotgallery.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_INFORMATION
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_POSITION_CONTENT
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_POSITION_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.SHARE_TYPE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.SHARE_TYPE_VIDEO
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController
import com.pleiades.pleione.slotgallery.info.Directory
import com.pleiades.pleione.slotgallery.ui.fragment.dialog.RecyclerDialogFragment
import com.pleiades.pleione.slotgallery.ui.fragment.main.ImageFragment

class ImageActivity : AppCompatActivity() {
    private lateinit var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var viewPager: ViewPager2
    private lateinit var contentsPagerAdapter: ImageFragmentStateAdapter

    private var directoryPosition = 0
    private var isFull = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        // set decor fits system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // initialize appbar
        val appbar = findViewById<View>(R.id.appbar_image)
        val toolbar: Toolbar = appbar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // set toolbar margin
        val statusBarHeightResId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val toolbarLayoutParams = toolbar.layoutParams as ViewGroup.MarginLayoutParams
        toolbarLayoutParams.topMargin = resources.getDimensionPixelSize(statusBarHeightResId)
        toolbar.layoutParams = toolbarLayoutParams

        // get intent extra
        directoryPosition = intent.getIntExtra(INTENT_EXTRA_POSITION_DIRECTORY, 0)
        val contentPosition = intent.getIntExtra(INTENT_EXTRA_POSITION_CONTENT, 0)

        // initialize activity result launcher
        deleteResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // initialize position
                val position = viewPager.currentItem

                // remove content
                ContentController.directoryArrayList[directoryPosition].contentArrayList.removeAt(position)

                contentsPagerAdapter.notifyItemRemoved(position)
            }
        }

        // initialize view pager
        viewPager = findViewById(R.id.pager_image)
        contentsPagerAdapter = ImageFragmentStateAdapter(supportFragmentManager, lifecycle)
        viewPager.offscreenPageLimit = 5
        viewPager.adapter = contentsPagerAdapter
        viewPager.setCurrentItem(contentPosition, false)

        // set action bar title
        title = ContentController.directoryArrayList[directoryPosition].contentArrayList[contentPosition].name
    }

    override fun onResume() {
        // set last resumed activity code
        MainActivity.lastResumedActivityCode = ACTIVITY_CODE_IMAGE

        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // initialize content
        val content = getCurrentContent()

        when (item.itemId) {
            R.id.share -> {
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, content.uri)
                    type = if (content.isVideo) SHARE_TYPE_VIDEO else SHARE_TYPE_IMAGE
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
                return true
            }
            R.id.delete -> {
                // initialize create delete request pending intent
                val pendingIntent = MediaStore.createDeleteRequest(contentResolver, setOf(content.uri))
                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()

                // launch intent sender request
                deleteResultLauncher.launch(intentSenderRequest)
                return true
            }
            R.id.information -> {
                RecyclerDialogFragment(DIALOG_TYPE_INFORMATION).show(supportFragmentManager, DIALOG_TYPE_INFORMATION.toString())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getCurrentContent(): Directory.Content {
        return ContentController.directoryArrayList[directoryPosition].contentArrayList[viewPager.currentItem]
    }

    fun fullImage() {
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

    inner class ImageFragmentStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun createFragment(position: Int): Fragment {
            return ImageFragment.newInstance(directoryPosition, position)
        }

        override fun getItemCount(): Int {
            return ContentController.directoryArrayList[directoryPosition].contentArrayList.size
        }

        override fun getItemId(position: Int): Long {
            return ContentController.directoryArrayList[directoryPosition].contentArrayList[position].uri.hashCode().toLong()
        }
    }
}
package com.pleiades.pleione.slotgallery.ui.activity

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_INFORMATION
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_POSITION_CONTENT
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_POSITION_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_POSITION
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_VIDEO
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController
import com.pleiades.pleione.slotgallery.info.Directory
import com.pleiades.pleione.slotgallery.ui.fragment.dialog.ProgressDialogFragment
import com.pleiades.pleione.slotgallery.ui.fragment.dialog.RecyclerDialogFragment
import com.pleiades.pleione.slotgallery.ui.fragment.main.ImageFragment


class ImageActivity : AppCompatActivity() {
    private lateinit var copyResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var renameResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var viewPager: ViewPager2
    private lateinit var contentsPagerAdapter: ImageFragmentStateAdapter
    lateinit var titleEditText: EditText

    private var directoryPosition = 0
    private var isFull = false
    private var isEditFocused = false

    private lateinit var directory: Directory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        // set decor fits system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // set title
        title = ""

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

        // initialize directory
        directory = ContentController.directoryArrayList[directoryPosition]

        // initialize activity result launcher
        copyResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // initialize directory position from extra
                val toDirectoryPosition = result.data!!.getIntExtra(INTENT_EXTRA_POSITION_DIRECTORY, -1)

                when {
                    // case same directory
                    toDirectoryPosition == directoryPosition -> {
                        // show toast
                        Toast.makeText(this, R.string.message_error_same_directory, Toast.LENGTH_SHORT).show()
                    }
                    // case default directory
                    ContentController.directoryArrayList[toDirectoryPosition].directoryPath.rootUriString == null -> {
                        // show toast
                        Toast.makeText(this, R.string.message_error_default_directory, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // show progress dialog fragment
                        val progressDialogFragment = ProgressDialogFragment(this)
                        progressDialogFragment.show(supportFragmentManager, null)

                        // copy content
                        ContentController(this).copyContents(directoryPosition, toDirectoryPosition, setOf(viewPager.currentItem), progressDialogFragment)
                    }
                }
            }
        }
        deleteResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // initialize position
                val position = viewPager.currentItem

                // remove content
                directory.contentArrayList.removeAt(position)

                // notify item removed
                contentsPagerAdapter.notifyItemRemoved(position)

                // case delete all
                if (directory.contentArrayList.size == 0) {
                    // remove directory
                    ContentController.directoryArrayList.removeAt(directoryPosition)

                    // on back pressed
                    onBackPressed()
                } else {
                    // refresh directory date
                    directory.refreshDate()

                    // sort directory array list
                    ContentController(this).sortDirectoryArrayList()

                    // initialize directory position again
                    directoryPosition = ContentController.directoryArrayList.indexOf(directory)
                }
            }
        }
        renameResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // initialize content
                val currentContent = getCurrentContent()

                // initialize to name
                val wishName = titleEditText.text.toString()
                val preWishName = wishName.substringBeforeLast(".")
                val postWishName = wishName.substringAfterLast(".")
                val isValidFormat = preWishName != postWishName
                var isDuplicate: Boolean
                var toName = wishName
                var index = 1
                do {
                    isDuplicate = false
                    for (content in directory.contentArrayList) {
                        if (toName == content.name) {
                            isDuplicate = true
                            toName =
                                if (isValidFormat)
                                    "$preWishName ($index).$postWishName"
                                else
                                    "$wishName ($index)"
                            index++
                            break
                        }
                    }
                } while (isDuplicate)

                // initialize content values
                val values = ContentValues()
                if (currentContent.isVideo) {
                    values.put(MediaStore.Video.Media.DISPLAY_NAME, toName)
                } else {
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, toName)
                }

                // update physical content
                contentResolver.update(currentContent.uri, values, null, null)

                // update content
                directory.contentArrayList[viewPager.currentItem].name = toName

                // backup
                val backupContent = directory.contentArrayList[viewPager.currentItem]

                // sort
                ContentController(this).sortContentArrayList(directoryPosition)

                // restore
                viewPager.setCurrentItem(directory.contentArrayList.indexOf(backupContent), false)

                // cancel rename
                cancelRename(toName)
            }
        }

        // initialize fragment result listener
        supportFragmentManager.setFragmentResultListener(KEY_DIRECTORY_POSITION, this) { key: String, bundle: Bundle ->
            if (key == KEY_DIRECTORY_POSITION) {
                directoryPosition = bundle.getInt(KEY_DIRECTORY_POSITION)
            }
        }

        // initialize view pager
        viewPager = findViewById(R.id.pager_image)
        contentsPagerAdapter = ImageFragmentStateAdapter(supportFragmentManager, lifecycle)
        viewPager.offscreenPageLimit = 5
        viewPager.adapter = contentsPagerAdapter
        viewPager.setCurrentItem(contentPosition, false)

        // initialize title edit text
        titleEditText = findViewById(R.id.title_appbar)
        titleEditText.setText(directory.contentArrayList[contentPosition].name)
        titleEditText.setOnFocusChangeListener { _, b ->
            isEditFocused = b
            invalidateOptionsMenu()
        }
    }

    override fun onResume() {
        // set last resumed activity code
        MainActivity.lastResumedActivityCode = ACTIVITY_CODE_IMAGE

        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isEditFocused)
            menuInflater.inflate(R.menu.menu_image_edit, menu)
        else
            menuInflater.inflate(R.menu.menu_image, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // initialize content
        val currentContent = getCurrentContent()

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.share -> {
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, currentContent.uri)
                    type = if (currentContent.isVideo) MIME_TYPE_VIDEO else MIME_TYPE_IMAGE
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
                return true
            }
            R.id.copy -> {
                val intent = Intent(this, ChoiceActivity::class.java)
                copyResultLauncher.launch(intent)
            }
            R.id.delete -> {
                // initialize create delete request pending intent
                val pendingIntent = MediaStore.createDeleteRequest(contentResolver, setOf(currentContent.uri))
                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()

                // launch intent sender request
                deleteResultLauncher.launch(intentSenderRequest)
                return true
            }
            R.id.information -> {
                RecyclerDialogFragment(DIALOG_TYPE_INFORMATION).show(supportFragmentManager, DIALOG_TYPE_INFORMATION.toString())
                return true
            }
            R.id.rename -> {
                // initialize create rename request pending intent
                val pendingIntent = MediaStore.createWriteRequest(contentResolver, setOf(currentContent.uri))
                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()

                // launch intent sender request
                renameResultLauncher.launch(intentSenderRequest)
            }
            R.id.cancel -> {
                cancelRename(currentContent.name)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        // set result
        intent.putExtra(INTENT_EXTRA_POSITION_DIRECTORY, directoryPosition)
        setResult(RESULT_OK, intent)

        super.onBackPressed()
    }

    fun getCurrentContent(): Directory.Content {
        return directory.contentArrayList[viewPager.currentItem]
    }

    fun fullImage() {
        if (isFull) {
            // show action bar
            supportActionBar!!.show()
        } else {
            // cancel rename
            cancelRename(getCurrentContent().name)

            // hide action bar
            supportActionBar!!.hide()
        }

        // set is full
        isFull = !isFull
    }

    private fun cancelRename(originName: String) {
        // rollback title text
        titleEditText.setText(originName)

        // hide keyboard
        val manager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)

        // clear title focus
        titleEditText.clearFocus()
    }

    inner class ImageFragmentStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun createFragment(position: Int): Fragment {
            return ImageFragment.newInstance(directoryPosition, position)
        }

        override fun getItemCount(): Int {
            return directory.contentArrayList.size
        }

        override fun getItemId(position: Int): Long {
            return directory.contentArrayList[position].uri.hashCode().toLong()
        }
    }
}
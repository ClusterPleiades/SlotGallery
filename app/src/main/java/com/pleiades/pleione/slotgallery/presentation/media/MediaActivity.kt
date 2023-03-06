package com.pleiades.pleione.slotgallery.presentation.media

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
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
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_INFORMATION
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_POSITION_CONTENT
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_POSITION_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_POSITION
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_VIDEO
import com.pleiades.pleione.slotgallery.Config.Companion.PACKAGE_NAME_EDIT
import com.pleiades.pleione.slotgallery.Config.Companion.STORE_URL_EDIT
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController
import com.pleiades.pleione.slotgallery.databinding.ActivityImageBinding
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.presentation.choice.ChoiceActivity
import com.pleiades.pleione.slotgallery.presentation.dialog.ProgressDialogFragment
import com.pleiades.pleione.slotgallery.presentation.dialog.ListDialogFragment
import com.pleiades.pleione.slotgallery.presentation.main.MainActivity
import kotlinx.coroutines.launch

class MediaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageBinding

    private lateinit var copyResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var editResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var renameResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var contentsPagerAdapter: ImageFragmentStateAdapter
    lateinit var titleEditText: EditText

    private var directoryPosition = 0
    private var isFull = false
    private var isEditFocused = false

    private lateinit var directory: Directory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set decor fits system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // set title
        title = ""

        // initialize appbar
        val toolbar: Toolbar = binding.appbarImage.toolbar
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
                        val progressDialogFragment = ProgressDialogFragment()
                        progressDialogFragment.show(supportFragmentManager, null)

                        // copy content
                        lifecycleScope.launch {
                            ContentController(applicationContext).copyContents(
                                directoryPosition,
                                toDirectoryPosition,
                                setOf(binding.pagerImage.currentItem),
                                progressDialogFragment
                            )
                        }
                    }
                }
            }
        }
        editResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // refresh snapseed
                ContentController(this).refreshSnapseed()

                // show toast
                Toast.makeText(this, R.string.message_snapseed, Toast.LENGTH_SHORT).show()
            }
        }
        deleteResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // initialize position
                val position = binding.pagerImage.currentItem

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
                directory.contentArrayList[binding.pagerImage.currentItem].name = toName

                // backup
                val backupContent = directory.contentArrayList[binding.pagerImage.currentItem]

                // sort
                ContentController(this).sortContentArrayList(directoryPosition)

                // restore
                binding.pagerImage.setCurrentItem(directory.contentArrayList.indexOf(backupContent), false)

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
        contentsPagerAdapter = ImageFragmentStateAdapter(supportFragmentManager, lifecycle)
        binding.pagerImage.offscreenPageLimit = 5
        binding.pagerImage.adapter = contentsPagerAdapter
        binding.pagerImage.setCurrentItem(contentPosition, false)
        binding.pagerImage.requestDisallowInterceptTouchEvent(true)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
            R.id.edit -> {
                if (currentContent.isVideo) {
                    // show toast
                    Toast.makeText(this, R.string.message_error_edit_video, Toast.LENGTH_SHORT).show()
                } else {
                    // case snapseed not installed
                    if (packageManager.getLaunchIntentForPackage(PACKAGE_NAME_EDIT) == null) {
                        // open play store
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(STORE_URL_EDIT)
                        }
                        startActivity(intent)
                    }
                    // case snapseed installed
                    else {
                        val editIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, currentContent.uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            setPackage(PACKAGE_NAME_EDIT)
                            type = MIME_TYPE_IMAGE
                        }
                        editResultLauncher.launch(Intent.createChooser(editIntent, getString(R.string.action_edit)))
                    }
                }
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
                ListDialogFragment(DIALOG_TYPE_INFORMATION).show(supportFragmentManager, DIALOG_TYPE_INFORMATION.toString())
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
        return directory.contentArrayList[binding.pagerImage.currentItem]
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
            return MediaFragment.newInstance(directoryPosition, position)
        }

        override fun getItemCount(): Int {
            return directory.contentArrayList.size
        }

        override fun getItemId(position: Int): Long {
            return directory.contentArrayList[position].uri.hashCode().toLong()
        }
    }

//    inner class SingleSwipeViewPager : ViewPager2 {
//        constructor(context: Context?) : super(context!!) {}
//        constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
//
//        override fun onTouchEvent(ev: MotionEvent): Boolean {
//            return if (ev.pointerCount == 1) {
//                super.onTouchEvent(ev)
//            } else {
//                true
//            }
//        }
//    }
}
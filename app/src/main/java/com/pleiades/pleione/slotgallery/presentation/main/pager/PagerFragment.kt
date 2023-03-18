package com.pleiades.pleione.slotgallery.presentation.main.pager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_INFORMATION
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_VIDEO
import com.pleiades.pleione.slotgallery.Config.Companion.PACKAGE_NAME_EDIT
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_RESULT_MEDIA
import com.pleiades.pleione.slotgallery.Config.Companion.STORE_URL_EDIT
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.FragmentPagerBinding
import com.pleiades.pleione.slotgallery.presentation.main.MainActivity
import com.pleiades.pleione.slotgallery.presentation.main.MainViewModel
import com.pleiades.pleione.slotgallery.presentation.main.dialog.list.ListDialogFragment
import com.pleiades.pleione.slotgallery.presentation.main.pager.media.MediaFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PagerFragment : Fragment() {
    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!
    private val activityViewModel: MainViewModel by activityViewModels()
    private val fragmentViewModel: PagerViewModel by viewModels()

    private lateinit var contentsPagerAdapter: ImageFragmentStateAdapter
//    private val copyResultLauncher: ActivityResultLauncher<Intent> =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                // TODO
//            }
//        }
//    private val editResultLauncher: ActivityResultLauncher<Intent> =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                activityViewModel.loadDirectoryList()
//                Toast.makeText(context, R.string.message_snapseed, Toast.LENGTH_SHORT).show()
//            }
//        }
//    private val deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
//        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                // TODO
//            }
//        }
//    private val renameResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
//        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                // TODO
//            }
//        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("InternalInsetResource")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // window
//        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        // action bar
//        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.appbar.toolbar)
//        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        binding.appbar.toolbar.layoutParams =
//            (binding.appbar.toolbar.layoutParams as ViewGroup.MarginLayoutParams).apply {
//                topMargin = resources.getDimensionPixelSize(
//                    resources.getIdentifier(
//                        "status_bar_height",
//                        "dimen",
//                        "android"
//                    )
//                )
//            }

        // directory
        fragmentViewModel.directory = activityViewModel.state.value.directoryList.first {
            it.directoryOverview == fragmentViewModel.directoryOverview
        }

        // pager
        contentsPagerAdapter = ImageFragmentStateAdapter(requireActivity().supportFragmentManager, lifecycle)
        with(binding.pager) {
            adapter = contentsPagerAdapter
            setCurrentItem(fragmentViewModel.currentPosition, false)
            requestDisallowInterceptTouchEvent(true)
        }

//        // title
//        binding.appbar.title.setOnFocusChangeListener { _, hasFocus ->
//            fragmentViewModel.isTitleFocused = hasFocus
//            requireActivity().invalidateOptionsMenu()
//        }

//        // fragment result listener
//        requireActivity().supportFragmentManager.setFragmentResultListener(
//            REQUEST_RESULT_KEY_COPY_COMPLETE,
//            viewLifecycleOwner
//        ) { _: String, _: Bundle ->
//            activityViewModel.loadDirectoryList()
//        }

//        // main state
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                activityViewModel.state.collect {
//                    // directory 찾았는데(find) null이면 back
// //                    fragmentViewModel.directory = activityViewModel.state.value.directoryList.find {
// //                        it.directoryOverview == fragmentViewModel.directoryOverview
// //                    }
// //                    listAdapter.notifyItemRangeChanged(0, listAdapter.itemCount)
//                }
//            }
//        }

        // pager state
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
// //                fragmentViewModel.state.collect { state ->
// //                    requireActivity().title =
// //                        state.selectedPositionSet.size.toString() + "/" + listAdapter.itemCount
// //                    listAdapter.notifyItemRangeChanged(0, listAdapter.itemCount)
// //                }
//            }
//        }
    }

    override fun onResume() {
        super.onResume()

        activityViewModel.loadDirectoryList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (fragmentViewModel.isTitleFocused) {
            inflater.inflate(R.menu.menu_media_rename, menu)
        } else {
            inflater.inflate(R.menu.menu_media, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (requireActivity() as MainActivity).onBackPressed()
                return true
            }
            R.id.share -> {
                val intent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, fragmentViewModel.currentMedia.uri)
                    type =
                        if (fragmentViewModel.currentMedia.isVideo) {
                            MIME_TYPE_VIDEO
                        } else {
                            MIME_TYPE_IMAGE
                        }
                }
                startActivity(Intent.createChooser(intent, getString(R.string.action_share)))
                return true
            }
            R.id.copy -> {
//                copyResultLauncher.launch(Intent(requireContext(), ChoiceActivity::class.java))
                return true
            }
            R.id.edit -> {
                if (fragmentViewModel.currentMedia.isVideo) {
                    // show toast
                    Toast.makeText(context, R.string.message_error_edit_video, Toast.LENGTH_SHORT).show()
                } else {
                    // case snapseed not installed
                    if (requireContext().packageManager.getLaunchIntentForPackage(PACKAGE_NAME_EDIT) == null) {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(STORE_URL_EDIT)
                        }
                        startActivity(intent)
                    }
                    // case snapseed installed
                    else {
                        val editIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, fragmentViewModel.currentMedia.uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            setPackage(PACKAGE_NAME_EDIT)
                            type = MIME_TYPE_IMAGE
                        }
//                        editResultLauncher.launch(
//                            Intent.createChooser(
//                                editIntent,
//                                getString(R.string.action_edit)
//                            )
//                        )
                    }
                }
                return true
            }
            R.id.delete -> {
                val pendingIntent =
                    MediaStore.createDeleteRequest(
                        requireContext().contentResolver,
                        setOf(fragmentViewModel.currentMedia.uri)
                    )
                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()

//                deleteResultLauncher.launch(intentSenderRequest)
                return true
            }
            R.id.information -> {
                ListDialogFragment(DIALOG_TYPE_INFORMATION).show(
                    requireActivity().supportFragmentManager,
                    null
                )
                return true
            }
            R.id.rename -> {
                val pendingIntent =
                    MediaStore.createWriteRequest(
                        requireContext().contentResolver,
                        setOf(fragmentViewModel.currentMedia.uri)
                    )
                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()

                // launch intent sender request
//                renameResultLauncher.launch(intentSenderRequest)
            }
            R.id.cancel -> {
                binding.appbar.title.setText(fragmentViewModel.currentMedia.name)
                binding.appbar.title.clearFocus()

                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    fun fullImage() {
//        if (isFull) {
//            // show action bar
//            supportActionBar!!.show()
//        } else {
//            // cancel rename
//            cancelRename(getCurrentContent().name)
//
//            // hide action bar
//            supportActionBar!!.hide()
//        }
//
//        // set is full
//        isFull = !isFull
//    }

    inner class ImageFragmentStateAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun createFragment(position: Int): Fragment =
            MediaFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(REQUEST_RESULT_MEDIA, fragmentViewModel.currentMedia)
                }
            }

        override fun getItemCount(): Int {
            return fragmentViewModel.directory.mediaMutableList.size
        }

        override fun getItemId(position: Int): Long {
            return fragmentViewModel.currentMedia.uri.hashCode().toLong()
        }
    }
}

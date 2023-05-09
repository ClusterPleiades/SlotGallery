package com.pleiades.pleione.slotgallery.presentation.main.pager

import android.app.Activity
import android.app.ProgressDialog.show
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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_COPY_MEDIA
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_INFORMATION
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_DIRECTORY_OVERVIEW
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_VIDEO
import com.pleiades.pleione.slotgallery.Config.Companion.PACKAGE_NAME_EDIT
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_RESULT_MEDIA
import com.pleiades.pleione.slotgallery.Config.Companion.STORE_URL_EDIT
import com.pleiades.pleione.slotgallery.Config.Companion.URI_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController
import com.pleiades.pleione.slotgallery.databinding.FragmentPagerBinding
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.presentation.choice.ChoiceActivity
import com.pleiades.pleione.slotgallery.presentation.main.MainActivity
import com.pleiades.pleione.slotgallery.presentation.main.MainViewModel
import com.pleiades.pleione.slotgallery.presentation.main.dialog.list.ListDialogFragment
import com.pleiades.pleione.slotgallery.presentation.main.dialog.progress.ProgressDialogFragment
import com.pleiades.pleione.slotgallery.presentation.main.pager.page.PageFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PagerFragment : Fragment() {
    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!
    private val activityViewModel: MainViewModel by activityViewModels()
    private val fragmentViewModel: PagerViewModel by viewModels()

    private val copyResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    val toDirectoryOverview = intent.getParcelableExtra(
                        INTENT_EXTRA_DIRECTORY_OVERVIEW,
                        DirectoryOverview::class.java
                    )
                    val toDirectory = activityViewModel.state.value.directoryList.find {
                        it.directoryOverview == toDirectoryOverview
                    } ?: return@registerForActivityResult

                    if (toDirectory.directoryOverview.uri == URI_DEFAULT_DIRECTORY) {
                        Toast
                            .makeText(context, R.string.message_error_default_directory, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        ProgressDialogFragment(DIALOG_TYPE_COPY_MEDIA).show(
                            requireActivity().supportFragmentManager,
                            null
                        )
                        activityViewModel.copyMedia(listOf(fragmentViewModel.currentMedia), toDirectory)
                    }
                }
            }
        }
    private val editResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(requireContext(), R.string.message_snapseed, Toast.LENGTH_SHORT).show()
            }
        }
    private val deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                fragmentViewModel.directory.mediaMutableList.removeAt(fragmentViewModel.currentPosition)
                contentsPagerAdapter.notifyItemRemoved(fragmentViewModel.currentPosition)

                // current position
                fragmentViewModel.currentPosition = binding.pager.currentItem

                // title
                requireActivity().title = fragmentViewModel.currentMedia.name
            }
        }

    private lateinit var contentsPagerAdapter: ImageFragmentStateAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // options menu
        setHasOptionsMenu(true)

        // directory
        fragmentViewModel.directory = activityViewModel.state.value.directoryList.first {
            it.directoryOverview == fragmentViewModel.directoryOverview
        }

        // pager
        contentsPagerAdapter = ImageFragmentStateAdapter(requireActivity().supportFragmentManager, lifecycle)
        with(binding.pager) {
            adapter = contentsPagerAdapter
            setCurrentItem(fragmentViewModel.initialPosition, false)
            requestDisallowInterceptTouchEvent(true)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    // current position
                    fragmentViewModel.currentPosition = position

                    // title
                    requireActivity().title = fragmentViewModel.currentMedia.name

                    super.onPageSelected(position)
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_page, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (requireActivity() as MainActivity).onBackPressed()
                return true
            }

            R.id.share -> {
                share()
                return true
            }

            R.id.copy -> {
                copyResultLauncher.launch(Intent(requireContext(), ChoiceActivity::class.java))
                return true
            }

            R.id.edit -> {
                edit()
                return true
            }

            R.id.delete -> {
                delete()
                return true
            }

            R.id.information -> {
                ListDialogFragment(DIALOG_TYPE_INFORMATION).apply {
                    arguments = Bundle().apply {
                        putParcelable(
                            REQUEST_RESULT_MEDIA,
                            fragmentViewModel.currentMedia
                        )
                    }
                }.show(
                    requireActivity().supportFragmentManager,
                    DIALOG_TYPE_INFORMATION.toString()
                )
                return true
            }
//            R.id.rename -> {
//                val pendingIntent =
//                    MediaStore.createWriteRequest(
//                        requireContext().contentResolver,
//                        setOf(fragmentViewModel.currentMedia.uri)
//                    )
//                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
//
//                // launch intent sender request
////                renameResultLauncher.launch(intentSenderRequest)
//            }
//            R.id.cancel -> {
////                binding.appbar.title.setText(fragmentViewModel.currentMedia.name)
////                binding.appbar.title.clearFocus()
//
//                val inputMethodManager =
//                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
//
//                return true
//            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun share() {
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
    }

    private fun edit() {
        if (fragmentViewModel.currentMedia.isVideo) {
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
                editResultLauncher.launch(
                    Intent.createChooser(
                        editIntent,
                        getString(R.string.action_edit)
                    )
                )
            }
        }
    }

    private fun delete() {
        val pendingIntent =
            MediaStore.createDeleteRequest(
                requireContext().contentResolver,
                setOf(fragmentViewModel.currentMedia.uri)
            )
        val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()

        deleteResultLauncher.launch(intentSenderRequest)
    }

    inner class ImageFragmentStateAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle,
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun createFragment(position: Int): Fragment =
            PageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(
                        REQUEST_RESULT_MEDIA,
                        fragmentViewModel.directory.mediaMutableList[position]
                    )
                }
            }

        override fun getItemCount(): Int {
            return fragmentViewModel.directory.mediaMutableList.size
        }

        override fun getItemId(position: Int): Long {
            return fragmentViewModel.directory.mediaMutableList[position].uri.hashCode().toLong()
        }
    }
}

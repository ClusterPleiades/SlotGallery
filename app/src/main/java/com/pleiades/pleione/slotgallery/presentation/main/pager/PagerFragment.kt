package com.pleiades.pleione.slotgallery.presentation.main.pager

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_INFORMATION
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.PACKAGE_NAME_EDIT
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_RESULT_MEDIA
import com.pleiades.pleione.slotgallery.Config.Companion.STORE_URL_EDIT
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.FragmentPagerBinding
import com.pleiades.pleione.slotgallery.presentation.main.MainActivity
import com.pleiades.pleione.slotgallery.presentation.main.MainViewModel
import com.pleiades.pleione.slotgallery.presentation.main.dialog.list.ListDialogFragment
import com.pleiades.pleione.slotgallery.presentation.main.pager.page.PageFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PagerFragment : Fragment() {
    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!
    private val activityViewModel: MainViewModel by activityViewModels()
    private val fragmentViewModel: PagerViewModel by viewModels()

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
        }
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

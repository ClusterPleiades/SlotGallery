package com.pleiades.pleione.slotgallery.presentation.main.directory.inside

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_COPY_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_SORT_DIRECTORY_INSIDE
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_ALL
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_VIDEO
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_RESULT_KEY_SORT_ORDER_DIRECTORY_INSIDE
import com.pleiades.pleione.slotgallery.Config.Companion.SPAN_COUNT_MEDIA
import com.pleiades.pleione.slotgallery.Config.Companion.URI_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.FragmentMainBinding
import com.pleiades.pleione.slotgallery.databinding.ItemThumbnailBinding
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.domain.model.Media
import com.pleiades.pleione.slotgallery.presentation.choice.ChoiceActivity
import com.pleiades.pleione.slotgallery.presentation.main.MainViewModel
import com.pleiades.pleione.slotgallery.presentation.main.dialog.list.ListDialogFragment
import com.pleiades.pleione.slotgallery.presentation.main.dialog.progress.ProgressDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class DirectoryInsideFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val activityViewModel: MainViewModel by activityViewModels()
    private val fragmentViewModel: DirectoryInsideViewModel by viewModels()

    private val listAdapter: DirectoryInsideListAdapter = DirectoryInsideListAdapter()
    private val dragSelectTouchListener =
        DragSelectTouchListener()
            .withSelectListener { start: Int, end: Int, _: Boolean ->
                fragmentViewModel.selectRange(start, end)
            }
            .withMaxScrollDistance(24)
    private val imageResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            // TODO check
            if (result.resultCode == Activity.RESULT_OK) {
//                directoryPosition = result.data!!.getIntExtra(INTENT_EXTRA_POSITION_DIRECTORY, -1)
            }
        }
    private val copyResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    val toDirectoryOverview = intent.getParcelableExtra(Config.INTENT_EXTRA_DIRECTORY_OVERVIEW, DirectoryOverview::class.java)
                    val toDirectory = activityViewModel.state.value.directoryList.find {
                        it.directoryOverview == toDirectoryOverview
                    } ?: return@registerForActivityResult
                    val fromDirectory = fragmentViewModel.directory ?: return@registerForActivityResult

                    if (toDirectory.directoryOverview.uri == URI_DEFAULT_DIRECTORY) {
                        Toast.makeText(context, R.string.message_error_default_directory, Toast.LENGTH_SHORT).show()
                    } else {
                        ProgressDialogFragment(DIALOG_TYPE_COPY_DIRECTORY).show(requireActivity().supportFragmentManager, null)
                        activityViewModel.copyDirectory(listOf(fromDirectory), toDirectory)
                    }
                }
            }
        }
    private val deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                fragmentViewModel.stopSelect()
                requireActivity().invalidateOptionsMenu()
                activityViewModel.loadDirectoryList()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
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

        // action bar
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // directory
        fragmentViewModel.directory = activityViewModel.state.value.directoryList.find {
            it.directoryOverview == fragmentViewModel.directoryOverview
        }

        // list
        with(binding.list) {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, SPAN_COUNT_MEDIA)
            adapter = listAdapter
            itemAnimator = null
            addOnItemTouchListener(dragSelectTouchListener)
        }

        // fragment result listener
        requireActivity().supportFragmentManager.setFragmentResultListener(
            REQUEST_RESULT_KEY_SORT_ORDER_DIRECTORY_INSIDE,
            viewLifecycleOwner
        ) { _: String, _: Bundle ->
            activityViewModel.loadDirectoryList()
        }
        requireActivity().supportFragmentManager.setFragmentResultListener(
            Config.REQUEST_RESULT_KEY_COPY_COMPLETE,
            viewLifecycleOwner
        ) { _: String, _: Bundle ->
            fragmentViewModel.stopSelect()
            requireActivity().invalidateOptionsMenu()
            activityViewModel.loadDirectoryList()
        }
//        requireActivity().supportFragmentManager.setFragmentResultListener(
//            KEY_DIRECTORY_POSITION,
//            viewLifecycleOwner
//        ) { key: String, bundle: Bundle ->
//            if (key == KEY_DIRECTORY_POSITION) {
//                directoryPosition = bundle.getInt(KEY_DIRECTORY_POSITION)
//            }
//        }

        // main state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.state.collect {
                    fragmentViewModel.directory = activityViewModel.state.value.directoryList.find {
                        it.directoryOverview == fragmentViewModel.directoryOverview
                    }
                    listAdapter.notifyItemRangeChanged(0, listAdapter.itemCount)
                }
            }
        }

        // directory inside state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                fragmentViewModel.state.collect { state ->
                    requireActivity().title = state.selectedPositionSet.size.toString() + "/" + listAdapter.itemCount
                    listAdapter.notifyItemRangeChanged(0, listAdapter.itemCount)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!fragmentViewModel.isSelecting) activityViewModel.loadDirectoryList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (fragmentViewModel.isSelecting) {
            inflater.inflate(R.menu.menu_content_select, menu)
        } else {
            requireActivity().title = fragmentViewModel.directoryOverview.toString()
            inflater.inflate(R.menu.menu_content, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.sort -> {
                ListDialogFragment(DIALOG_TYPE_SORT_DIRECTORY_INSIDE)
                    .show(
                        requireActivity().supportFragmentManager,
                        DIALOG_TYPE_SORT_DIRECTORY_INSIDE.toString()
                    )
                return true
            }
            R.id.select_all -> {
                fragmentViewModel.selectAll(listAdapter.itemCount)
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
            R.id.delete -> {
                delete()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onBackPressed(): Boolean {
        if (fragmentViewModel.isSelecting) {
            fragmentViewModel.stopSelect()
            requireActivity().invalidateOptionsMenu()
            return true
        }
        return false
    }

    private fun share() {
        val mediaUriArrayList = ArrayList<Uri>()
        var isContainVideo = false
        var isContainImage = false
        val directory = fragmentViewModel.directory ?: return

        directory.mediaMutableList.find { it.isVideo }?.run { isContainVideo = true }
        directory.mediaMutableList.find { !it.isVideo }?.run { isContainImage = true }
        mediaUriArrayList.addAll(directory.mediaMutableList.map { it.uri })

        val intent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, mediaUriArrayList)
            type =
                if (isContainVideo && isContainImage) {
                    MIME_TYPE_ALL
                } else if (isContainVideo) {
                    MIME_TYPE_VIDEO
                } else {
                    MIME_TYPE_IMAGE
                }
        }
        startActivity(Intent.createChooser(intent, getString(R.string.action_share)))
    }

    private fun delete() {
        val mediaUriArrayList = ArrayList<Uri>()
        val directory = fragmentViewModel.directory ?: return
        mediaUriArrayList.addAll(directory.mediaMutableList.map { it.uri })

        val pendingIntent = MediaStore.createDeleteRequest(requireContext().contentResolver, mediaUriArrayList)
        val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()

        deleteResultLauncher.launch(intentSenderRequest)
    }

    inner class DirectoryInsideListAdapter : ListAdapter<Media, DirectoryInsideListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<Media>() {
            override fun areItemsTheSame(
                oldItem: Media,
                newItem: Media
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: Media,
                newItem: Media
            ): Boolean = oldItem == newItem
        }
    ) {
        inner class ViewHolder(val binding: ItemThumbnailBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.description.visibility = GONE

                itemView.setOnClickListener {
                    if (fragmentViewModel.isSelecting) {
                        fragmentViewModel.toggleSelect(bindingAdapterPosition)
                    } else {
                        // TODO
//                        val intent = Intent(context, MediaActivity::class.java)
//                        intent.putExtra(INTENT_EXTRA_POSITION_DIRECTORY, directoryPosition)
//                        intent.putExtra(INTENT_EXTRA_POSITION_MEDIA, position)
//                        imageResultLauncher.launch(intent)
                    }
                }
                itemView.setOnLongClickListener {
                    it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    fragmentViewModel.startSelect(bindingAdapterPosition)
                    requireActivity().invalidateOptionsMenu()
                    dragSelectTouchListener.startDragSelection(bindingAdapterPosition)
                    true
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemThumbnailBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail, parent, false)))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val media = fragmentViewModel.directory?.mediaMutableList?.get(position) ?: return

            Glide.with(requireContext())
                .load(media.uri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(activityViewModel.width / SPAN_COUNT_MEDIA)
                .into(holder.binding.thumbnail)

            with(holder.binding) {
                select.isVisible = fragmentViewModel.state.value.selectedPositionSet.contains(position)

                if (media.isVideo) {
                    play.visibility = VISIBLE
                    time.visibility = VISIBLE

                    val minutes = TimeUnit.MILLISECONDS.toMinutes(media.duration)
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(media.duration) % 60
                    time.text = String.format("%02d:%02d", minutes, seconds)

                    thumbnail.setColorFilter(ContextCompat.getColor(context!!, R.color.color_transparent_black))
                } else {
                    play.visibility = GONE
                    time.visibility = GONE
                    thumbnail.clearColorFilter()
                }
            }
        }

        override fun getItemCount() = fragmentViewModel.directory?.mediaMutableList?.size ?: 0
    }
}
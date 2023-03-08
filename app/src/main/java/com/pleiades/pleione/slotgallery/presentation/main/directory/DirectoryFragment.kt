package com.pleiades.pleione.slotgallery.presentation.main.directory

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_SORT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_STACK
import com.pleiades.pleione.slotgallery.Config.Companion.SPAN_COUNT_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.FragmentMainBinding
import com.pleiades.pleione.slotgallery.databinding.ItemThumbnailBinding
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.presentation.main.MainViewModel
import com.pleiades.pleione.slotgallery.presentation.setting.SettingActivity
import com.pleiades.pleione.slotgallery.ui.choice.ChoiceActivity
import com.pleiades.pleione.slotgallery.ui.dialog.ListDialogFragment
import com.pleiades.pleione.slotgallery.ui.main.ContentFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DirectoryFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val activityViewModel: MainViewModel by activityViewModels()
    private val fragmentViewModel: DirectoryViewModel by viewModels()

    private val listAdapter: DirectoryListAdapter = DirectoryListAdapter()
    private val dragSelectTouchListener =
        DragSelectTouchListener()
            .withSelectListener { start: Int, end: Int, isSelected: Boolean ->
                fragmentViewModel.selectRange(start, end)
            }
            .withMaxScrollDistance(24)

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

        // list
        with(binding.list) {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, SPAN_COUNT_DIRECTORY)
            adapter = listAdapter
            itemAnimator = null
            addOnItemTouchListener(dragSelectTouchListener)
        }

        // main state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.state.collect { state ->
                    if (activityViewModel.isSlotListEmpty()) {
                        binding.message.setText(R.string.message_error_no_slot)
                        binding.message.isVisible = true
                        binding.list.isVisible = false
                    } else {
                        listAdapter.submitList(state.directoryList)
                        binding.message.isVisible = false
                        binding.list.isVisible = true
                    }
                }
            }
        }

        // directory state
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
            inflater.inflate(R.menu.menu_directory_select, menu)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            requireActivity().title = ""
            inflater.inflate(R.menu.menu_directory, menu)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.sort -> {
                // TODO
//                ListDialogFragment(DIALOG_TYPE_SORT_DIRECTORY).show(
//                    (context as FragmentActivity).supportFragmentManager,
//                    DIALOG_TYPE_SORT_DIRECTORY.toString()
//                )
                return true
            }
            R.id.setting -> {
                startActivity(Intent(context, SettingActivity::class.java))
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
                // TODO
//                copyResultLauncher.launch(Intent(requireContext(), ChoiceActivity::class.java))
                return true
            }
            R.id.delete -> {
                fragmentViewModel.delete()
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

    fun share() {
        // TODO
    }

    inner class DirectoryListAdapter : ListAdapter<Directory, DirectoryListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<Directory>() {
            override fun areItemsTheSame(
                oldItem: Directory,
                newItem: Directory
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: Directory,
                newItem: Directory
            ): Boolean = oldItem == newItem
        }
    ) {
        inner class ViewHolder(val binding: ItemThumbnailBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                itemView.setOnClickListener {
                    if (fragmentViewModel.isSelecting) {
                        fragmentViewModel.toggleSelect(bindingAdapterPosition)
                    } else {
                        // TODO
//                        requireActivity().supportFragmentManager
//                            .beginTransaction()
//                            .replace(R.id.fragment_container, ContentFragment(bindingAdapterPosition))
//                            .addToBackStack(KEY_STACK)
//                            .commit()
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
            val directory = activityViewModel.state.value.directoryList[position]
            val media = directory.mediaMutableList[0]

            Glide.with(requireContext())
                .load(media.uri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(activityViewModel.width / SPAN_COUNT_DIRECTORY)
                .into(holder.binding.thumbnail)

            with(holder.binding) {
                select.isVisible = fragmentViewModel.state.value.selectedPositionSet.contains(position)
                title.text = directory.name
                content.text = directory.mediaMutableList.size.toString()
            }
        }

        override fun getItemCount() = activityViewModel.state.value.directoryList.size
    }
}
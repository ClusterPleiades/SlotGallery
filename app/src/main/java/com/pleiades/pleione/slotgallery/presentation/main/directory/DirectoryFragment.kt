package com.pleiades.pleione.slotgallery.presentation.main.directory

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
import com.pleiades.pleione.slotgallery.Config.Companion.SPAN_COUNT_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.FragmentMainBinding
import com.pleiades.pleione.slotgallery.databinding.ItemThumbnailBinding
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DirectoryFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val activityViewModel: MainViewModel by activityViewModels()
    private val fragmentViewModel: DirectoryViewModel by viewModels()

    private val listAdapter: DirectoryRecyclerAdapter by lazy { DirectoryRecyclerAdapter().apply { setHasStableIds(true) } }

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
        }

        // main state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.state.collect { state ->
                    listAdapter.submitList(state.directoryList)
                }
            }
        }

        // directory state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                fragmentViewModel.state.collect { state ->
                    state.selectedPositionSet.forEach { listAdapter.notifyItemChanged(it) }
                }
            }
        }
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

    inner class DirectoryRecyclerAdapter : ListAdapter<Directory, DirectoryRecyclerAdapter.ViewHolder>(
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

                }
                itemView.setOnLongClickListener {
                    true
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                ItemThumbnailBinding
                    .bind(
                        LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.item_thumbnail, parent, false)
                    )
            )

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

        override fun getItemId(position: Int) = activityViewModel.state.value.directoryList[position].directoryOverview.hashCode().toLong()
    }
}
package com.pleiades.pleione.slotgallery.presentation.setting.manage

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.*
import com.pleiades.pleione.slotgallery.Config.Companion.COUNT_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.SETTING_POSITION_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.FragmentManageBinding
import com.pleiades.pleione.slotgallery.databinding.ItemEditBinding
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.presentation.setting.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageDirectoryFragment : Fragment() {
    private var _binding: FragmentManageBinding? = null
    private val binding get() = _binding!!
    private val activityViewModel: SettingViewModel by activityViewModels()

    private val listAdapter: ManageDirectoryListAdapter by lazy { ManageDirectoryListAdapter() }
    private val addResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            result.data?.data?.let { uri ->
                with(requireContext().contentResolver) {
                    takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                uri.lastPathSegment?.let { lastPathSegment ->
                    activityViewModel.addDirectoryOverView(DirectoryOverview(uri.toString(), lastPathSegment))
                }
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // title
        requireActivity().title = resources.getStringArray(R.array.setting)[SETTING_POSITION_DIRECTORY]

        // options menu
        setHasOptionsMenu(true)

        // list
        with(binding.list) {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }

        // setting state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.state.collect { state ->
                    listAdapter.submitList(state.slotList[state.selectedSlotPosition].directoryOverviewMutableList)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = inflater.inflate(R.menu.menu_manage, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId == R.id.add) {
            addResultLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
            true
        } else {
            super.onOptionsItemSelected(item)
        }

    inner class ManageDirectoryListAdapter : ListAdapter<DirectoryOverview, ManageDirectoryListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<DirectoryOverview>() {
            override fun areItemsTheSame(
                oldItem: DirectoryOverview,
                newItem: DirectoryOverview
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: DirectoryOverview,
                newItem: DirectoryOverview
            ): Boolean = oldItem == newItem
        }
    ) {
        inner class ViewHolder(val binding: ItemEditBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                with(binding.layout) {
                    isClickable = false
                    isFocusable = false
                }
                with(binding.edit) {
                    isClickable = false
                    isFocusable = false
                    isLongClickable = false
                }
                binding.save.visibility = GONE
                binding.remove.setOnClickListener {
                    if (bindingAdapterPosition < COUNT_DEFAULT_DIRECTORY) {
                        activityViewModel.toggleDirectoryOverViewVisibility(bindingAdapterPosition)
//                        notifyItemChanged(bindingAdapterPosition)
                    } else {
                        activityViewModel.removeDirectoryOverView(bindingAdapterPosition)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemEditBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.item_edit, parent, false)))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            activityViewModel.getSelectedSlot()?.directoryOverviewMutableList?.get(position)?.let {
                with(holder.binding) {
                    edit.setText(it.lastPath)
                    remove.setImageResource(
                        if (position < COUNT_DEFAULT_DIRECTORY) {
                            if (it.isVisible) R.drawable.icon_visible
                            else R.drawable.icon_invisible
                        } else {
                            R.drawable.icon_remove
                        }
                    )
                }
            }
        }

        override fun getItemCount() = activityViewModel.getSelectedSlot()?.directoryOverviewMutableList?.size ?: 0
    }
}
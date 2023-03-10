package com.pleiades.pleione.slotgallery.presentation.setting.manage

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pleiades.pleione.slotgallery.Config.Companion.SETTING_POSITION_SLOT
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.FragmentManageBinding
import com.pleiades.pleione.slotgallery.databinding.ItemEditBinding
import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.presentation.setting.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageSlotFragment : Fragment() {
    private var _binding: FragmentManageBinding? = null
    private val binding get() = _binding!!
    private val activityViewModel: SettingViewModel by activityViewModels()

    private val listAdapter: ManageSlotListAdapter = ManageSlotListAdapter()

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
        requireActivity().title = resources.getStringArray(R.array.setting)[SETTING_POSITION_SLOT]

        // options menu
        setHasOptionsMenu(true)

        // list
        with(binding.list) {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
            itemAnimator = null
        }

        // setting state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.state.collect { state ->
                    listAdapter.submitList(state.slotList)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = inflater.inflate(R.menu.menu_manage, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId == R.id.add) {
            activityViewModel.addSlot(getString(R.string.name_new_slot))
            true
        } else {
            super.onOptionsItemSelected(item)
        }

    inner class ManageSlotListAdapter : ListAdapter<Slot, ManageSlotListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<Slot>() {
            override fun areItemsTheSame(
                oldItem: Slot,
                newItem: Slot
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: Slot,
                newItem: Slot
            ): Boolean = oldItem == newItem
        }
    ) {
        inner class ViewHolder(val binding: ItemEditBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.layout.setOnClickListener {
                    notifyItemChanged(activityViewModel.state.value.selectedSlotPosition)
                    activityViewModel.selectSlot(bindingAdapterPosition)
                    notifyItemChanged(bindingAdapterPosition)
                    it.requestFocus()
                    binding.edit.clearFocus()
                }
                binding.edit.setOnFocusChangeListener { _: View, isFocused: Boolean ->
                    binding.save.isVisible = isFocused
                    if (!isFocused) binding.edit.setText(activityViewModel.state.value.slotList[bindingAdapterPosition].name)
                }
                binding.save.setOnClickListener {
                    it.isVisible = false
                    activityViewModel.renameSlot(bindingAdapterPosition, binding.edit.text.toString())
                    binding.edit.clearFocus()
                }
                binding.remove.setOnClickListener {
                    activityViewModel.removeSlot(bindingAdapterPosition)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemEditBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.item_edit, parent, false)))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val slot = activityViewModel.state.value.slotList[position]
            val selectedSlotPosition = activityViewModel.state.value.selectedSlotPosition

            with(holder.binding) {
                edit.setText(slot.name)
                layout.setBackgroundColor(
                    if (position == selectedSlotPosition) {
                        ContextCompat.getColor(requireContext(), R.color.color_light_gray)
                    } else {
                        Color.WHITE
                    }
                )
            }
        }

        override fun getItemCount() = activityViewModel.state.value.slotList.size
    }
}

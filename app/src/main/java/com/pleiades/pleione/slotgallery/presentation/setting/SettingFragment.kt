package com.pleiades.pleione.slotgallery.presentation.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pleiades.pleione.slotgallery.Config.Companion.SETTING_POSITION_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.SETTING_POSITION_SLOT
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.FragmentSettingBinding
import com.pleiades.pleione.slotgallery.databinding.ItemSettingBinding
import com.pleiades.pleione.slotgallery.presentation.setting.manage.ManageDirectoryFragment
import com.pleiades.pleione.slotgallery.presentation.setting.manage.ManageSlotFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val activityViewModel: SettingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // title
        requireActivity().title = getString(R.string.label_setting)

        // list
        with(binding.list) {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(requireContext())
            adapter = SettingRecyclerAdapter()
        }
    }

    inner class SettingRecyclerAdapter : RecyclerView.Adapter<SettingRecyclerAdapter.ViewHolder>() {
        inner class ViewHolder(val binding: ItemSettingBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                itemView.setOnClickListener {
                    when (bindingAdapterPosition) {
                        SETTING_POSITION_SLOT -> {
                            requireActivity()
                                .supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, ManageSlotFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                        SETTING_POSITION_DIRECTORY -> {
                            if (activityViewModel.getSelectedSlot() == null) {
                                Toast.makeText(context, R.string.message_error_no_slot, Toast.LENGTH_SHORT).show()
                            } else {
                                requireActivity()
                                    .supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, ManageDirectoryFragment())
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }
                        else -> return@setOnClickListener
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                ItemSettingBinding
                    .bind(
                        LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false)
                    )
            )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder.binding) {
                title.text = resources.getStringArray(R.array.setting)[position]
                if (position == SETTING_POSITION_DIRECTORY) {
                    activityViewModel.getSelectedSlot()?.let {
                        content.text = it.name
                    }
                }
            }
        }

        override fun getItemCount() = resources.getStringArray(R.array.setting).size
    }
}

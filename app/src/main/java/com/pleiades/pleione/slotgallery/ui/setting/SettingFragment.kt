package com.pleiades.pleione.slotgallery.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_STACK
import com.pleiades.pleione.slotgallery.Config.Companion.SETTING_POSITION_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.SETTING_POSITION_SLOT
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.SlotController
import com.pleiades.pleione.slotgallery.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    companion object {
        fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingArray: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set title
        activity?.title = getString(R.string.label_setting)

        // initialize setting array
        settingArray = resources.getStringArray(R.array.setting)

        // initialize setting recycler view
        binding.recyclerSetting.setHasFixedSize(true)
        binding.recyclerSetting.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.recyclerSetting.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSetting.adapter = SettingRecyclerAdapter()
    }

    inner class SettingRecyclerAdapter : RecyclerView.Adapter<SettingRecyclerAdapter.SettingViewHolder>() {
        inner class SettingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.title_setting)
            val contentTextView: TextView = itemView.findViewById(R.id.content_setting)

            init {
                itemView.setOnClickListener {
                    when (bindingAdapterPosition) {
                        SETTING_POSITION_SLOT -> {
                            // replace fragment
                            activity!!.supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_setting, ManageSlotFragment.newInstance())
                                .addToBackStack(KEY_STACK)
                                .commit()
                        }
                        SETTING_POSITION_DIRECTORY -> {
                            if (SlotController(requireContext()).getSelectedSlot() == null) {
                                // show toast
                                Toast.makeText(context, R.string.message_error_no_slot, Toast.LENGTH_SHORT).show()
                            } else {
                                // replace fragment
                                activity!!.supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.fragment_setting, ManageDirectoryFragment.newInstance())
                                    .addToBackStack(KEY_STACK)
                                    .commit()
                            }
                        }
                        else -> return@setOnClickListener
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
            return SettingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_setting, parent, false))
        }

        override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
            // case title
            holder.titleTextView.text = settingArray[position]

            // case content
            if (position == SETTING_POSITION_DIRECTORY) {
                val selectedSlot = SlotController(requireContext()).getSelectedSlot()
                holder.contentTextView.text = selectedSlot?.name
            }
        }

        override fun getItemCount(): Int {
            return settingArray.size
        }
    }
}
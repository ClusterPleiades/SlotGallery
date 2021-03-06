package com.pleiades.pleione.slotgallery.ui.fragment.setting

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

class SettingFragment : Fragment() {
    companion object {
        fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }

    private lateinit var rootView: View
    private lateinit var settingArray: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_setting, container, false)

        // set title
        activity?.title = getString(R.string.label_setting)

        // initialize setting array
        settingArray = resources.getStringArray(R.array.setting)

        // initialize setting recycler view
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_setting)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SettingRecyclerAdapter()

        return rootView
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
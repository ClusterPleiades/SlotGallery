package com.pleiades.pleione.slotgallery.ui.fragment.setting

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pleiades.pleione.slotgallery.Config.Companion.SETTING_POSITION_SLOT
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.slot.SlotController
import java.util.*

class SlotFragment : Fragment() {
    companion object {
        fun newInstance(): SlotFragment {
            return SlotFragment()
        }
    }

    private lateinit var rootView: View
    private lateinit var slotLinkedList: LinkedList<SlotController.Slot>
    private lateinit var slotRecyclerAdapter: SlotRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_slot, container, false)

        // set title
        activity?.title = resources.getStringArray(R.array.setting)[SETTING_POSITION_SLOT]

        // set options menu
        setHasOptionsMenu(true)

        // initialize slot linked list
        slotLinkedList = SlotController(requireContext()).getSlotLinkedList()

        // initialize slot recycler adapter
        slotRecyclerAdapter = SlotRecyclerAdapter()

        // initialize slot recycler view
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_slot)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = slotRecyclerAdapter

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // case default
        inflater.inflate(R.menu.menu_slot, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                slotLinkedList.add(SlotController.Slot(getString(R.string.name_new_slot)))
                slotRecyclerAdapter.notifyItemInserted(slotLinkedList.size - 1)
                SlotController(requireContext()).putSlotLinkedList(slotLinkedList)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class SlotRecyclerAdapter : RecyclerView.Adapter<SlotRecyclerAdapter.SlotViewHolder>() {
        inner class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleEditText: EditText = itemView.findViewById(R.id.title_slot)
            private val saveButton: ImageButton = itemView.findViewById(R.id.save_slot)
            private val removeButton: ImageButton = itemView.findViewById(R.id.remove_slot)

            init {
                titleEditText.setOnFocusChangeListener { _: View, b: Boolean ->
                    // case error
                    if (adapterPosition == RecyclerView.NO_POSITION)
                        return@setOnFocusChangeListener

                    if (b)
                        saveButton.visibility = VISIBLE
                    else {
                        saveButton.visibility = GONE
                        titleEditText.setText(slotLinkedList[adapterPosition].name)
                    }

                }
                saveButton.setOnClickListener {
                    // case error
                    if (adapterPosition == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    slotLinkedList[adapterPosition].name = titleEditText.text.toString()
                    SlotController(requireContext()).putSlotLinkedList(slotLinkedList)
                    saveButton.visibility = GONE
                    Toast.makeText(context, R.string.toast_saved, Toast.LENGTH_SHORT).show()
                }
                removeButton.setOnClickListener {
                    // case error
                    if (adapterPosition == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    slotLinkedList.removeAt(adapterPosition)
                    slotRecyclerAdapter.notifyItemRemoved(adapterPosition)
                    SlotController(requireContext()).putSlotLinkedList(slotLinkedList)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
            return SlotViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_slot, parent, false))
        }

        override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
            holder.titleEditText.setText(slotLinkedList[position].name)
        }

        override fun getItemCount(): Int {
            return slotLinkedList.size
        }
    }
}
package com.pleiades.pleione.slotgallery.ui.fragment.setting

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
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
    private lateinit var slotController: SlotController
    private lateinit var slotLinkedList: LinkedList<SlotController.Slot>
    private lateinit var slotRecyclerAdapter: SlotRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_slot, container, false)

        // set title
        activity?.title = resources.getStringArray(R.array.setting)[SETTING_POSITION_SLOT]

        // set options menu
        setHasOptionsMenu(true)

        // initialize slot controller
        slotController = SlotController(requireContext())

        // initialize slot linked list
        slotLinkedList = slotController.getSlotLinkedList()

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
                slotController.putSlotLinkedList(slotLinkedList)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class SlotRecyclerAdapter : RecyclerView.Adapter<SlotRecyclerAdapter.SlotViewHolder>() {
        inner class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleEditText: EditText = itemView.findViewById(R.id.title_slot)
            val layout: LinearLayoutCompat = itemView.findViewById(R.id.layout_slot)
            private val saveButton: ImageButton = itemView.findViewById(R.id.save_slot)
            private val removeButton: ImageButton = itemView.findViewById(R.id.remove_slot)

            init {
                titleEditText.setOnFocusChangeListener { _: View, b: Boolean ->
                    // case error
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnFocusChangeListener

                    if (b)
                        saveButton.visibility = VISIBLE
                    else {
                        saveButton.visibility = GONE
                        titleEditText.setText(slotLinkedList[position].name)
                    }

                }
                layout.setOnClickListener {
                    // case error
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    layout.requestFocus()
                    titleEditText.clearFocus()

                    val beforeSelectedSlot = slotController.getSelectedSlotPosition()
                    slotController.putSelectedSlotPosition(position)

                    slotRecyclerAdapter.notifyItemChanged(beforeSelectedSlot)
                    slotRecyclerAdapter.notifyItemChanged(position)
                }
                saveButton.setOnClickListener {
                    // case error
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    slotLinkedList[position].name = titleEditText.text.toString()
                    saveButton.visibility = GONE
                    titleEditText.clearFocus()
                    slotController.putSlotLinkedList(slotLinkedList)
                    Toast.makeText(context, R.string.toast_saved, Toast.LENGTH_SHORT).show()
                }
                removeButton.setOnClickListener {
                    // case error
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    slotLinkedList.removeAt(position)
                    slotRecyclerAdapter.notifyItemRemoved(position)

                    val selectedSlotPosition = slotController.getSelectedSlotPosition()
                    if (position < selectedSlotPosition) {
                        slotController.putSelectedSlotPosition(selectedSlotPosition - 1)
                    } else if (position == selectedSlotPosition) {
                        val beforePosition = 0.coerceAtLeast(position - 1)
                        slotController.putSelectedSlotPosition(beforePosition)
                        slotRecyclerAdapter.notifyItemChanged(beforePosition)
                    }

                    slotController.putSlotLinkedList(slotLinkedList)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
            return SlotViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_slot, parent, false))
        }

        override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
            // case title
            holder.titleEditText.setText(slotLinkedList[position].name)

            // case layout
            val backgroundColor = if (position == slotController.getSelectedSlotPosition()) ContextCompat.getColor(context!!, R.color.color_light_gray) else Color.WHITE
            holder.layout.setBackgroundColor(backgroundColor)
        }

        override fun getItemCount(): Int {
            return slotLinkedList.size
        }
    }
}
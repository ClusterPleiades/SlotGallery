package com.pleiades.pleione.slotgallery.presentation.setting

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pleiades.pleione.slotgallery.Config.Companion.SETTING_POSITION_SLOT
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.SlotController
import com.pleiades.pleione.slotgallery.databinding.FragmentManageBinding
import com.pleiades.pleione.slotgallery.domain.model.Slot
import java.util.*

class ManageSlotFragment : Fragment() {
    companion object {
        fun newInstance(): ManageSlotFragment {
            return ManageSlotFragment()
        }
    }

    private var _binding: FragmentManageBinding? = null
    private val binding get() = _binding!!

    private lateinit var slotController: SlotController
    private lateinit var slotLinkedList: LinkedList<Slot>
    private lateinit var recyclerAdapter: ManageSlotRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set title
        activity?.title = resources.getStringArray(R.array.setting)[SETTING_POSITION_SLOT]

        // set options menu
        setHasOptionsMenu(true)

        // initialize slot controller
        slotController = SlotController(requireContext())

        // initialize slot array list
        slotLinkedList = slotController.getSlotInfoLinkedList()

        // initialize slot recycler adapter
        recyclerAdapter = ManageSlotRecyclerAdapter()

        // initialize slot recycler view
        binding.recyclerManage.setHasFixedSize(true)
        binding.recyclerManage.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.recyclerManage.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerManage.adapter = recyclerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // case default
        inflater.inflate(R.menu.menu_manage, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                slotLinkedList.add(Slot(getString(R.string.name_new_slot)))
                recyclerAdapter.notifyItemInserted(slotLinkedList.size - 1)
                slotController.putSlotInfoLinkedList(slotLinkedList)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class ManageSlotRecyclerAdapter : RecyclerView.Adapter<ManageSlotRecyclerAdapter.ManageSlotViewHolder>() {
        inner class ManageSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleEditText: EditText = itemView.findViewById(R.id.title_edit)
            val layout: ConstraintLayout = itemView.findViewById(R.id.layout_edit)
            private val saveButton: ImageButton = itemView.findViewById(R.id.save_edit)
            private val removeButton: ImageButton = itemView.findViewById(R.id.remove_edit)

            init {
                // set title edit text on focus change listener
                titleEditText.setOnFocusChangeListener { _: View, b: Boolean ->
                    // case error
                    val position = bindingAdapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnFocusChangeListener

                    // case focused
                    if (b) saveButton.visibility = VISIBLE
                    // case not focused
                    else {
                        // set save button visibility
                        saveButton.visibility = INVISIBLE

                        // rollback title text
                        titleEditText.setText(slotLinkedList[position].name)
                    }

                }
                // set layout on click listener
                layout.setOnClickListener {
                    // case error
                    val position = bindingAdapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    // request focus to layout
                    layout.requestFocus()

                    // clear focus from title edit text
                    titleEditText.clearFocus()

                    // initialize selected slot position
                    val beforeSelectedSlotPosition = slotController.getSelectedSlotInfoPosition()

                    // case selected slot position changed
                    if (beforeSelectedSlotPosition != position) {
                        // put selected slot position
                        slotController.putSelectedSlotInfoPosition(position)

                        // notify item changed
                        recyclerAdapter.notifyItemChanged(beforeSelectedSlotPosition)
                        recyclerAdapter.notifyItemChanged(position)
                    }
                }
                // set save button on click listener
                saveButton.setOnClickListener {
                    // case error
                    val position = bindingAdapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    // set save button visibility
                    saveButton.visibility = INVISIBLE

                    // update slot name
                    slotLinkedList[position].name = titleEditText.text.toString()

                    // clear focus from title edit text
                    titleEditText.clearFocus()

                    // put slot array list
                    slotController.putSlotInfoLinkedList(slotLinkedList)
                }
                // set remove button on click listener
                removeButton.setOnClickListener {
                    // case error
                    val position = bindingAdapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    // remove slot
                    slotLinkedList.removeAt(position)

                    // notify item removed
                    recyclerAdapter.notifyItemRemoved(position)

                    // initialize selected slot position
                    val selectedSlotPosition = slotController.getSelectedSlotInfoPosition()

                    // case position is lower
                    if (position < selectedSlotPosition) {
                        slotController.putSelectedSlotInfoPosition(selectedSlotPosition - 1)
                    }
                    // case position is same to selected slot position
                    else if (position == selectedSlotPosition) {
                        val beforePosition = 0.coerceAtLeast(position - 1)
                        slotController.putSelectedSlotInfoPosition(beforePosition)
                        recyclerAdapter.notifyItemChanged(beforePosition)
                    }

                    // put slot array list
                    slotController.putSlotInfoLinkedList(slotLinkedList)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageSlotViewHolder {
            return ManageSlotViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_edit, parent, false))
        }

        override fun onBindViewHolder(holder: ManageSlotViewHolder, position: Int) {
            // case title
            holder.titleEditText.setText(slotLinkedList[position].name)

            // case layout
            val backgroundColor = if (position == slotController.getSelectedSlotInfoPosition()) ContextCompat.getColor(context!!, R.color.color_light_gray) else Color.WHITE
            holder.layout.setBackgroundColor(backgroundColor)
        }

        override fun getItemCount(): Int {
            return slotLinkedList.size
        }
    }
}
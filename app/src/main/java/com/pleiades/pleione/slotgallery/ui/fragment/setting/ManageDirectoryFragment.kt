package com.pleiades.pleione.slotgallery.ui.fragment.setting

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pleiades.pleione.slotgallery.Config.Companion.COUNT_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.SETTING_POSITION_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.SlotController
import com.pleiades.pleione.slotgallery.info.Slot


class ManageDirectoryFragment : Fragment() {
    companion object {
        fun newInstance(): ManageDirectoryFragment {
            return ManageDirectoryFragment()
        }
    }

    private lateinit var rootView: View
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var slotController: SlotController
    private lateinit var selectedSlot: Slot
    private lateinit var recyclerAdapter: ManageDirectoryRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_manage, container, false)

        // set title
        activity?.title = resources.getStringArray(R.array.setting)[SETTING_POSITION_DIRECTORY]

        // set options menu
        setHasOptionsMenu(true)

        // initialize result launcher
        resultLauncher = registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.data != null) {
                result.data!!.data.also { uri ->
                    // persist permission
                    val contentResolver = requireContext().contentResolver
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    contentResolver.takePersistableUriPermission(uri!!, takeFlags)

                    // case not duplicated
                    if (!selectedSlot.directoryPathLinkedList.contains(uri.lastPathSegment!!)) {
                        // add directory
                        selectedSlot.directoryPathLinkedList.add(uri.lastPathSegment!!)

                        // notify item inserted
                        recyclerAdapter.notifyItemInserted(selectedSlot.directoryPathLinkedList.size - 1)

                        // put selected slot
                        slotController.putSelectedSlotInfo(selectedSlot)
                    }
                }
            }
        }

        // initialize slot controller
        slotController = SlotController(requireContext())

        // initialize slot linked list
        selectedSlot = slotController.getSelectedSlot()!!

        // initialize slot recycler adapter
        recyclerAdapter = ManageDirectoryRecyclerAdapter()

        // initialize slot recycler view
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_manage)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = recyclerAdapter

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // case default
        inflater.inflate(R.menu.menu_manage, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                resultLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class ManageDirectoryRecyclerAdapter : RecyclerView.Adapter<ManageDirectoryRecyclerAdapter.ManageDirectoryViewHolder>() {
        inner class ManageDirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleEditText: EditText = itemView.findViewById(R.id.title_edit)
            var removeButton: ImageButton = itemView.findViewById(R.id.remove_edit)
            private val layout: ConstraintLayout = itemView.findViewById(R.id.layout_edit)
            private val saveButton: ImageButton = itemView.findViewById(R.id.save_edit)

            init {
                // set default attribute settings
                titleEditText.isClickable = false
                titleEditText.isFocusable = false
                titleEditText.isLongClickable = false
                layout.isClickable = false
                layout.isFocusable = false
                saveButton.visibility = GONE

                // set remove button on click listener
                removeButton.setOnClickListener {
                    // case error
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    if (position < COUNT_DEFAULT_DIRECTORY) {
                        selectedSlot.isVisible[position] = !selectedSlot.isVisible[position]
                        notifyItemChanged(position)
                    } else {
                        selectedSlot.directoryPathLinkedList.removeAt(position)
                        notifyItemRemoved(position)
                    }

                    // put selected slot
                    slotController.putSelectedSlotInfo(selectedSlot)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageDirectoryViewHolder {
            return ManageDirectoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_edit, parent, false))
        }

        override fun onBindViewHolder(holder: ManageDirectoryViewHolder, position: Int) {
            // case title
            holder.titleEditText.setText(selectedSlot.directoryPathLinkedList[position])

            // case remove button
            if (position < COUNT_DEFAULT_DIRECTORY) {
                if (selectedSlot.isVisible[position])
                    holder.removeButton.setImageResource(R.drawable.icon_visible)
                else
                    holder.removeButton.setImageResource(R.drawable.icon_invisible)
            } else {
                holder.removeButton.setImageResource(R.drawable.icon_remove)
            }
        }

        override fun getItemCount(): Int {
            return selectedSlot.directoryPathLinkedList.size
        }
    }
}
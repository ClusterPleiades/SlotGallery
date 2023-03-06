package com.pleiades.pleione.slotgallery.presentation.setting

import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pleiades.pleione.slotgallery.Config.Companion.COUNT_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.NAME_DUMMY
import com.pleiades.pleione.slotgallery.Config.Companion.SETTING_POSITION_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.SlotController
import com.pleiades.pleione.slotgallery.databinding.FragmentManageBinding
import com.pleiades.pleione.slotgallery.domain.model.Slot
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.InputStream


class ManageDirectoryFragment : Fragment() {
    companion object {
        fun newInstance(): ManageDirectoryFragment {
            return ManageDirectoryFragment()
        }
    }

    private var _binding: FragmentManageBinding? = null
    private val binding get() = _binding!!

    private lateinit var addResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var slotController: SlotController
    private lateinit var selectedSlot: Slot
    private lateinit var recyclerAdapter: ManageDirectoryRecyclerAdapter

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
        activity?.title = resources.getStringArray(R.array.setting)[SETTING_POSITION_DIRECTORY]

        // set options menu
        setHasOptionsMenu(true)

        // initialize activity result launcher
        addResultLauncher = registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.data != null) {
                result.data!!.data.also { uri ->
                    // persist permission
                    val contentResolver = requireContext().contentResolver
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    contentResolver.takePersistableUriPermission(uri!!, takeFlags)

                    // initialize directory path
                    val directoryPath = Slot.DirectoryPath(uri.toString(), uri.lastPathSegment!!)

                    // case not duplicated
                    if (!selectedSlot.directoryPathList.contains(directoryPath)) {
                        // initialize directory document file
                        val directoryDocumentFile = DocumentFile.fromTreeUri(requireContext(), uri)!!
                        if (directoryDocumentFile.listFiles().isEmpty()) {
                            val dummyDocumentFile = directoryDocumentFile.createFile(MIME_TYPE_IMAGE, NAME_DUMMY)!!

                            try {
                                // save content
                                val inputStream: InputStream = requireContext().resources.openRawResource(R.raw.ic_launcher_foreground)
                                val bufferedInputStream = BufferedInputStream(inputStream)
                                val outputStream = requireContext().contentResolver.openOutputStream(dummyDocumentFile.uri)!!
                                val bufferedOutputStream = BufferedOutputStream(outputStream)

                                var read: Int
                                while (bufferedInputStream.read().also { read = it } != -1) {
                                    bufferedOutputStream.write(read)
                                }

                                bufferedInputStream.close()
                                bufferedOutputStream.flush()
                                bufferedOutputStream.close()
                                inputStream.close()
                                outputStream.flush()
                                outputStream.close()

                                // scan media
                                MediaScannerConnection.scanFile(context, arrayOf(dummyDocumentFile.uri.toString()), arrayOf(MIME_TYPE_IMAGE), null)
                            } catch (e: Exception) {
                            }
                        }

                        // add directory
                        selectedSlot.directoryPathList.toMutableList().add(directoryPath)

                        // notify item inserted
                        recyclerAdapter.notifyItemInserted(selectedSlot.directoryPathList.size - 1)

                        // put selected slot
                        slotController.putSelectedSlotInfo(selectedSlot)
                    }
                }
            }
        }

        // initialize slot controller
        slotController = SlotController(requireContext())

        // initialize slot array list
        selectedSlot = slotController.getSelectedSlot()!!

        // initialize slot recycler adapter
        recyclerAdapter = ManageDirectoryRecyclerAdapter()

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
                addResultLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
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
                    val position = bindingAdapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    if (position < COUNT_DEFAULT_DIRECTORY) {
                        selectedSlot.directoryPathList.toMutableList()[position] = selectedSlot.directoryPathList[position].copy(
                            isVisible = !isVisible
                        )
                        notifyItemChanged(position)
                    } else {
                        selectedSlot.directoryPathList.toMutableList().removeAt(position)
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
            holder.titleEditText.setText(selectedSlot.directoryPathList[position].lastPath)

            // case remove button
            if (position < COUNT_DEFAULT_DIRECTORY) {
                if (selectedSlot.directoryPathList[position].isVisible)
                    holder.removeButton.setImageResource(R.drawable.icon_visible)
                else
                    holder.removeButton.setImageResource(R.drawable.icon_invisible)
            } else {
                holder.removeButton.setImageResource(R.drawable.icon_remove)
            }
        }

        override fun getItemCount(): Int {
            return selectedSlot.directoryPathList.size
        }
    }
}
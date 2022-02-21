package com.pleiades.pleione.slotgallery.ui.fragment.main

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener.OnDragSelectListener
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_SORT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.SPAN_COUNT_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController
import com.pleiades.pleione.slotgallery.controller.DeviceController
import com.pleiades.pleione.slotgallery.controller.SlotController
import com.pleiades.pleione.slotgallery.info.Directory
import com.pleiades.pleione.slotgallery.ui.activity.SettingActivity
import com.pleiades.pleione.slotgallery.ui.fragment.dialog.RecyclerDialogFragment

class DirectoryFragment : Fragment() {
    companion object {
        fun newInstance(): DirectoryFragment {
            return DirectoryFragment()
        }
    }

    private lateinit var rootView: View
    private lateinit var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var slotController: SlotController
    private lateinit var contentController: ContentController
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: DirectoryRecyclerAdapter
    private lateinit var dragSelectTouchListener: DragSelectTouchListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_main, container, false)

        // set options menu
        setHasOptionsMenu(true)

        // initialize activity result launcher
        deleteResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                // initialize selected array
                val selectedArray = recyclerAdapter.selectedHashSet.toIntArray()
                selectedArray.sortDescending()

                // remove directory
                for (position in selectedArray) {
                    ContentController.directoryArrayList.removeAt(position)
                    recyclerAdapter.notifyItemRemoved(position)
                }

                // clear selected hash set
                recyclerAdapter.selectedHashSet.clear()

                // set is selecting false
                recyclerAdapter.isSelecting = false

                // refresh action bar menu
                (context as FragmentActivity).invalidateOptionsMenu()
            }
        }

        // initialize slot controller
        slotController = SlotController(requireContext())

        // initialize content controller
        contentController = ContentController(requireContext())

        // initialize directory recycler view
        recyclerView = rootView.findViewById(R.id.recycler_thumbnail)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, SPAN_COUNT_DIRECTORY)

        // initialize recycler adapter
        recyclerAdapter = DirectoryRecyclerAdapter()
        recyclerAdapter.setHasStableIds(true)
        recyclerAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        recyclerView.adapter = recyclerAdapter

        // initialize drag selection listener
        val onDragSelectionListener = OnDragSelectListener { start: Int, end: Int, isSelected: Boolean ->
            recyclerAdapter.setRangeSelected(start, end, isSelected)
        }

        // initialize drag select touch listener
        dragSelectTouchListener = DragSelectTouchListener()
            // set drag selection listener
            .withSelectListener(onDragSelectionListener)
            // set options
            .withMaxScrollDistance(24)

        // add on item touch listener to recycler view
        recyclerView.addOnItemTouchListener(dragSelectTouchListener)

        // initialize fragment result listener
        (context as FragmentActivity).supportFragmentManager.setFragmentResultListener(KEY_DIRECTORY_SORT_ORDER, viewLifecycleOwner) { key: String, _: Bundle ->
            if (key == KEY_DIRECTORY_SORT_ORDER) {
                contentController.sortDirectoryArrayList()
                recyclerAdapter.notifyItemRangeChanged(0, ContentController.directoryArrayList.size, false)
            }
        }

        // case launch application
        if (ContentController.directoryArrayList.size == 0) {
            // refresh
            refresh()
        }

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val actionBar = (activity as AppCompatActivity).supportActionBar!!

        // case is selecting
        if (recyclerAdapter.isSelecting) {
            inflater.inflate(R.menu.menu_directory_select, menu)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        // case default
        else {
            inflater.inflate(R.menu.menu_directory, menu)
            actionBar.setDisplayHomeAsUpEnabled(false)
            requireActivity().title = ""
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.sort -> {
                RecyclerDialogFragment(DIALOG_TYPE_SORT_DIRECTORY).show((context as FragmentActivity).supportFragmentManager, DIALOG_TYPE_SORT_DIRECTORY.toString())
                return true
            }
            R.id.setting -> {
                val intent = Intent(context, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.select_all -> {
                recyclerAdapter.setSelectedAll(true)
                return true
            }
            R.id.share -> {
                recyclerAdapter.share()
                return true
            }
            R.id.delete -> {
                recyclerAdapter.delete()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun refresh() {
        // check is content changed
        val selectedSlot = slotController.getSelectedSlot()
        val messageTextView = rootView.findViewById<TextView>(R.id.message_main)

        // case no slot
        if (selectedSlot == null) {
            messageTextView.setText(R.string.message_error_no_slot)
            messageTextView.visibility = VISIBLE
        } else {
            messageTextView.visibility = GONE

            // backup directory array list
            val backupDirectoryArrayList: ArrayList<Directory> = ArrayList()
            for (directory in ContentController.directoryArrayList) backupDirectoryArrayList.add(directory)

            // initialize contents
            contentController.initializeContents()

            // case content changed
            if (ContentController.directoryArrayList != backupDirectoryArrayList) {
                // case launch application
                if (backupDirectoryArrayList.size == 0)
                    notifyDataSetChanged()
                else {
                    recyclerAdapter.selectedHashSet.clear()
                    recyclerAdapter.isSelecting = false
                    (context as FragmentActivity).invalidateOptionsMenu()
                    notifyDataSetChanged()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyDataSetChanged() {
        recyclerAdapter.notifyDataSetChanged()
    }

    fun onBackPressed(): Boolean {
        if (recyclerAdapter.isSelecting) {
            recyclerAdapter.setSelectedAll(false)
            recyclerAdapter.isSelecting = false
            (context as FragmentActivity).invalidateOptionsMenu()
            return true
        }
        return false
    }

    inner class DirectoryRecyclerAdapter : RecyclerView.Adapter<DirectoryRecyclerAdapter.DirectoryViewHolder>() {
        private val screenWidth = DeviceController.getWidthMax(requireContext())
        val selectedHashSet: HashSet<Int> = HashSet()
        var isSelecting = false

        inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val thumbnailImageView: ImageView = itemView.findViewById(R.id.image_thumbnail)
            val selectImageView: ImageView = itemView.findViewById(R.id.select_thumbnail)
            val titleTextView: TextView = itemView.findViewById(R.id.title_thumbnail)
            val contentTextView: TextView = itemView.findViewById(R.id.content_thumbnail)

            init {
                itemView.setOnClickListener {
                    // initialize position
                    val position = bindingAdapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    if (isSelecting) {
                        // toggle selected
                        toggleSelected(position)

                        // case undo
                        if (selectedHashSet.size == 0) {
                            // set is selecting
                            isSelecting = false

                            // refresh action bar menu
                            (context as FragmentActivity).invalidateOptionsMenu()
                        }
                    } else {
                        // replace fragment
                        activity!!.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_main, ContentFragment.newInstance(position))
                            .addToBackStack(Config.KEY_STACK)
                            .commit()
                    }
                }
                itemView.setOnLongClickListener { view: View ->
                    // initialize position
                    val position = bindingAdapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnLongClickListener false

                    // perform haptic feedback
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

                    // set is selecting
                    isSelecting = true

                    // set selected
                    setSelected(position, true)

                    // refresh action bar menu
                    (context as FragmentActivity).invalidateOptionsMenu()

                    // start drag
                    dragSelectTouchListener.startDragSelection(position)

                    true
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
            return DirectoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_thumbnail, parent, false))
        }

        override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {
            val directory = ContentController.directoryArrayList[position]
            val content = directory.contentArrayList[0]

            // case thumbnail
            Glide.with(context!!)
                .load(content.uri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(screenWidth / SPAN_COUNT_DIRECTORY)
                .into(holder.thumbnailImageView)

            // case select
            holder.selectImageView.visibility = if (selectedHashSet.contains(position)) VISIBLE else GONE

            // case title
            holder.titleTextView.text = directory.name

            // case content
            holder.contentTextView.text = directory.contentArrayList.size.toString()
        }

        override fun getItemCount(): Int {
            return ContentController.directoryArrayList.size
        }

        override fun getItemId(position: Int): Long {
            return ContentController.directoryArrayList[position].path.hashCode().toLong()
        }

        fun setSelected(position: Int, isSelected: Boolean) {
            if (isSelected) selectedHashSet.add(position)
            else selectedHashSet.remove(position)
            notifyItemChanged(position, false)
            activity!!.title = selectedHashSet.size.toString() + "/" + itemCount
        }

        fun setRangeSelected(startPosition: Int, endPosition: Int, isSelected: Boolean) {
            for (position in startPosition..endPosition) {
                if (isSelected) selectedHashSet.add(position)
                else selectedHashSet.remove(position)
                notifyItemChanged(position, false)
            }
            activity!!.title = selectedHashSet.size.toString() + "/" + itemCount
        }

        fun setSelectedAll(isSelected: Boolean) {
            if (isSelected) {
                for (i in 0 until itemCount) selectedHashSet.add(i)
            } else
                selectedHashSet.clear()
            notifyItemRangeChanged(0, itemCount, false)
            activity!!.title = selectedHashSet.size.toString() + "/" + itemCount
        }

        fun toggleSelected(position: Int) {
            if (selectedHashSet.contains(position)) selectedHashSet.remove(position)
            else selectedHashSet.add(position)
            notifyItemChanged(position, false)
            activity!!.title = selectedHashSet.size.toString() + "/" + itemCount
        }

        fun share() {
            // initialize selected array
            val selectedArray = selectedHashSet.toIntArray()

            // initialize content uri array list
            val contentUriLinkedList: ArrayList<Uri> = ArrayList()
            var isContainVideo = false
            var isContainImage = false
            for (position in selectedArray) {
                // initialize directory
                val directory = ContentController.directoryArrayList[position]

                // add directory contents
                for (content in directory.contentArrayList) {
                    // set is contain video, image
                    if (content.isVideo) isContainVideo = true
                    else isContainImage = true

                    // add content uri
                    contentUriLinkedList.add(content.uri)
                }
            }

            // initialize share intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUriLinkedList)
                type = if (isContainVideo && isContainImage) Config.SHARE_TYPE_ALL else if (isContainVideo) Config.SHARE_TYPE_VIDEO else Config.SHARE_TYPE_IMAGE
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
        }

        fun delete() {
            // initialize selected array
            val selectedArray = selectedHashSet.toIntArray()

            // initialize content uri array list
            val contentUriLinkedList: ArrayList<Uri> = ArrayList()
            for (position in selectedArray) {
                // initialize directory
                val directory = ContentController.directoryArrayList[position]

                // add content uris
                for (content in directory.contentArrayList) contentUriLinkedList.add(content.uri)
            }

            // initialize create delete request pending intent
            val pendingIntent = MediaStore.createDeleteRequest(requireContext().contentResolver, contentUriLinkedList)
            val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()

            // launch intent sender request
            deleteResultLauncher.launch(intentSenderRequest)
        }
    }
}
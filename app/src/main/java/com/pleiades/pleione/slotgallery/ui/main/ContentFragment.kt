package com.pleiades.pleione.slotgallery.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener.OnDragSelectListener
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_SORT_MEDIA
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_POSITION_MEDIA
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_POSITION_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_MEDIA_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_POSITION
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_ALL
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_VIDEO
import com.pleiades.pleione.slotgallery.Config.Companion.SPAN_COUNT_MEDIA
import com.pleiades.pleione.slotgallery.Config.Companion.SPAN_COUNT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.URI_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController
import com.pleiades.pleione.slotgallery.controller.DeviceController
import com.pleiades.pleione.slotgallery.controller.SlotController
import com.pleiades.pleione.slotgallery.databinding.FragmentMainBinding
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.ui.choice.ChoiceActivity
import com.pleiades.pleione.slotgallery.ui.media.MediaActivity
import com.pleiades.pleione.slotgallery.ui.dialog.ProgressDialogFragment
import com.pleiades.pleione.slotgallery.ui.dialog.ListDialogFragment
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class ContentFragment(private var directoryPosition: Int) : Fragment() {
    companion object {
        fun newInstance(directoryPosition: Int): ContentFragment {
            return ContentFragment(directoryPosition)
        }
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var copyResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private var directory: Directory = ContentController.directoryArrayList[directoryPosition]

    private lateinit var slotController: SlotController
    private lateinit var contentController: ContentController
    private lateinit var recyclerAdapter: DirectoryRecyclerAdapter
    private lateinit var dragSelectTouchListener: DragSelectTouchListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set options menu
        setHasOptionsMenu(true)

        // initialize activity result launcher
        imageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                directoryPosition = result.data!!.getIntExtra(INTENT_EXTRA_POSITION_DIRECTORY, -1)
            }
        }
        copyResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // initialize directory position from extra
                val toDirectoryPosition = result.data!!.getIntExtra(INTENT_EXTRA_POSITION_DIRECTORY, -1)

                when {
                    // case same directory
                    toDirectoryPosition == directoryPosition -> {
                        // show toast
                        Toast.makeText(context, R.string.message_error_same_directory, Toast.LENGTH_SHORT).show()
                    }
                    // case default directory
                    ContentController.directoryArrayList[toDirectoryPosition].directoryOverview.uri == URI_DEFAULT_DIRECTORY -> {
                        // show toast
                        Toast.makeText(context, R.string.message_error_default_directory, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // show progress dialog fragment
                        val progressDialogFragment = ProgressDialogFragment()
                        progressDialogFragment.show((context as FragmentActivity).supportFragmentManager, null)

                        // copy contents
                        lifecycleScope.launch {
                            contentController.copyContents(
                                directoryPosition,
                                toDirectoryPosition,
                                recyclerAdapter.selectedHashSet,
                                progressDialogFragment
                            )
                        }
                    }
                }
            }
        }
        deleteResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // case delete all
                if (recyclerAdapter.selectedHashSet.size == directory.mediaMutableList.size) {
                    // remove directory
                    ContentController.directoryArrayList.removeAt(directoryPosition)

                    // set is selecting false
                    recyclerAdapter.isSelecting = false

                    // on back pressed
                    requireActivity().onBackPressed()
                } else {
                    // initialize selected array
                    val selectedArray = recyclerAdapter.selectedHashSet.toIntArray()
                    selectedArray.sortDescending()

                    // remove content
                    for (position in selectedArray) {
                        directory.mediaMutableList.removeAt(position)
                        recyclerAdapter.notifyItemRemoved(position)
                    }

                    // sort directory array list
                    contentController.sortDirectoryArrayList()

                    // initialize directory position again
                    directoryPosition = ContentController.directoryArrayList.indexOf(directory)

                    // clear selected hash set
                    recyclerAdapter.selectedHashSet.clear()

                    // set is selecting false
                    recyclerAdapter.isSelecting = false

                    // refresh action bar menu
                    (context as FragmentActivity).invalidateOptionsMenu()
                }
            }
        }

        // initialize fragment result listener
        (context as FragmentActivity).supportFragmentManager.setFragmentResultListener(
            KEY_MEDIA_SORT_ORDER,
            viewLifecycleOwner
        ) { key: String, _: Bundle ->
            if (key == KEY_MEDIA_SORT_ORDER) {
                contentController.sortContentArrayList()
                recyclerAdapter.notifyItemRangeChanged(0, directory.mediaMutableList.size, false)
            }
        }
        (context as FragmentActivity).supportFragmentManager.setFragmentResultListener(
            KEY_DIRECTORY_POSITION,
            viewLifecycleOwner
        ) { key: String, bundle: Bundle ->
            if (key == KEY_DIRECTORY_POSITION) {
                directoryPosition = bundle.getInt(KEY_DIRECTORY_POSITION)
            }
        }

        // initialize slot controller
        slotController = SlotController(requireContext())

        // initialize content controller
        contentController = ContentController(requireContext())

        // initialize directory recycler view
        binding.list.setHasFixedSize(true)
        binding.list.layoutManager = GridLayoutManager(context, SPAN_COUNT_MEDIA)

        // initialize recycler adapter
        recyclerAdapter = DirectoryRecyclerAdapter()
        recyclerAdapter.setHasStableIds(true)
        recyclerAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.list.adapter = recyclerAdapter

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
        binding.list.addOnItemTouchListener(dragSelectTouchListener)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val actionBar = (activity as AppCompatActivity).supportActionBar!!

        // set display home as up enabled true
        actionBar.setDisplayHomeAsUpEnabled(true)

        // case is selecting
        if (recyclerAdapter.isSelecting) {
            inflater.inflate(R.menu.menu_content_select, menu)
        }
        // case default
        else {
            inflater.inflate(R.menu.menu_content, menu)
            requireActivity().title = directory.name
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                return true
            }
            R.id.sort -> {
                ListDialogFragment(DIALOG_TYPE_SORT_MEDIA).show(
                    (context as FragmentActivity).supportFragmentManager,
                    DIALOG_TYPE_SORT_MEDIA.toString()
                )
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
            R.id.copy -> {
                val intent = Intent(requireContext(), ChoiceActivity::class.java)
                copyResultLauncher.launch(intent)
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
        Handler(Looper.myLooper()!!).post {
            // backup directory
            val backupDirectory = directory

            // initialize contents
            contentController.initializeContents()

            // find directory
            var isFound = false
            for (i in ContentController.directoryArrayList.indices) {
                if (ContentController.directoryArrayList[i].name == backupDirectory.name && ContentController.directoryArrayList[i].directoryOverview == backupDirectory.directoryOverview) {
                    directoryPosition = i
                    directory = ContentController.directoryArrayList[i]
                    isFound = true
                    break
                }
            }

            // case same directory found
            if (isFound) {
                // case content changed
                if (directory.mediaMutableList != backupDirectory.mediaMutableList) {
                    recyclerAdapter.selectedHashSet.clear()
                    recyclerAdapter.isSelecting = false
                    (context as FragmentActivity).invalidateOptionsMenu()
                    notifyDataSetChanged()
                }
            } else {
                recyclerAdapter.isSelecting = false
                requireActivity().onBackPressed()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyDataSetChanged() {
        // notify data set changed
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
            val thumbnailImageView: ImageView = itemView.findViewById(R.id.thumbnail)
            val selectImageView: ImageView = itemView.findViewById(R.id.select)
            val playImageView: ImageView = itemView.findViewById(R.id.play_button)
            val timeTextView: TextView = itemView.findViewById(R.id.time)
            private val descriptionLinearLayout: LinearLayoutCompat = itemView.findViewById(R.id.description)

            init {
                // set description linear layout gone
                descriptionLinearLayout.visibility = GONE

                // set item view on click listener
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
                        val intent = Intent(context, MediaActivity::class.java)
                        intent.putExtra(INTENT_EXTRA_POSITION_DIRECTORY, directoryPosition)
                        intent.putExtra(INTENT_EXTRA_POSITION_MEDIA, position)
                        imageResultLauncher.launch(intent)
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
            return DirectoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail, parent, false))
        }

        override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {
            val content = directory.mediaMutableList[position]

            // case thumbnail
            Glide.with(context!!)
                .load(content.uri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(screenWidth / SPAN_COUNT_DIRECTORY)
                .into(holder.thumbnailImageView)

            // case select
            holder.selectImageView.visibility = if (selectedHashSet.contains(position)) VISIBLE else GONE

            // case video
            if (content.isVideo) {
                holder.playImageView.visibility = VISIBLE
                holder.timeTextView.visibility = VISIBLE

                val minutes = TimeUnit.MILLISECONDS.toMinutes(content.duration)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(content.duration) % 60
                val time = String.format("%02d:%02d", minutes, seconds)
                holder.timeTextView.text = time

                holder.thumbnailImageView.setColorFilter(ContextCompat.getColor(context!!, R.color.color_transparent_black))
            } else {
                holder.playImageView.visibility = GONE
                holder.timeTextView.visibility = GONE
                holder.thumbnailImageView.clearColorFilter()
            }

        }

        override fun getItemCount(): Int {
            return directory.mediaMutableList.size
        }

        override fun getItemId(position: Int): Long {
            return directory.mediaMutableList[position].uri.hashCode().toLong()
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
            val contentUriArrayList: ArrayList<Uri> = ArrayList()
            var isContainVideo = false
            var isContainImage = false
            for (position in selectedArray) {
                // initialize content
                val content = directory.mediaMutableList[position]

                // set is contain video, image
                if (content.isVideo) isContainVideo = true
                else isContainImage = true

                // add content uri
                contentUriArrayList.add(content.uri)
            }

            // initialize share intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUriArrayList)
                type = if (isContainVideo && isContainImage) MIME_TYPE_ALL else if (isContainVideo) MIME_TYPE_VIDEO else MIME_TYPE_IMAGE
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
        }

        fun delete() {
            // initialize selected array
            val selectedArray = selectedHashSet.toIntArray()

            // initialize content uri array list
            val contentUriArrayList: ArrayList<Uri> = ArrayList()
            for (position in selectedArray) {
                // add content uri
                contentUriArrayList.add(directory.mediaMutableList[position].uri)
            }

            // initialize create delete request pending intent
            val pendingIntent = MediaStore.createDeleteRequest(requireContext().contentResolver, contentUriArrayList)
            val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()

            // launch intent sender request
            deleteResultLauncher.launch(intentSenderRequest)
        }
    }
}
package com.pleiades.pleione.slotgallery.ui.fragment.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener.OnDragSelectListener
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_SORT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.SPAN_COUNT_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController
import com.pleiades.pleione.slotgallery.controller.DeviceController
import com.pleiades.pleione.slotgallery.controller.SlotController
import com.pleiades.pleione.slotgallery.ui.activity.SettingActivity
import com.pleiades.pleione.slotgallery.ui.fragment.dialog.RecyclerDialogFragment

class DirectoryFragment : Fragment() {
    companion object {
        fun newInstance(): DirectoryFragment {
            return DirectoryFragment()
        }
    }

    private lateinit var rootView: View

    private lateinit var slotController: SlotController
    private lateinit var contentController: ContentController
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: DirectoryRecyclerAdapter
    private lateinit var dragSelectTouchListener: DragSelectTouchListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_main, container, false)

        // set title
        activity?.title = ""

        // set options menu
        setHasOptionsMenu(true)

        // initialize slot controller
        slotController = SlotController(requireContext())

        // initialize content controller
        contentController = ContentController(requireContext())

        // initialize directory recycler view
        recyclerView = rootView.findViewById(R.id.recycler_thumbnail)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, SPAN_COUNT_DIRECTORY)
        recyclerView.itemAnimator = null

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
            .withMaxScrollDistance(24);    // default: 16; 	defines the speed of the auto scrolling
        //.withTopOffset(toolbarHeight)       // default: 0; 		set an offset for the touch region on top of the RecyclerView
        //.withBottomOffset(toolbarHeight)    // default: 0; 		set an offset for the touch region on bottom of the RecyclerView
        //.withScrollAboveTopRegion(enabled)  // default: true; 	enable auto scrolling, even if the finger is moved above the top region
        //.withScrollBelowTopRegion(enabled)  // default: true; 	enable auto scrolling, even if the finger is moved below the top region
        //.withDebug(enabled);                // default: false;

        // add on item touch listener to recycler view
        recyclerView.addOnItemTouchListener(dragSelectTouchListener)

        // initialize fragment result listener
        (context as FragmentActivity).supportFragmentManager.setFragmentResultListener(KEY_DIRECTORY_SORT_ORDER, viewLifecycleOwner) { key: String, _: Bundle ->
            if (key == KEY_DIRECTORY_SORT_ORDER) {
                contentController.sortDirectoryArrayList()
                recyclerAdapter.notifyItemRangeChanged(0, ContentController.directoryArrayList.size)
            }
        }

        return rootView
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        // check is content changed
        val selectedSlot = slotController.getSelectedSlot()
        val messageTextView = rootView.findViewById<TextView>(R.id.message_main)

        // case no slot
        if (selectedSlot == null) {
            messageTextView.setText(R.string.message_error_no_slot)
            messageTextView.visibility = VISIBLE
        } else {
            messageTextView.visibility = GONE

            // clear directory array list
            ContentController.directoryArrayList.clear()

            // initialize contents
            contentController.initializeContents()

            // refresh recycler adapter
            recyclerAdapter.notifyDataSetChanged()
        }

        super.onResume()
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
        }

        return super.onOptionsItemSelected(item)
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
        private val selectedHashSet: HashSet<Int> = HashSet()
        var isSelecting = false

        inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val thumbnailImageView: ImageView = itemView.findViewById(R.id.image_thumbnail)
            val selectImageView: ImageView = itemView.findViewById(R.id.select_thumbnail)
            val titleTextView: TextView = itemView.findViewById(R.id.title_thumbnail)
            val contentTextView: TextView = itemView.findViewById(R.id.content_thumbnail)

            init {
                itemView.setOnClickListener { v: View? ->
                    if (isSelecting) {
                        // initialize position
                        val position = bindingAdapterPosition
                        if (position == RecyclerView.NO_POSITION)
                            return@setOnClickListener

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
                        // TODO
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

            // case thumbnail
            val content = directory.contentArrayList[0]
            val contentData = contentController.getContentData(content)
            Glide.with(context!!)
                .load(contentData)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(screenWidth / SPAN_COUNT_DIRECTORY)
                .dontAnimate()
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
            notifyItemChanged(position)
            activity!!.title = selectedHashSet.size.toString() + "/" + itemCount
        }

        fun setRangeSelected(startPosition: Int, endPosition: Int, isSelected: Boolean) {
            for (position in startPosition..endPosition) {
                if (isSelected) selectedHashSet.add(position)
                else selectedHashSet.remove(position)
                notifyItemChanged(position)
            }
            activity!!.title = selectedHashSet.size.toString() + "/" + itemCount
        }

        fun setSelectedAll(isSelected: Boolean) {
            if (isSelected) {
                for (i in 0 until itemCount) selectedHashSet.add(i)
            } else
                selectedHashSet.clear()
            notifyItemRangeChanged(0, itemCount)
            activity!!.title = selectedHashSet.size.toString() + "/" + itemCount
        }

        fun toggleSelected(position: Int) {
            if (selectedHashSet.contains(position)) selectedHashSet.remove(position)
            else selectedHashSet.add(position)
            notifyItemChanged(position)
            activity!!.title = selectedHashSet.size.toString() + "/" + itemCount
        }
    }
}
package com.pleiades.pleione.slotgallery.ui.fragment

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_SCROLL_POSITION
import com.pleiades.pleione.slotgallery.Config.Companion.SPAN_COUNT_DIRECTORY
import com.pleiades.pleione.slotgallery.R

class DirectoryFragment : Fragment() {
    companion object {
        fun newInstance(): DirectoryFragment {
            return DirectoryFragment()
        }
    }

    private lateinit var rootView: View
    private lateinit var directoryRecyclerView: RecyclerView
//    private lateinit var directoryRecyclerAdapter: DirectoryRecyclerAdapter

    private var stateBundle: Bundle? = null
    private var stateParcelable: Parcelable? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_main, container, false)

        // set options menu
//        setHasOptionsMenu(true)

        // initialize
        initializeDirectoryRecyclerView()

        return rootView
    }

//    override fun onResume() {
//        // check is content changed
//        if (ContentChangeObserver.isContentChanged) {
//            // initialize content controller
//            ContentController(requireContext()).initializeContentInfoHashSet()
//
//            // refresh directory recycler view
//            refreshDirectoryRecyclerView()
//
//            // restore recycler view scroll position
//            if (stateBundle != null) {
//                stateParcelable = stateBundle!!.getParcelable(KEY_SCROLL_POSITION)
//                directoryRecyclerView.layoutManager!!.onRestoreInstanceState(stateParcelable)
//            }
//
//            // reset is content changed
//            ContentChangeObserver.isContentChanged = false
//        }
//        super.onResume()
//    }

    override fun onPause() {
        stateParcelable = directoryRecyclerView.layoutManager!!.onSaveInstanceState()
        stateBundle = Bundle()
        stateBundle!!.putParcelable(KEY_SCROLL_POSITION, stateParcelable)

        super.onPause()
    }

//    fun onBackPressed(): Boolean {
//        if (directoryRecyclerAdapter.isSelecting) {
//            directoryRecyclerAdapter.unsetAllSelected()
//            directoryRecyclerAdapter.isSelecting = false
//            (context as FragmentActivity).invalidateOptionsMenu()
//            return true
//        }
//        return false
//    }

    private fun initializeDirectoryRecyclerView() {
        // initialize directory recycler view
        directoryRecyclerView = rootView.findViewById(R.id.thumbnail_recyclerview)
        directoryRecyclerView.setHasFixedSize(true)

        val simpleItemAnimator = directoryRecyclerView.itemAnimator as SimpleItemAnimator?
        if (simpleItemAnimator != null) simpleItemAnimator.supportsChangeAnimations = false

        val gridLayoutManager = GridLayoutManager(context, SPAN_COUNT_DIRECTORY)
        directoryRecyclerView.layoutManager = gridLayoutManager

//        // initialize directory recycler adapter
//        directoryRecyclerAdapter = DirectoryRecyclerAdapter()
//        directoryRecyclerAdapter.setHasStableIds(true)
//        directoryRecyclerView.adapter = directoryRecyclerAdapter

        // initialize drag selection listener
//        val onDragSelectionListener: DragSelectTouchListener.OnDragSelectListener =
//            DragSelectTouchListener.OnDragSelectListener { start, end, isSelected -> directoryRecyclerAdapter.setPositionRangeSelected(start, end, isSelected) }
//
//        // initialize drag select touch listener
//
//        // initialize drag select touch listener
//        DirectoryFragment.dragSelectTouchListener = DragSelectTouchListener() // set drag selection listener
//            .withSelectListener(onDragSelectionListener) // set options
//            .withMaxScrollDistance(24) // default: 16; 	defines the speed of the auto scrolling
//
//        //.withTopOffset(toolbarHeight)       // default: 0; 		set an offset for the touch region on top of the RecyclerView
//        //.withBottomOffset(toolbarHeight)    // default: 0; 		set an offset for the touch region on bottom of the RecyclerView
//        //.withScrollAboveTopRegion(enabled)  // default: true; 	enable auto scrolling, even if the finger is moved above the top region
//        //.withScrollBelowTopRegion(enabled)  // default: true; 	enable auto scrolling, even if the finger is moved below the top region
//        //.withDebug(enabled);                // default: false;
//        //.withTopOffset(toolbarHeight)       // default: 0; 		set an offset for the touch region on top of the RecyclerView
//        //.withBottomOffset(toolbarHeight)    // default: 0; 		set an offset for the touch region on bottom of the RecyclerView
//        //.withScrollAboveTopRegion(enabled)  // default: true; 	enable auto scrolling, even if the finger is moved above the top region
//        //.withScrollBelowTopRegion(enabled)  // default: true; 	enable auto scrolling, even if the finger is moved below the top region
//        //.withDebug(enabled);                // default: false;
//        directoryRecyclerView.addOnItemTouchListener(DirectoryFragment.dragSelectTouchListener)
    }

//    private fun refreshDirectoryRecyclerView() {
//        directoryRecyclerAdapter.notifyItemRangeChanged(0, ContentController.directoryInfoArrayList.size())
//    }

//    private class DirectoryRecyclerAdapter : RecyclerView.Adapter<DirectoryRecyclerAdapter.DirectoryViewHolder>() {
//        private val selectedHashSet = HashSet<Int>()
//        private var isSelecting = false
//        private val width: Int = DeviceController.getWidthMax(context)
//
//        internal inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            var thumbnailImageView: ImageView
//            var playImageView: ImageView
//            var selectImageView: ImageView
//            var titleTextView: TextView
//            var contentTextView: TextView
//
//            init {
//                thumbnailImageView = itemView.findViewById(R.id.thumbnail)
//                playImageView = itemView.findViewById(R.id.play_icon)
//                selectImageView = itemView.findViewById(R.id.select_icon)
//                titleTextView = itemView.findViewById(R.id.thumbnail_title)
//                contentTextView = itemView.findViewById(R.id.thumbnail_content)
//                itemView.setOnClickListener { v: View? ->
//                    if (isSelecting) {
//                        val position = adapterPosition
//                        if (position == RecyclerView.NO_POSITION) return@setOnClickListener
//
//                        // toggle position selected
//                        togglePositionSelected(position)
//
//                        // case undo
//                        if (selectedHashSet.size == 0) {
//                            // set is selecting
//                            isSelecting = false
//
//                            // refresh action bar menu
//                            (context as FragmentActivity).invalidateOptionsMenu()
//                        }
//                    } else {
//                        // TODO
//                    }
//                }
//                itemView.setOnLongClickListener { view: View ->
//                    // initialize position
//                    val position = adapterPosition
//                    if (position == RecyclerView.NO_POSITION) return@setOnLongClickListener false
//
//                    // set haptic feedback
//                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
//
//                    // set is selecting
//                    isSelecting = true
//
//                    // set position selected
//                    setPositionSelected(position, true)
//
//                    // refresh action bar menu
//                    (context as FragmentActivity).invalidateOptionsMenu()
//
//                    // start drag
//                    DirectoryFragment.dragSelectTouchListener.startDragSelection(position)
//                    true
//                }
//            }
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
//            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_thumbnail, parent, false)
//            return DirectoryViewHolder(view)
//        }
//
//        override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {
//            val directoryInfo: DirectoryInfo = ContentController.directoryInfoArrayList.get(position)
//            val firstChildContentInfo: ContentInfo = directoryInfo.getFirstChildMediaInfo()
//            Glide.with(context)
//                .load(firstChildContentInfo.getPath())
//                .centerCrop()
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .override(width / SPAN_COUNT_DIRECTORY)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .dontAnimate()
//                .into(holder.thumbnailImageView)
//
//            // set play visibility
//            if (firstChildContentInfo.isVideo()) holder.playImageView.visibility = View.VISIBLE else holder.playImageView.visibility = View.INVISIBLE
//
//            // set select visibility
//            if (isSelecting) {
//                if (selectedHashSet.contains(position)) holder.selectImageView.visibility = View.VISIBLE else holder.selectImageView.visibility = View.INVISIBLE
//            } else holder.selectImageView.visibility = View.INVISIBLE
//
//            // set title
//            holder.titleTextView.setText(directoryInfo.getName())
//
//            // set content
//            val content = Integer.toString(directoryInfo.getChildMediaInfoArrayListSize())
//            holder.contentTextView.text = content
//        }
//
//        override fun getItemCount(): Int {
//            return ContentController.directoryInfoArrayList.size()
//        }
//
//        override fun getItemId(position: Int): Long {
//            return ContentController.directoryInfoArrayList.get(position).getPath().hashCode()
//        }
//
//        fun setPositionSelected(position: Int, isSelected: Boolean) {
//            if (isSelected) selectedHashSet.add(position) else selectedHashSet.remove(position)
//            notifyItemChanged(position)
//            activity.setTitle(selectedHashSet.size.toString() + "/" + itemCount)
//        }
//
//        fun setPositionRangeSelected(startPosition: Int, endPosition: Int, isSelected: Boolean) {
//            if (isSelected) for (position in startPosition..endPosition) {
//                selectedHashSet.add(position)
//                notifyItemChanged(position)
//            } else for (position in startPosition..endPosition) {
//                selectedHashSet.remove(position)
//                notifyItemChanged(position)
//            }
//            activity.setTitle(selectedHashSet.size.toString() + "/" + itemCount)
//        }
//
//        fun togglePositionSelected(position: Int) {
//            if (selectedHashSet.contains(position)) selectedHashSet.remove(position) else selectedHashSet.add(position)
//            notifyItemChanged(position)
//            activity.setTitle(selectedHashSet.size.toString() + "/" + itemCount)
//        }
//
//        fun setAllSelected() {
//            for (i in 0 until itemCount) selectedHashSet.add(i)
//            notifyDataSetChanged()
//            activity.setTitle(selectedHashSet.size.toString() + "/" + itemCount)
//        }
//
//        fun unsetAllSelected() {
//            selectedHashSet.clear()
//            notifyDataSetChanged()
//            activity.setTitle(selectedHashSet.size.toString() + "/" + itemCount)
//        }
//    }
}
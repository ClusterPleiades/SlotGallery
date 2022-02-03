package com.pleiades.pleione.slotgallery.ui.fragment.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        // initialize recycler adapter
        recyclerAdapter = DirectoryRecyclerAdapter()
        recyclerAdapter.setHasStableIds(true)
        recyclerAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        recyclerView.adapter = recyclerAdapter

        // initialize fragment result listener
        (context as FragmentActivity).supportFragmentManager.setFragmentResultListener(KEY_DIRECTORY_SORT_ORDER, viewLifecycleOwner) { key: String, _: Bundle ->
            if (key == KEY_DIRECTORY_SORT_ORDER) {
                contentController.sortDirectoryLinkedList()
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

        // case default
        inflater.inflate(R.menu.menu_directory, menu)
        actionBar.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == R.id.sort) {
            RecyclerDialogFragment(DIALOG_TYPE_SORT_DIRECTORY).show((context as FragmentActivity).supportFragmentManager, DIALOG_TYPE_SORT_DIRECTORY.toString())
        }
        if (id == R.id.setting) {
            val intent = Intent(context, SettingActivity::class.java)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun onBackPressed(): Boolean {
        return false
    }

    inner class DirectoryRecyclerAdapter : RecyclerView.Adapter<DirectoryRecyclerAdapter.DirectoryViewHolder>() {
        private val selectedHashSet: HashSet<Int> = HashSet()
        private val screenWidth = DeviceController.getWidthMax(requireContext())

        inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val thumbnailImageView: ImageView = itemView.findViewById(R.id.image_thumbnail)
            val selectImageView: ImageView = itemView.findViewById(R.id.select_thumbnail)
            val titleTextView: TextView = itemView.findViewById(R.id.title_thumbnail)
            val contentTextView: TextView = itemView.findViewById(R.id.content_thumbnail)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
            return DirectoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_thumbnail, parent, false))
        }

        override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {
            val directory = ContentController.directoryArrayList[position]

            // case thumbnail
            val content = directory.contentLinkedList[0]
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
            holder.contentTextView.text = directory.contentLinkedList.size.toString()
        }

        override fun getItemCount(): Int {
            return ContentController.directoryArrayList.size
        }

        override fun getItemId(position: Int): Long {
            return ContentController.directoryArrayList[position].path.hashCode().toLong()
        }
    }
}
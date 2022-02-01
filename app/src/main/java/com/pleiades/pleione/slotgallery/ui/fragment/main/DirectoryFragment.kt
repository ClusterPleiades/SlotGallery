package com.pleiades.pleione.slotgallery.ui.fragment.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pleiades.pleione.slotgallery.Config.Companion.SPAN_COUNT_DIRECTORY
import com.pleiades.pleione.slotgallery.ContentChangeObserver
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController
import com.pleiades.pleione.slotgallery.controller.DeviceController
import com.pleiades.pleione.slotgallery.controller.SlotController
import com.pleiades.pleione.slotgallery.ui.activity.SettingActivity
import java.util.*

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

        return rootView
    }

    override fun onResume() {
        // check is content changed
        if (ContentChangeObserver.isContentChanged) {
            val selectedSlot = slotController.getSelectedSlot()
            val messageTextView = rootView.findViewById<TextView>(R.id.message_main)

            // case no slot
            if (selectedSlot == null) {
                messageTextView.setText(R.string.message_error_no_slot)
                messageTextView.visibility = VISIBLE
            } else {
                messageTextView.visibility = GONE

                // clear directory linked list
                ContentController.directoryLinkedList.clear()

                // initialize contents
                contentController.initializeContents()

                // initialize recycler adapter
                recyclerAdapter = DirectoryRecyclerAdapter()
                recyclerAdapter.setHasStableIds(true)
                recyclerView.adapter = recyclerAdapter

                // set is content changed
                ContentChangeObserver.isContentChanged = false
            }
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
            val directory = ContentController.directoryLinkedList[position]

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
            return ContentController.directoryLinkedList.size
        }

    }
}
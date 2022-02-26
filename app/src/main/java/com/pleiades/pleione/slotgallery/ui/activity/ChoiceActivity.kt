package com.pleiades.pleione.slotgallery.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.Config.Companion.ACTIVITY_CODE_CHOICE
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_POSITION_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController
import com.pleiades.pleione.slotgallery.controller.DeviceController

class ChoiceActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: ChoiceRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice)

        // initialize appbar
        val appbar = findViewById<View>(R.id.appbar_choice)
        val toolbar: Toolbar = appbar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // initialize directory recycler view
        recyclerView = findViewById(R.id.recycler_choice)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, Config.SPAN_COUNT_DIRECTORY)

        // initialize recycler adapter
        recyclerAdapter = ChoiceRecyclerAdapter()
        recyclerAdapter.setHasStableIds(true)
        recyclerAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        recyclerView.adapter = recyclerAdapter

        // set title
        title = getString(R.string.choose)
    }

    override fun onResume() {
        // set last resumed activity code
        MainActivity.lastResumedActivityCode = ACTIVITY_CODE_CHOICE

        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    inner class ChoiceRecyclerAdapter : RecyclerView.Adapter<ChoiceRecyclerAdapter.ChoiceViewHolder>() {
        private val screenWidth = DeviceController.getWidthMax(this@ChoiceActivity)

        inner class ChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val thumbnailImageView: ImageView = itemView.findViewById(R.id.image_thumbnail)
            val titleTextView: TextView = itemView.findViewById(R.id.title_thumbnail)
            val contentTextView: TextView = itemView.findViewById(R.id.content_thumbnail)
            private val selectImageView: ImageView = itemView.findViewById(R.id.select_thumbnail)

            init {
                selectImageView.visibility = GONE

                itemView.setOnClickListener {
                    // initialize position
                    val position = bindingAdapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    // set result
                    intent.putExtra(INTENT_EXTRA_POSITION_DIRECTORY, position)
                    setResult(RESULT_OK, intent)

                    // finish activity
                    finish()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoiceViewHolder {
            return ChoiceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_thumbnail, parent, false))
        }

        override fun onBindViewHolder(holder: ChoiceViewHolder, position: Int) {
            val directory = ContentController.directoryArrayList[position]
            val content = directory.contentArrayList[0]

            // case thumbnail
            Glide.with(this@ChoiceActivity)
                .load(content.uri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(screenWidth / Config.SPAN_COUNT_DIRECTORY)
                .into(holder.thumbnailImageView)

            // case title
            holder.titleTextView.text = directory.name

            // case content
            holder.contentTextView.text = directory.contentArrayList.size.toString()
        }

        override fun getItemCount(): Int {
            return ContentController.directoryArrayList.size
        }

        override fun getItemId(position: Int): Long {
            return ContentController.directoryArrayList[position].directoryPath.hashCode().toLong()
        }
    }
}
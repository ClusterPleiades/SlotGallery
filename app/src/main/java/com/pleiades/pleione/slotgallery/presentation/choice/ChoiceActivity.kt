package com.pleiades.pleione.slotgallery.presentation.choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View.GONE
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_DIRECTORY_OVERVIEW
import com.pleiades.pleione.slotgallery.Config.Companion.SPAN_COUNT_DIRECTORY
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.ActivityChoiceBinding
import com.pleiades.pleione.slotgallery.databinding.ItemThumbnailBinding
import com.pleiades.pleione.slotgallery.domain.model.Directory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChoiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChoiceBinding
    private val activityViewModel: ChoiceViewModel by viewModels()

    private val listAdapter: ChoiceListAdapter = ChoiceListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // title
        title = getString(R.string.choose)

        // action bar
        setSupportActionBar(binding.appbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // list
        with(binding.list) {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, SPAN_COUNT_DIRECTORY)
            adapter = listAdapter
        }

        // choice state
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.state.collect { state ->
                    listAdapter.submitList(state.directoryList)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    inner class ChoiceListAdapter : ListAdapter<Directory, ChoiceListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<Directory>() {
            override fun areItemsTheSame(
                oldItem: Directory,
                newItem: Directory
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: Directory,
                newItem: Directory
            ): Boolean = oldItem == newItem
        }
    ) {
        inner class ViewHolder(val binding: ItemThumbnailBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.select.visibility = GONE

                itemView.setOnClickListener {
                    intent.putExtra(
                        INTENT_EXTRA_DIRECTORY_OVERVIEW,
                        activityViewModel.state.value.directoryList[bindingAdapterPosition].directoryOverview
                    )
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                ItemThumbnailBinding.bind(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail, parent, false)
                )
            )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val directory = activityViewModel.state.value.directoryList[position]
            val media = directory.mediaMutableList[0]

            Glide.with(this@ChoiceActivity)
                .load(media.uri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(activityViewModel.width / SPAN_COUNT_DIRECTORY)
                .into(holder.binding.thumbnail)

            with(holder.binding) {
                title.text = directory.name
                content.text = directory.mediaMutableList.size.toString()
            }
        }

        override fun getItemCount() = activityViewModel.state.value.directoryList.size
    }
}

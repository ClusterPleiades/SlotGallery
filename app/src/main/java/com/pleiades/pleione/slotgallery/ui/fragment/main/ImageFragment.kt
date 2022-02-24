package com.pleiades.pleione.slotgallery.ui.fragment.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.chrisbanes.photoview.PhotoView
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_NAME
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_URI
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController
import com.pleiades.pleione.slotgallery.ui.activity.ImageActivity
import com.pleiades.pleione.slotgallery.ui.activity.VideoActivity
import java.util.concurrent.TimeUnit

class ImageFragment(directoryPosition: Int, contentPosition: Int) : Fragment() {
    companion object {
        fun newInstance(directoryPosition: Int, contentPosition: Int): ImageFragment {
            return ImageFragment(directoryPosition, contentPosition)
        }
    }

    private lateinit var rootView: View
    private lateinit var photoView: PhotoView
    private lateinit var playImageView: ImageView
    private lateinit var timeTextView: TextView

    private val content = ContentController.directoryArrayList[directoryPosition].contentArrayList[contentPosition]

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_image, container, false)

        // initialize views
        photoView = rootView.findViewById(R.id.photo_image)
        photoView.setOnClickListener { (activity as ImageActivity).fullImage() }
        playImageView = rootView.findViewById(R.id.play_image)
        timeTextView = rootView.findViewById(R.id.time_image)

        // load image
        Glide.with(requireContext())
            .load(content.uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(photoView)

        // case video
        if (content.isVideo) {
            playImageView.visibility = View.VISIBLE
            timeTextView.visibility = View.VISIBLE

            val minutes = TimeUnit.MILLISECONDS.toMinutes(content.duration)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(content.duration) % 60
            val time = String.format("%02d:%02d", minutes, seconds)
            timeTextView.text = time

            photoView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_transparent_black))
            photoView.isZoomable = false

            playImageView.setOnClickListener {
                val intent = Intent(context, VideoActivity::class.java)
                intent.putExtra(INTENT_EXTRA_NAME, content.name)
                intent.putExtra(INTENT_EXTRA_URI, content.uri.toString())
                startActivity(intent)
            }
        }

        return rootView
    }

    override fun onResume() {
        // set action bar title
        (requireActivity() as ImageActivity).titleEditText.setText(content.name)
        Log.d("test", content.name)

        super.onResume()
    }
}
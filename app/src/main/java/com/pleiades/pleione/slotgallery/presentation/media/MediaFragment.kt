package com.pleiades.pleione.slotgallery.presentation.media

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_NAME
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_URI
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController
import com.pleiades.pleione.slotgallery.databinding.FragmentImageBinding
import com.pleiades.pleione.slotgallery.presentation.media.video.VideoActivity
import java.util.concurrent.TimeUnit

class MediaFragment(directoryPosition: Int, contentPosition: Int) : Fragment() {
    companion object {
        fun newInstance(directoryPosition: Int, contentPosition: Int): MediaFragment {
            return MediaFragment(directoryPosition, contentPosition)
        }
    }

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    private val content = ContentController.directoryArrayList[directoryPosition].contentArrayList[contentPosition]

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize views
        binding.photoImage.setOnClickListener { (activity as MediaActivity).fullImage() }

        // load image
        Glide.with(requireContext())
            .load(content.uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.photoImage)

        // case video
        if (content.isVideo) {
            binding.playImage.visibility = View.VISIBLE
            binding.timeImage.visibility = View.VISIBLE

            val minutes = TimeUnit.MILLISECONDS.toMinutes(content.duration)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(content.duration) % 60
            val time = String.format("%02d:%02d", minutes, seconds)
            binding.timeImage.text = time

            binding.photoImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_transparent_black))
            binding.photoImage.isZoomable = false

            binding.playImage.setOnClickListener {
                val intent = Intent(context, VideoActivity::class.java)
                intent.putExtra(INTENT_EXTRA_NAME, content.name)
                intent.putExtra(INTENT_EXTRA_URI, content.uri.toString())
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        // set action bar title
        (requireActivity() as MediaActivity).titleEditText.setText(content.name)

        super.onResume()
    }
}
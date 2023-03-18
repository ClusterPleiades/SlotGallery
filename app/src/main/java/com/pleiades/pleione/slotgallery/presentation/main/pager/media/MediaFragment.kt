package com.pleiades.pleione.slotgallery.presentation.main.pager.media

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_NAME
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_URI
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.FragmentMediaBinding
import com.pleiades.pleione.slotgallery.ui.media.video.VideoActivity
import java.util.concurrent.TimeUnit

class MediaFragment : Fragment() {
    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!
    private val fragmentViewModel: MediaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize views
//        binding.photoImage.setOnClickListener { (activity as MediaActivity).fullImage() }

        // photo
        Glide.with(requireContext())
            .load(fragmentViewModel.media.uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.photo)

        // video
        if (fragmentViewModel.media.isVideo) {
//            binding.play.visibility = View.VISIBLE
//            binding.time.visibility = View.VISIBLE
//
//            val minutes = TimeUnit.MILLISECONDS.toMinutes(fragmentViewModel.media.duration)
//            val seconds = TimeUnit.MILLISECONDS.toSeconds(fragmentViewModel.media.duration) % 60
//            val time = String.format("%02d:%02d", minutes, seconds)
//            binding.time.text = time
//
//            binding.photo.setColorFilter(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.color_transparent_black
//                )
//            )
//            binding.photo.isZoomable = false
//
//            binding.play.setOnClickListener {
//                val intent = Intent(context, VideoActivity::class.java)
//                intent.putExtra(INTENT_EXTRA_NAME, fragmentViewModel.media.name)
//                intent.putExtra(INTENT_EXTRA_URI, fragmentViewModel.media.uri.toString())
//                startActivity(intent)
//            }
        }
    }

    override fun onResume() {
        // set action bar title
//        (requireActivity() as MediaActivity).titleEditText.setText(content.name)

        super.onResume()
    }
}

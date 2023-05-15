package com.pleiades.pleione.slotgallery.presentation.main.pager.page

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
import com.pleiades.pleione.slotgallery.databinding.FragmentPageBinding
import com.pleiades.pleione.slotgallery.presentation.main.pager.page.image.ImageActivity
import com.pleiades.pleione.slotgallery.ui.media.video.VideoActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PageFragment : Fragment() {
    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!
    private val fragmentViewModel: PageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // image
        Glide.with(requireContext())
            .load(fragmentViewModel.media.uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.image)

        // case video
        if (fragmentViewModel.media.isVideo) {
            binding.play.visibility = View.VISIBLE
            binding.time.visibility = View.VISIBLE

            val minutes = TimeUnit.MILLISECONDS.toMinutes(fragmentViewModel.media.duration)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(fragmentViewModel.media.duration) % 60
            val time = String.format("%02d:%02d", minutes, seconds)
            binding.time.text = time

            binding.image.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_transparent_black
                )
            )

            binding.play.setOnClickListener {
                val intent = Intent(context, VideoActivity::class.java)
                intent.putExtra(INTENT_EXTRA_NAME, fragmentViewModel.media.name)
                intent.putExtra(INTENT_EXTRA_URI, fragmentViewModel.media.uri.toString())
                startActivity(intent)
            }
        }
        // case image
        else {
            binding.image.setOnClickListener {
                val intent = Intent(context, ImageActivity::class.java).apply {
                    putExtra(INTENT_EXTRA_URI, fragmentViewModel.media.uri)
                }

                startActivity(intent)
            }
        }
    }
}

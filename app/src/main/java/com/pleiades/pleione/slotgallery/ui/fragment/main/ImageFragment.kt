package com.pleiades.pleione.slotgallery.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.chrisbanes.photoview.PhotoView
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.ContentController

class ImageFragment(directoryPosition: Int, contentPosition: Int) : Fragment() {
    companion object {
        fun newInstance(directoryPosition: Int, contentPosition: Int): ImageFragment {
            return ImageFragment(directoryPosition, contentPosition)
        }
    }

    private lateinit var rootView: View
    private lateinit var photoView: PhotoView

    private val content = ContentController.directoryArrayList[directoryPosition].contentArrayList[contentPosition]

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_image, container, false)

        // initialize photo view
        photoView = rootView.findViewById(R.id.photo_image)
        photoView.setImageURI(content.uri)

        return rootView
    }
}
package com.pleiades.pleione.slotgallery.presentation.main.pager

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_RESULT_KEY_DIRECTORY_OVERVIEW
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_RESULT_KEY_POSITION
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.domain.model.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PagerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val directoryOverview =
        savedStateHandle.get<Parcelable>(REQUEST_RESULT_KEY_DIRECTORY_OVERVIEW) as DirectoryOverview
    val initialPosition =
        savedStateHandle.get<Int>(REQUEST_RESULT_KEY_POSITION) ?: 0
    var currentPosition = initialPosition
    val currentMedia: Media
        get() = directory.mediaMutableList[currentPosition]

    lateinit var directory: Directory
}

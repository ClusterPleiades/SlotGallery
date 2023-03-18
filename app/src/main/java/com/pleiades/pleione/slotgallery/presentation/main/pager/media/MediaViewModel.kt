package com.pleiades.pleione.slotgallery.presentation.main.pager.media

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_RESULT_MEDIA
import com.pleiades.pleione.slotgallery.domain.model.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val media = savedStateHandle.get<Parcelable>(REQUEST_RESULT_MEDIA) as Media
}

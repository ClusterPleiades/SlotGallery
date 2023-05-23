package com.pleiades.pleione.slotgallery.presentation.main.pager.page.image

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.pleiades.pleione.slotgallery.Config.Companion.INTENT_EXTRA_URI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val uri = savedStateHandle.get<Uri>(INTENT_EXTRA_URI)
}

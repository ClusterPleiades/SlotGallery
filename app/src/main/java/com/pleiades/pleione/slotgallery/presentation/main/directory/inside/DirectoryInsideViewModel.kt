package com.pleiades.pleione.slotgallery.presentation.main.directory.inside

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_RESULT_KEY_DIRECTORY_OVERVIEW
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.presentation.main.directory.DirectoryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DirectoryInsideViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : DirectoryViewModel() {
    val directoryOverview =
        savedStateHandle.get<Parcelable>(REQUEST_RESULT_KEY_DIRECTORY_OVERVIEW) as DirectoryOverview
    var directory: Directory? = null
}

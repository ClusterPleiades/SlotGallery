package com.pleiades.pleione.slotgallery.presentation.main.directory

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DirectoryViewModel : ViewModel() {
    private val _state = MutableStateFlow(DirectoryState())
    val state = _state.asStateFlow()

    var isSelecting = false
}
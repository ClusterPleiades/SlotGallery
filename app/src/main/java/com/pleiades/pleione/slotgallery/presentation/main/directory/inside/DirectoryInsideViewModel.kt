package com.pleiades.pleione.slotgallery.presentation.main.directory.inside

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_RESULT_KEY_DIRECTORY_OVERVIEW
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.presentation.main.directory.DirectoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DirectoryInsideViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(DirectoryState())
    val state = _state.asStateFlow()

    val directoryOverview = savedStateHandle.get<Parcelable>(REQUEST_RESULT_KEY_DIRECTORY_OVERVIEW) as DirectoryOverview
    var isSelecting: Boolean = false
    var directory: Directory? = null

    fun startSelect(position: Int) {
        val selectedPositionMutableSet = state.value.selectedPositionSet.toMutableSet().apply {
            add(position)
        }

        isSelecting = true
        _state.value = state.value.copy(
            selectedPositionSet = selectedPositionMutableSet
        )
    }

    fun stopSelect() {
        val selectedPositionMutableSet = mutableSetOf<Int>()

        isSelecting = false
        _state.value = state.value.copy(
            selectedPositionSet = selectedPositionMutableSet
        )
    }

    fun toggleSelect(position: Int) {
        if (state.value.selectedPositionSet.contains(position)) {
            unselect(position)
        } else {
            select(position)
        }
    }

    private fun select(position: Int) {
        val selectedPositionMutableSet = state.value.selectedPositionSet.toMutableSet().apply {
            add(position)
        }

        _state.value = state.value.copy(
            selectedPositionSet = selectedPositionMutableSet
        )
    }

    private fun unselect(position: Int) {
        val selectedPositionMutableSet = state.value.selectedPositionSet.toMutableSet().apply {
            remove(position)
        }

        _state.value = state.value.copy(
            selectedPositionSet = selectedPositionMutableSet
        )
    }

    fun selectRange(startPosition: Int, endPosition: Int) {
        val selectedPositionMutableSet = state.value.selectedPositionSet.toMutableSet().apply {
            for (position in startPosition..endPosition) add(position)
        }

        _state.value = state.value.copy(
            selectedPositionSet = selectedPositionMutableSet
        )
    }

    fun selectAll(size: Int) {
        val selectedPositionMutableSet = state.value.selectedPositionSet.toMutableSet().apply {
            for (position in 0 until size) add(position)
        }

        _state.value = state.value.copy(
            selectedPositionSet = selectedPositionMutableSet
        )
    }
}

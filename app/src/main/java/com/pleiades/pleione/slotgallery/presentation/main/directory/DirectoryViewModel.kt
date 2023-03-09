package com.pleiades.pleione.slotgallery.presentation.main.directory

import android.text.method.TextKeyListener.clear
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DirectoryViewModel : ViewModel() {
    private val _state = MutableStateFlow(DirectoryState())
    val state = _state.asStateFlow()

    var isSelecting: Boolean = false

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
        if (state.value.selectedPositionSet.contains(position)) unselect(position)
        else select(position)
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
package com.pleiades.pleione.slotgallery.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.model.Media
import com.pleiades.pleione.slotgallery.domain.usecase.media.bundle.MediaUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.usecase.slot.bundle.SlotUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.usecase.window.GetWidthUseCase
import com.pleiades.pleione.slotgallery.presentation.main.dialog.progress.ProgressDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaUseCaseBundle: MediaUseCaseBundle,
    private val slotUseCaseBundle: SlotUseCaseBundle,
    getWidthUseCase: GetWidthUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private val _progressDialogState = MutableStateFlow(ProgressDialogState())
    val progressDialogState = _progressDialogState.asStateFlow()

    val width = getWidthUseCase()
    var isFragmentAdded = false

    init {
        loadDirectoryList()
    }

    fun isSlotListEmpty() = slotUseCaseBundle.getSlotListUseCase().isEmpty()

    fun loadDirectoryList() {
        val slotList = slotUseCaseBundle.getSlotListUseCase()

        if (slotList.isNotEmpty()) {
            val selectedSlot = slotList[slotUseCaseBundle.getSelectedSlotPositionUseCase()]

            _state.value = state.value.copy(
                directoryList = mediaUseCaseBundle.getDirectoryListUseCase(selectedSlot),
                loadTime = System.currentTimeMillis()
            )
        }
    }

    fun copyDirectory(fromDirectoryList: List<Directory>, toDirectory: Directory) {
        val job = viewModelScope.launch {
            mediaUseCaseBundle.copyDirectoryUseCase(
                fromDirectoryList,
                toDirectory,
                ::setMaxProgress,
                ::progress
            )
        }

        _progressDialogState.value = progressDialogState.value.copy(
            job = job
        )
    }

    fun copyMedia(mediaList: List<Media>, toDirectory: Directory) {
        val job = viewModelScope.launch {
            mediaUseCaseBundle.copyMediaUseCase(
                mediaList,
                toDirectory,
                ::setMaxProgress,
                ::progress
            )
        }

        _progressDialogState.value = progressDialogState.value.copy(
            job = job
        )
    }

    private fun setMaxProgress(maxProgress: Int) {
        _progressDialogState.value = progressDialogState.value.copy(
            maxProgress = maxProgress
        )
    }

    private fun progress() {
        _progressDialogState.value = progressDialogState.value.copy(
            progress = progressDialogState.value.progress + 1
        )
    }

    fun cancelProgress() {
        progressDialogState.value.job?.cancel()

        _progressDialogState.value = progressDialogState.value.copy(
            isCanceled = true,
            job = null
        )
    }

    fun resetProgress() {
        _progressDialogState.value = progressDialogState.value.copy(
            isCanceled = false,
            progress = 0,
            maxProgress = 0,
            job = null
        )
    }
}

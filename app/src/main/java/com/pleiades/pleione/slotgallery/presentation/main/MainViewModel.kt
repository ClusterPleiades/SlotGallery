package com.pleiades.pleione.slotgallery.presentation.main

import androidx.lifecycle.ViewModel
import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.use_case.media.bundle.MediaUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.use_case.slot.bundle.SlotUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.use_case.window.GetWidthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaUseCaseBundle: MediaUseCaseBundle,
    private val slotUseCaseBundle: SlotUseCaseBundle,
    getWidthUseCase: GetWidthUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

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
                directoryList = mediaUseCaseBundle.getDirectoryListUseCase(selectedSlot)
            )
        }
    }
}
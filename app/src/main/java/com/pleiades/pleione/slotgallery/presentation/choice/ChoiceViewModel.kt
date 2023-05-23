package com.pleiades.pleione.slotgallery.presentation.choice

import androidx.lifecycle.ViewModel
import com.pleiades.pleione.slotgallery.domain.usecase.media.bundle.MediaUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.usecase.slot.bundle.SlotUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.usecase.window.GetWidthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChoiceViewModel @Inject constructor(
    private val mediaUseCaseBundle: MediaUseCaseBundle,
    private val slotUseCaseBundle: SlotUseCaseBundle,
    getWidthUseCase: GetWidthUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ChoiceState())
    val state = _state.asStateFlow()

    val width = getWidthUseCase()

    init {
        loadDirectoryList()
    }

    private fun loadDirectoryList() {
        val slotList = slotUseCaseBundle.getSlotListUseCase()

        if (slotList.isNotEmpty()) {
            val selectedSlot = slotList[slotUseCaseBundle.getSelectedSlotPositionUseCase()]

            _state.value = state.value.copy(
                directoryList = mediaUseCaseBundle.getDirectoryListUseCase(selectedSlot)
            )
        }
    }
}

package com.pleiades.pleione.slotgallery.presentation.setting

import androidx.lifecycle.ViewModel
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.usecase.slot.bundle.SlotUseCaseBundle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val slotUseCaseBundle: SlotUseCaseBundle
) : ViewModel() {
    private val _state = MutableStateFlow(SettingState())
    val state = _state.asStateFlow()

    init {
        _state.value = state.value.copy(
            selectedSlotPosition = slotUseCaseBundle.getSelectedSlotPositionUseCase(),
            slotList = slotUseCaseBundle.getSlotListUseCase()
        )
    }

    fun getSelectedSlot(): Slot? {
        val selectedSlotPosition = state.value.selectedSlotPosition
        val slotList = state.value.slotList

        return if (slotList.isEmpty()) {
            null
        } else {
            slotList[selectedSlotPosition]
        }
    }

    fun addDirectoryOverView(directoryOverview: DirectoryOverview) {
        val selectedSlotPosition = state.value.selectedSlotPosition
        val directoryOverviewMutableList = state.value.slotList[selectedSlotPosition].directoryOverviewMutableList.toMutableList().apply {
            add(directoryOverview)
        }
        val slotMutableList = state.value.slotList.toMutableList().apply {
            set(
                index = selectedSlotPosition,
                element = get(selectedSlotPosition).copy(
                    directoryOverviewMutableList = directoryOverviewMutableList
                )
            )
        }

        slotUseCaseBundle.putSlotListUseCase(slotMutableList)
        _state.value = state.value.copy(
            slotList = slotMutableList
        )
    }

    fun removeDirectoryOverView(position: Int) {
        val selectedSlotPosition = state.value.selectedSlotPosition
        val directoryOverviewMutableList = state.value.slotList[selectedSlotPosition].directoryOverviewMutableList.toMutableList().apply {
            removeAt(position)
        }
        val slotMutableList = state.value.slotList.toMutableList().apply {
            set(
                index = selectedSlotPosition,
                element = get(selectedSlotPosition).copy(
                    directoryOverviewMutableList = directoryOverviewMutableList
                )
            )
        }

        slotUseCaseBundle.putSlotListUseCase(slotMutableList)
        _state.value = state.value.copy(
            slotList = slotMutableList
        )
    }

    fun toggleDirectoryOverViewVisibility(position: Int) {
        val selectedSlotPosition = state.value.selectedSlotPosition
        val overview = state.value.slotList[selectedSlotPosition].directoryOverviewMutableList[position]
        val directoryOverviewMutableList = state.value.slotList[selectedSlotPosition].directoryOverviewMutableList.toMutableList().apply {
            set(
                index = position,
                element = get(position).copy(
                    isVisible = !overview.isVisible
                )
            )
        }
        val slotMutableList = state.value.slotList.toMutableList().apply {
            set(
                index = selectedSlotPosition,
                element = get(selectedSlotPosition).copy(
                    directoryOverviewMutableList = directoryOverviewMutableList
                )
            )
        }

        slotUseCaseBundle.putSlotListUseCase(slotMutableList)
        _state.value = state.value.copy(
            slotList = slotMutableList
        )
    }

    fun selectSlot(position: Int) {
        slotUseCaseBundle.putSelectedSlotPositionUseCase(position)
        _state.value = state.value.copy(
            selectedSlotPosition = position
        )
    }

    fun addSlot(name: String) {
        val slotMutableList = state.value.slotList.toMutableList().apply {
            add(Slot(name))
        }

        slotUseCaseBundle.putSlotListUseCase(slotMutableList)
        _state.value = state.value.copy(
            slotList = slotMutableList
        )
    }

    fun removeSlot(position: Int) {
        var selectedSlotPosition = state.value.selectedSlotPosition
        if ((position < selectedSlotPosition) || (position == selectedSlotPosition && position > 0)) selectedSlotPosition--

        val slotMutableList = state.value.slotList.toMutableList().apply {
            removeAt(position)
        }

        slotUseCaseBundle.putSelectedSlotPositionUseCase(selectedSlotPosition)
        slotUseCaseBundle.putSlotListUseCase(slotMutableList)
        _state.value = state.value.copy(
            selectedSlotPosition = selectedSlotPosition,
            slotList = slotMutableList
        )
    }

    fun renameSlot(position: Int, name: String) {
        val slotMutableList = state.value.slotList.toMutableList().apply {
            set(
                index = position,
                element = get(position).copy(
                    name = name
                )
            )
        }

        slotUseCaseBundle.putSlotListUseCase(slotMutableList)
        _state.value = state.value.copy(
            slotList = slotMutableList
        )
    }
}

package com.pleiades.pleione.slotgallery.presentation.setting

import androidx.lifecycle.ViewModel
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.use_case.slot.bundle.SlotUseCaseBundle
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
        val slotList = state.value.slotList
        val selectedSlotPosition = state.value.selectedSlotPosition

        return if (slotList.isEmpty()) null
        else slotList[selectedSlotPosition]
    }

    fun addDirectoryOverView(directoryOverview: DirectoryOverview) {
        getSelectedSlot()?.let {
            it.directoryOverviewMutableList.add(directoryOverview)
            _state.value = state.value.copy()
        }
    }

    fun removeDirectoryOverView(position: Int) {
        getSelectedSlot()?.let {
            it.directoryOverviewMutableList.removeAt(position)
            _state.value = state.value.copy()
        }
    }

    fun toggleDirectoryOverViewVisibility(position: Int) {
        getSelectedSlot()?.let {
            with(it.directoryOverviewMutableList[position]) { isVisible = !isVisible }
            _state.value = state.value.copy()
        }
    }

    fun selectSlot(position: Int) {
        slotUseCaseBundle.putSelectedSlotPositionUseCase(position)
        _state.value = state.value.copy(
            selectedSlotPosition = position
        )
    }

    fun addSlot(name: String) {
        val slotList = mutableListOf<Slot>().apply {
            addAll(state.value.slotList)
            add(Slot(name))
        }
        slotUseCaseBundle.putSlotListUseCase(slotList)
        _state.value = state.value.copy(
            slotList = slotList
        )
    }

    fun removeSlot(position: Int) {
        val slotList = mutableListOf<Slot>().apply {
            addAll(state.value.slotList)
        }
        slotList.removeAt(position)
        slotUseCaseBundle.putSlotListUseCase(slotList)

        var selectedSlotPosition = state.value.selectedSlotPosition
        if ((position < selectedSlotPosition) || (position == selectedSlotPosition && position > 0)) selectedSlotPosition--
        slotUseCaseBundle.putSelectedSlotPositionUseCase(selectedSlotPosition)

        _state.value = state.value.copy(
            selectedSlotPosition = selectedSlotPosition,
            slotList = slotList
        )
    }

    fun renameSlot(position: Int, name: String) {
        val slotList = mutableListOf<Slot>().apply {
            addAll(state.value.slotList)
        }.also {
            it[position].name = name
        }
        slotUseCaseBundle.putSlotListUseCase(slotList)
        _state.value = state.value.copy(
            slotList = slotList
        )
    }
}
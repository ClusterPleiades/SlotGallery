package com.pleiades.pleione.slotgallery.presentation.setting

import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide.init
import com.pleiades.pleione.slotgallery.domain.use_case.media.bundle.MediaUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.use_case.slot.bundle.SlotUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.use_case.window.GetWidthUseCase
import com.pleiades.pleione.slotgallery.presentation.main.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val slotUseCaseBundle: SlotUseCaseBundle
) : ViewModel() {
    fun getSelectedSlot() = slotUseCaseBundle.getSelectedSlotUseCase()
}
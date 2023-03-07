package com.pleiades.pleione.slotgallery.presentation.dialog

import androidx.lifecycle.ViewModel
import com.pleiades.pleione.slotgallery.domain.use_case.window.GetWidthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(
    getWidthUseCase: GetWidthUseCase
) : ViewModel() {
    val width = getWidthUseCase()
}
package com.pleiades.pleione.slotgallery.presentation.main.dialog.message

import androidx.lifecycle.ViewModel
import com.pleiades.pleione.slotgallery.domain.usecase.window.GetWidthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessageDialogViewModel @Inject constructor(
    getWidthUseCase: GetWidthUseCase
) : ViewModel() {
    val width = getWidthUseCase()
}

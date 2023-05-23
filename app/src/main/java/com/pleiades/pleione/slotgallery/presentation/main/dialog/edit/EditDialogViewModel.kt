package com.pleiades.pleione.slotgallery.presentation.main.dialog.edit

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_RESULT_MEDIA
import com.pleiades.pleione.slotgallery.domain.model.Media
import com.pleiades.pleione.slotgallery.domain.usecase.media.bundle.MediaUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.usecase.window.GetWidthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDialogViewModel @Inject constructor(
    private val mediaUseCaseBundle: MediaUseCaseBundle,
    getWidthUseCase: GetWidthUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val width = getWidthUseCase()

    val media = savedStateHandle.get<Parcelable>(REQUEST_RESULT_MEDIA) as Media?

    fun renameMedia(toName: String) {
        media?.let {
            viewModelScope.launch {
                mediaUseCaseBundle.renameMediaUseCase(
                    media = it,
                    toName = toName
                )
            }
        }
    }
}

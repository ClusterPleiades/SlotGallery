package com.pleiades.pleione.slotgallery.presentation.main.dialog.list

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.domain.model.Media
import com.pleiades.pleione.slotgallery.domain.usecase.util.bundle.UtilUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.usecase.window.GetWidthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListDialogViewModel @Inject constructor(
    private val utilUseCaseBundle: UtilUseCaseBundle,
    getWidthUseCase: GetWidthUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val width = getWidthUseCase()

    val media = savedStateHandle.get<Parcelable>(Config.REQUEST_RESULT_MEDIA) as Media?

    fun putDirectorySortOrderPosition(position: Int) =
        utilUseCaseBundle.putDirectorySortOrderPositionUseCase(position)

    fun getDirectorySortOrderPosition() = utilUseCaseBundle.getDirectorySortOrderPositionUseCase()

    fun putMediaSortOrderPosition(position: Int) =
        utilUseCaseBundle.putMediaSortOrderPositionUseCase(position)

    fun getMediaSortOrderPosition() = utilUseCaseBundle.getMediaSortOrderPositionUseCase()
}

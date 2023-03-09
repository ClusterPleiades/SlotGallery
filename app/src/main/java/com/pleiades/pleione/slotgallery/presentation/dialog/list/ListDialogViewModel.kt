package com.pleiades.pleione.slotgallery.presentation.dialog.list

import androidx.lifecycle.ViewModel
import com.pleiades.pleione.slotgallery.domain.use_case.util.bundle.UtilUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.use_case.window.GetWidthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListDialogViewModel @Inject constructor(
    private val utilUseCaseBundle: UtilUseCaseBundle,
    getWidthUseCase: GetWidthUseCase
) : ViewModel() {
    val width = getWidthUseCase()

    fun putDirectorySortOrderPosition(position: Int) = utilUseCaseBundle.putDirectorySortOrderPositionUseCase(position)

    fun getDirectorySortOrderPosition() = utilUseCaseBundle.getDirectorySortOrderPositionUseCase()

    fun putMediaSortOrderPosition(position: Int) = utilUseCaseBundle.putMediaSortOrderPositionUseCase(position)

    fun getMediaSortOrderPosition() = utilUseCaseBundle.getMediaSortOrderPositionUseCase()
}
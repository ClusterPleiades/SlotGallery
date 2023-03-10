package com.pleiades.pleione.slotgallery.domain.usecase.util

import com.pleiades.pleione.slotgallery.domain.repository.UtilRepository

class GetDirectorySortOrderPositionUseCase(private val repository: UtilRepository) {
    operator fun invoke() = repository.getDirectorySortOrderPosition()
}

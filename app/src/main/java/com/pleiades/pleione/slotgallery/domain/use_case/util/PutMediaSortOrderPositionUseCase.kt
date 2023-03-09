package com.pleiades.pleione.slotgallery.domain.use_case.util

import com.pleiades.pleione.slotgallery.domain.repository.UtilRepository

class PutMediaSortOrderPositionUseCase(private val repository: UtilRepository) {
    operator fun invoke(position: Int) = repository.putMediaSortOrderPosition(position)
}
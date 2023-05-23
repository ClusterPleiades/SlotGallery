package com.pleiades.pleione.slotgallery.domain.usecase.window

import com.pleiades.pleione.slotgallery.domain.repository.WindowRepository

class GetWidthUseCase(private val repository: WindowRepository) {
    operator fun invoke() = repository.getWidth()
}

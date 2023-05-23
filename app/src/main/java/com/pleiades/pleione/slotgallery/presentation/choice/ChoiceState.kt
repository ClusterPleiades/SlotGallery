package com.pleiades.pleione.slotgallery.presentation.choice

import com.pleiades.pleione.slotgallery.domain.model.Directory

data class ChoiceState(
    val directoryList: List<Directory> = emptyList()
)

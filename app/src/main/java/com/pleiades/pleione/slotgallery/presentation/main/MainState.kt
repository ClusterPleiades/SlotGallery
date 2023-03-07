package com.pleiades.pleione.slotgallery.presentation.main

import com.pleiades.pleione.slotgallery.domain.model.Directory

data class MainState(
    val directoryList: List<Directory> = emptyList()
)
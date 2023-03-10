package com.pleiades.pleione.slotgallery.presentation.main.dialog.progress

import kotlinx.coroutines.Job

data class ProgressDialogState(
    val isCanceled: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 0,
    val job: Job? = null
)

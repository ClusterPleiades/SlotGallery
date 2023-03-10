package com.pleiades.pleione.slotgallery.data.repository

import android.content.res.Resources
import com.pleiades.pleione.slotgallery.domain.repository.WindowRepository
import javax.inject.Inject

class DefaultWindowRepository @Inject constructor(
    private val resources: Resources
) : WindowRepository {
    override fun getWidth() = resources.displayMetrics.widthPixels
}

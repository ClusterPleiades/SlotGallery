package com.pleiades.pleione.slotgallery.controller

import android.content.Context
import android.view.WindowManager

class DeviceController {
    companion object {
        fun getWidthMax(context: Context) = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics.bounds.width()
        fun getHeightMax(context: Context) = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics.bounds.height()
    }
}
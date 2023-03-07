package com.pleiades.pleione.slotgallery.ui.dialog

import android.view.Window

fun Window.setLayoutSize(width: Number, height: Number) {
    this.setLayout(width.toInt(), height.toInt())
}
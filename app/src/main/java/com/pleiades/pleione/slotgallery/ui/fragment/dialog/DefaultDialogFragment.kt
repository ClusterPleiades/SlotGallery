package com.pleiades.pleione.slotgallery.ui.fragment.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_PERMISSION
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_WIDTH_PERCENTAGE_DEFAULT
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_IMAGES_VIDEOS
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_STORAGE
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_CODE_PERMISSION_IMAGES_VIDEOS
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_CODE_PERMISSION_STORAGE
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.DeviceController
import com.pleiades.pleione.slotgallery.databinding.FragmentDialogDefaultBinding
import com.pleiades.pleione.slotgallery.databinding.FragmentMainBinding

class DefaultDialogFragment(private val type: Int) : androidx.fragment.app.DialogFragment() {
    private var _binding: FragmentDialogDefaultBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("InflateParams", "UseRequireInsteadOfGet")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // initialize binding
        _binding = FragmentDialogDefaultBinding.inflate(requireActivity().layoutInflater)

        // initialize builder
        val builder = context?.let { AlertDialog.Builder(it) }

        // initialize activity
        val activity: FragmentActivity? = activity
        if (activity != null) {
            // initialize and set message
            val messageTextView = binding.messageDialogDefault
            when (type) {
                DIALOG_TYPE_PERMISSION -> messageTextView.setText(R.string.dialog_message_permission)
            }

            // set positive listener
            binding.positiveDialogDefault.setOnClickListener {
                when (type) {
                    DIALOG_TYPE_PERMISSION -> {
                        if (Build.VERSION.SDK_INT >= 33)
                            (context as Activity).requestPermissions(PERMISSION_IMAGES_VIDEOS, REQUEST_CODE_PERMISSION_IMAGES_VIDEOS)
                        else
                            (context as Activity).requestPermissions(PERMISSION_STORAGE, REQUEST_CODE_PERMISSION_STORAGE)
                    }
                }
                dismiss()
            }

            // set negative listener
            binding.negativeDialogDefault.setOnClickListener {
                dismiss()
            }

            // set negative visibility
            val negativeTextView = binding.negativeDialogDefault
            when (type) {
                DIALOG_TYPE_PERMISSION -> negativeTextView.visibility = View.INVISIBLE
                else -> negativeTextView.visibility = View.VISIBLE
            }

            // set dialog view
            builder!!.setView(binding.root)
        }

        // create dialog
        val dialog: AlertDialog = builder!!.create()

        // set transparent background
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // set canceled on touch outside
        when (type) {
            DIALOG_TYPE_PERMISSION -> dialog.setCanceledOnTouchOutside(false)
            else -> dialog.setCanceledOnTouchOutside(true)
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()

        val width = (DeviceController.getWidthMax(requireContext()) * DIALOG_WIDTH_PERCENTAGE_DEFAULT).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.setLayout(width, height)

        // set cancelable
        when (type) {
            DIALOG_TYPE_PERMISSION -> dialog!!.setCancelable(false)
            else -> {
                dialog!!.setCancelable(true)
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
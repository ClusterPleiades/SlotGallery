package com.pleiades.pleione.slotgallery.presentation.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_PERMISSION
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_WIDTH_PERCENTAGE_DEFAULT
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_IMAGES_VIDEOS
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_STORAGE
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_CODE_PERMISSION_IMAGES_VIDEOS
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_CODE_PERMISSION_STORAGE
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.DeviceController
import com.pleiades.pleione.slotgallery.databinding.FragmentDialogDefaultBinding

class MessageDialogFragment(private val type: Int) : androidx.fragment.app.DialogFragment() {
    private var _binding: FragmentDialogDefaultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // binding
        _binding = FragmentDialogDefaultBinding.inflate(requireActivity().layoutInflater)

        // message
        binding.message.text =
            getString(
                when (type) {
                    DIALOG_TYPE_PERMISSION -> R.string.message_dialog_permission
                    else -> R.string.message_invalid
                }
            )

        // positive button
        binding.positiveButton.setOnClickListener {
            when (type) {
                DIALOG_TYPE_PERMISSION -> {
                    if (Build.VERSION.SDK_INT >= 33)
                        requireActivity().requestPermissions(PERMISSION_IMAGES_VIDEOS, REQUEST_CODE_PERMISSION_IMAGES_VIDEOS)
                    else
                        requireActivity().requestPermissions(PERMISSION_STORAGE, REQUEST_CODE_PERMISSION_STORAGE)
                }
            }
            dismiss()
        }

        // negative button
        binding.negativeButton.run {
            isVisible = when (type) {
                DIALOG_TYPE_PERMISSION -> false
                else -> true
            }
            setOnClickListener {
                dismiss()
            }
        }

        return AlertDialog
            .Builder(requireContext())
            .apply { setView(binding.root) }
            .create()
            .apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCanceledOnTouchOutside(
                    when (type) {
                        DIALOG_TYPE_PERMISSION -> false
                        else -> true
                    }
                )
                setCancelable(
                    when (type) {
                        DIALOG_TYPE_PERMISSION -> false
                        else -> true
                    }
                )
            }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            /* width = */ (DeviceController.getWidthMax(requireContext()) * DIALOG_WIDTH_PERCENTAGE_DEFAULT).toInt(),
            /* height = */ ViewGroup.LayoutParams.WRAP_CONTENT
        )
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
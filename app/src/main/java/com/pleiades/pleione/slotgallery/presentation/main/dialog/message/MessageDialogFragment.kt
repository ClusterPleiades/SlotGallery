package com.pleiades.pleione.slotgallery.presentation.main.dialog.message

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_PERMISSION
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_WIDTH_PERCENTAGE_DEFAULT
import com.pleiades.pleione.slotgallery.Config.Companion.PERMISSION_IMAGES_VIDEOS
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_CODE_PERMISSION_IMAGES_VIDEOS
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.databinding.FragmentDialogMessageBinding
import com.pleiades.pleione.slotgallery.presentation.dialog.setLayoutSize
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageDialogFragment(private val type: Int) : androidx.fragment.app.DialogFragment() {
    private var _binding: FragmentDialogMessageBinding? = null
    private val binding get() = _binding!!
    private val fragmentViewModel: MessageDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // binding
        _binding = FragmentDialogMessageBinding.inflate(requireActivity().layoutInflater)

        // message
        binding.message.text =
            getString(
                if (type == DIALOG_TYPE_PERMISSION) {
                    R.string.message_dialog_permission
                } else {
                    R.string.message_invalid
                }
            )

        // positive button
        binding.positiveButton.setOnClickListener {
            if (type == DIALOG_TYPE_PERMISSION) {
                requireActivity().requestPermissions(
                    PERMISSION_IMAGES_VIDEOS,
                    REQUEST_CODE_PERMISSION_IMAGES_VIDEOS
                )
            }
            dismiss()
        }

        // negative button
        with(binding.negativeButton) {
            isVisible = type != DIALOG_TYPE_PERMISSION
            setOnClickListener { dismiss() }
        }

        return AlertDialog
            .Builder(requireContext())
            .apply { setView(binding.root) }
            .create()
            .apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCanceledOnTouchOutside(type != DIALOG_TYPE_PERMISSION)
                setCancelable(type != DIALOG_TYPE_PERMISSION)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayoutSize(
            width = fragmentViewModel.width * DIALOG_WIDTH_PERCENTAGE_DEFAULT,
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
    }
}

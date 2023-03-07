package com.pleiades.pleione.slotgallery.presentation.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_WIDTH_PERCENTAGE_DEFAULT
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_POSITION
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_KEY_COPY
import com.pleiades.pleione.slotgallery.controller.DeviceController
import com.pleiades.pleione.slotgallery.databinding.FragmentDialogProgressBinding

class ProgressDialogFragment : androidx.fragment.app.DialogFragment() {
    private var _binding: FragmentDialogProgressBinding? = null
    private val binding get() = _binding!!
    var isCanceled = false
    val progressBar by lazy { binding.progressBar }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // binding
        _binding = FragmentDialogProgressBinding.inflate(requireActivity().layoutInflater)

        // negative button
        binding.negativeButton.setOnClickListener {
            isCanceled = true
        }

        return AlertDialog
            .Builder(requireContext())
            .apply { setView(binding.root) }
            .create()
            .apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCanceledOnTouchOutside(false)
                setCancelable(false)
            }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayoutSize(
            width = (DeviceController.getWidthMax(requireContext()) * DIALOG_WIDTH_PERCENTAGE_DEFAULT).toInt(),
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
    }

    fun setFragmentResult(directoryPosition: Int) =
        parentFragmentManager.setFragmentResult(
            KEY_DIRECTORY_POSITION,
            Bundle().apply { putInt(KEY_DIRECTORY_POSITION, directoryPosition) }
        )

    fun setFragmentResult() =
        parentFragmentManager.setFragmentResult(REQUEST_KEY_COPY, Bundle())

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
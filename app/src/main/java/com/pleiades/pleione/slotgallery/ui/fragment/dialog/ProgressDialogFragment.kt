package com.pleiades.pleione.slotgallery.ui.fragment.dialog

import android.annotation.SuppressLint
import android.app.Activity
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

class ProgressDialogFragment(parentActivity: Activity) : androidx.fragment.app.DialogFragment() {
    private var _binding: FragmentDialogProgressBinding? = null
    private val binding get() = _binding!!

    private val builder = AlertDialog.Builder(parentActivity)

    var isCanceled = false

    init {
        // binding
        _binding = FragmentDialogProgressBinding.inflate(requireActivity().layoutInflater)

        // initialize progress bar
        binding.progressDialogProgress.progress = 0

        // set negative listener
        binding.negativeDialogProgress.setOnClickListener {
            isCanceled = true
        }

        // set dialog view
        builder.setView(binding.root)
    }

    @SuppressLint("InflateParams", "UseRequireInsteadOfGet")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // create dialog
        val dialog: AlertDialog = builder.create()

        // set transparent background
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // set canceled on touch outside
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    override fun onStart() {
        super.onStart()

        val width = (DeviceController.getWidthMax(requireContext()) * DIALOG_WIDTH_PERCENTAGE_DEFAULT).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.setLayout(width, height)

        // set cancelable
        dialog!!.setCancelable(false)
    }

    override fun onCancel(dialog: DialogInterface) {
        dismiss()
        super.onCancel(dialog)
    }

    fun setFragmentResult(directoryPosition: Int) {
        // set fragment result
        val resultBundle = Bundle()
        resultBundle.putInt(KEY_DIRECTORY_POSITION, directoryPosition)
        parentFragmentManager.setFragmentResult(KEY_DIRECTORY_POSITION, resultBundle)
    }

    fun setFragmentResult() {
        // set fragment result
        parentFragmentManager.setFragmentResult(REQUEST_KEY_COPY, Bundle())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
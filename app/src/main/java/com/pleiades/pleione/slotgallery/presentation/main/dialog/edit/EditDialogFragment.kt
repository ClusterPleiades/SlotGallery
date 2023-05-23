package com.pleiades.pleione.slotgallery.presentation.main.dialog.edit

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_RENAME_MEDIA
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_WIDTH_PERCENTAGE_DEFAULT
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_RESULT_KEY_RENAME_COMPLETE
import com.pleiades.pleione.slotgallery.databinding.FragmentDialogEditBinding
import com.pleiades.pleione.slotgallery.presentation.dialog.setLayoutSize
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditDialogFragment(private val type: Int) : androidx.fragment.app.DialogFragment() {
    private var _binding: FragmentDialogEditBinding? = null
    private val binding get() = _binding!!
    private val fragmentViewModel: EditDialogViewModel by viewModels()

    private val renameResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val toName = binding.edit.text.toString()

                fragmentViewModel.renameMedia(toName)

                parentFragmentManager.setFragmentResult(
                    REQUEST_RESULT_KEY_RENAME_COMPLETE,
                    Bundle().apply {
                        putString(REQUEST_RESULT_KEY_RENAME_COMPLETE, toName)
                    }
                )
            }
            dismiss()
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // binding
        _binding = FragmentDialogEditBinding.inflate(requireActivity().layoutInflater)

        // edit
        binding.edit.setText(fragmentViewModel.media?.name)

        // positive button
        binding.positiveButton.setOnClickListener {
            if (type == DIALOG_TYPE_RENAME_MEDIA) {
                val pendingIntent =
                    MediaStore.createWriteRequest(
                        requireContext().contentResolver,
                        setOf(fragmentViewModel.media?.uri)
                    )
                val intentSenderRequest =
                    IntentSenderRequest
                        .Builder(pendingIntent.intentSender)
                        .build()

                renameResultLauncher.launch(intentSenderRequest)
            }
        }

        // negative button
        with(binding.negativeButton) {
            setOnClickListener { dismiss() }
        }

        return AlertDialog
            .Builder(requireContext())
            .apply { setView(binding.root) }
            .create()
            .apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCanceledOnTouchOutside(true)
                setCancelable(true)
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

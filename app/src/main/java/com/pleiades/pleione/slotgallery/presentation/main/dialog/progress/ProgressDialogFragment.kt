package com.pleiades.pleione.slotgallery.presentation.main.dialog.progress

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_COPY_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_WIDTH_PERCENTAGE_DEFAULT
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_COPY_COMPLETE
import com.pleiades.pleione.slotgallery.databinding.FragmentDialogProgressBinding
import com.pleiades.pleione.slotgallery.presentation.dialog.setLayoutSize
import com.pleiades.pleione.slotgallery.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProgressDialogFragment(private val type: Int) : androidx.fragment.app.DialogFragment() {
    private var _binding: FragmentDialogProgressBinding? = null
    private val binding get() = _binding!!
    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDialogProgressBinding.inflate(requireActivity().layoutInflater)

        // negative button
        binding.negativeButton.setOnClickListener {
            activityViewModel.cancelProgress()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("RepeatOnLifecycleWrongUsage")
    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayoutSize(
            width = activityViewModel.width * DIALOG_WIDTH_PERCENTAGE_DEFAULT,
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // progress state
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                activityViewModel.progressDialogState.collect { state ->
                    with(binding.progressBar) {
                        progress = state.progress
                        max = state.maxProgress
                    }

                    if (state.progress == state.maxProgress || state.isCanceled) {
                        when (type) {
                            DIALOG_TYPE_COPY_DIRECTORY -> parentFragmentManager.setFragmentResult(KEY_COPY_COMPLETE, Bundle())
                        }
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
    }
}

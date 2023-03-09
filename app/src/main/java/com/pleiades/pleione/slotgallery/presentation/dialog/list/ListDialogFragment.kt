package com.pleiades.pleione.slotgallery.presentation.dialog.list

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_INFORMATION
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_SORT_MEDIA
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_SORT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_WIDTH_PERCENTAGE_DEFAULT
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_WIDTH_PERCENTAGE_RECYCLER
import com.pleiades.pleione.slotgallery.Config.Companion.FORMAT_DATE
import com.pleiades.pleione.slotgallery.Config.Companion.FORMAT_TIME
import com.pleiades.pleione.slotgallery.Config.Companion.INFORMATION_POSITION_DATE
import com.pleiades.pleione.slotgallery.Config.Companion.INFORMATION_POSITION_HEIGHT
import com.pleiades.pleione.slotgallery.Config.Companion.INFORMATION_POSITION_NAME
import com.pleiades.pleione.slotgallery.Config.Companion.INFORMATION_POSITION_PATH
import com.pleiades.pleione.slotgallery.Config.Companion.INFORMATION_POSITION_SIZE
import com.pleiades.pleione.slotgallery.Config.Companion.INFORMATION_POSITION_TIME
import com.pleiades.pleione.slotgallery.Config.Companion.INFORMATION_POSITION_WIDTH
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_MEDIA_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_SORT_ORDER
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.DeviceController
import com.pleiades.pleione.slotgallery.databinding.FragmentDialogListBinding
import com.pleiades.pleione.slotgallery.databinding.ItemDialogInformationBinding
import com.pleiades.pleione.slotgallery.databinding.ItemDialogRadioBinding
import com.pleiades.pleione.slotgallery.presentation.dialog.setLayoutSize
import com.pleiades.pleione.slotgallery.ui.media.MediaActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ListDialogFragment(private val type: Int) : androidx.fragment.app.DialogFragment() {
    private var _binding: FragmentDialogListBinding? = null
    private val binding get() = _binding!!
    private val fragmentViewModel: ListDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // binding
        _binding = FragmentDialogListBinding.inflate(requireActivity().layoutInflater)

        // list
        with(binding.list) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter =
                when (type) {
                    DIALOG_TYPE_SORT_DIRECTORY, DIALOG_TYPE_SORT_MEDIA -> RadioAdapter()
                    else -> InformationAdapter()
                }
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

    override fun onStart() {
        super.onStart()

        val widthMultiplier =
            if (type == DIALOG_TYPE_INFORMATION) DIALOG_WIDTH_PERCENTAGE_DEFAULT
            else DIALOG_WIDTH_PERCENTAGE_RECYCLER

        dialog?.window?.setLayoutSize(
            width = (DeviceController.getWidthMax(requireContext()) * widthMultiplier),
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
    }

    inner class InformationAdapter : RecyclerView.Adapter<InformationAdapter.ViewHolder>() {
        inner class ViewHolder(val binding: ItemDialogInformationBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                ItemDialogInformationBinding.bind(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_information, parent, false)
                )
            )

        // TODO check
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder.binding) {
                // title
                title.text = resources.getStringArray(R.array.information)[position]

                // content
                val media = (activity as MediaActivity).getCurrentContent()
                content.text =
                    when (position) {
                        INFORMATION_POSITION_NAME -> media.name
                        INFORMATION_POSITION_DATE -> SimpleDateFormat(FORMAT_DATE, Locale.getDefault()).format(media.date * 1000)
                        INFORMATION_POSITION_TIME -> SimpleDateFormat(FORMAT_TIME, Locale.getDefault()).format(media.date * 1000)
                        INFORMATION_POSITION_SIZE -> media.size
                        INFORMATION_POSITION_WIDTH -> media.width.toString()
                        INFORMATION_POSITION_HEIGHT -> media.height.toString()
                        INFORMATION_POSITION_PATH -> media.relativePath + media.name
                        else -> null
                    }
            }
        }

        override fun getItemCount() = resources.getStringArray(R.array.information).size
    }

    inner class RadioAdapter : RecyclerView.Adapter<RadioAdapter.ViewHolder>() {
        inner class ViewHolder(val binding: ItemDialogRadioBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.radioButton.setOnClickListener {
                    when (type) {
                        DIALOG_TYPE_SORT_DIRECTORY -> {
                            fragmentViewModel.putDirectorySortOrderPosition(bindingAdapterPosition)
                            parentFragmentManager.setFragmentResult(
                                KEY_DIRECTORY_SORT_ORDER,
                                Bundle().apply { putInt(KEY_DIRECTORY_SORT_ORDER, bindingAdapterPosition) }
                            )
                        }
                        DIALOG_TYPE_SORT_MEDIA -> {
                            fragmentViewModel.putMediaSortOrderPosition(bindingAdapterPosition)
                            parentFragmentManager.setFragmentResult(
                                KEY_DIRECTORY_SORT_ORDER,
                                Bundle().apply { putInt(KEY_MEDIA_SORT_ORDER, bindingAdapterPosition) }
                            )
                        }
                    }
                    dismiss()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemDialogRadioBinding.bind(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_radio, parent, false)
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder.binding) {
                // radio
                with(radioButton) {
                    text = resources.getStringArray(R.array.sort)[position]
                    isChecked =
                        position == when (type) {
                            DIALOG_TYPE_SORT_DIRECTORY -> fragmentViewModel.getDirectorySortOrderPosition()
                            DIALOG_TYPE_SORT_MEDIA -> fragmentViewModel.getMediaSortOrderPosition()
                            else -> 0
                        }
                }
            }
        }

        override fun getItemCount() = resources.getStringArray(R.array.sort).size
    }
}
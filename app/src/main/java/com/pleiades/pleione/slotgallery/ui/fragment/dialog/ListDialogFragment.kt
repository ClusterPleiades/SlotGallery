package com.pleiades.pleione.slotgallery.ui.fragment.dialog

import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_INFORMATION
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_SORT_CONTENT
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
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_CONTENT_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.PREFS
import com.pleiades.pleione.slotgallery.NonScrollLinearLayoutManager
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.DeviceController
import com.pleiades.pleione.slotgallery.databinding.FragmentDialogListBinding
import com.pleiades.pleione.slotgallery.ui.activity.ImageActivity
import java.text.SimpleDateFormat
import java.util.*

class ListDialogFragment(private val type: Int) : androidx.fragment.app.DialogFragment() {
    private var _binding: FragmentDialogListBinding? = null
    private val binding get() = _binding!!
    private val prefs by lazy { requireContext().getSharedPreferences(PREFS, MODE_PRIVATE) }
    private val editor by lazy { prefs.edit() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // binding
        _binding = FragmentDialogListBinding.inflate(requireActivity().layoutInflater)

        // list
        binding.list.run {
            setHasFixedSize(true)
            layoutManager = NonScrollLinearLayoutManager(requireContext())
            adapter =
                when (type) {
                    DIALOG_TYPE_SORT_DIRECTORY, DIALOG_TYPE_SORT_CONTENT -> RadioRecyclerAdapter()
                    else -> InformationRecyclerAdapter()
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

        dialog?.window?.setLayout(
            /* width = */ (DeviceController.getWidthMax(requireContext()) * widthMultiplier).toInt(),
            /* height = */ ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
    }

    inner class InformationRecyclerAdapter : RecyclerView.Adapter<InformationRecyclerAdapter.InformationViewHolder>() {
        private val textArray: Array<String> = context!!.resources.getStringArray(R.array.information)

        inner class InformationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.title_information)
            val contentTextView: TextView = itemView.findViewById(R.id.content_information)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): InformationRecyclerAdapter.InformationViewHolder {
            val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_dialog_information, viewGroup, false)
            return InformationViewHolder(view)
        }

        override fun onBindViewHolder(holder: InformationRecyclerAdapter.InformationViewHolder, position: Int) {
            // title
            holder.titleTextView.text = textArray[position]

            // content
            val content = (activity as ImageActivity).getCurrentContent()
            holder.contentTextView.text = when (position) {
                INFORMATION_POSITION_NAME -> content.name
                INFORMATION_POSITION_DATE -> SimpleDateFormat(FORMAT_DATE, Locale.getDefault()).format(content.date * 1000)
                INFORMATION_POSITION_TIME -> SimpleDateFormat(FORMAT_TIME, Locale.getDefault()).format(content.date * 1000)
                INFORMATION_POSITION_SIZE -> content.size
                INFORMATION_POSITION_WIDTH -> content.width.toString()
                INFORMATION_POSITION_HEIGHT -> content.height.toString()
                INFORMATION_POSITION_PATH -> content.relativePath + content.name
                else -> null
            }
        }

        override fun getItemCount(): Int {
            return textArray.size
        }
    }

    inner class RadioRecyclerAdapter : RecyclerView.Adapter<RadioRecyclerAdapter.RadioViewHolder>() {
        private val radioPosition = when (type) {
            DIALOG_TYPE_SORT_DIRECTORY -> prefs.getInt(KEY_DIRECTORY_SORT_ORDER, 0)
            DIALOG_TYPE_SORT_CONTENT -> prefs.getInt(KEY_CONTENT_SORT_ORDER, 0)
            else -> 0
        }
        private val textArray: Array<String> = when (type) {
            DIALOG_TYPE_SORT_DIRECTORY -> context!!.resources.getStringArray(R.array.sort)
            DIALOG_TYPE_SORT_CONTENT -> context!!.resources.getStringArray(R.array.sort)
            else -> arrayOf("")
        }

        inner class RadioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val radioButton: RadioButton = itemView.findViewById(R.id.radio_dialog)

            init {
                radioButton.setOnClickListener {
                    // case error
                    val position = bindingAdapterPosition
                    if (position == RecyclerView.NO_POSITION)
                        return@setOnClickListener

                    when (type) {
                        DIALOG_TYPE_SORT_DIRECTORY -> {
                            editor.putInt(KEY_DIRECTORY_SORT_ORDER, position)
                            editor.apply()

                            // set fragment result
                            val resultBundle = Bundle()
                            resultBundle.putInt(KEY_DIRECTORY_SORT_ORDER, position)
                            parentFragmentManager.setFragmentResult(KEY_DIRECTORY_SORT_ORDER, resultBundle)
                        }
                        DIALOG_TYPE_SORT_CONTENT -> {
                            editor.putInt(KEY_CONTENT_SORT_ORDER, position)
                            editor.apply()

                            // set fragment result
                            val resultBundle = Bundle()
                            resultBundle.putInt(KEY_CONTENT_SORT_ORDER, position)
                            parentFragmentManager.setFragmentResult(KEY_CONTENT_SORT_ORDER, resultBundle)
                        }
                    }

                    // dismiss dialog
                    dismiss()
                }
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RadioViewHolder {
            val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_dialog_radio, viewGroup, false)
            return RadioViewHolder(view)
        }

        override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
            // set text view
            holder.radioButton.text = textArray[position]

            // set radio checked
            holder.radioButton.isChecked = position == radioPosition
        }

        override fun getItemCount(): Int {
            return textArray.size
        }
    }
}
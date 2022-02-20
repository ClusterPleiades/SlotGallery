package com.pleiades.pleione.slotgallery.ui.fragment.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
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
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
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
import com.pleiades.pleione.slotgallery.ui.activity.ImageActivity
import java.text.SimpleDateFormat
import java.util.*


class RecyclerDialogFragment(private val type: Int) : androidx.fragment.app.DialogFragment() {
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // initialize builder
        val builder = context?.let { AlertDialog.Builder(it) }

        // initialize prefs
        prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        editor = prefs.edit()

        // initialize activity
        val activity: FragmentActivity? = activity
        if (activity != null) {
            // initialize dialog view
            val dialogView: View = activity.layoutInflater.inflate(R.layout.fragment_dialog_recycler, null)

            // initialize recycler view
            val dialogRecyclerView: RecyclerView = dialogView.findViewById(R.id.recycler_dialog)
            dialogRecyclerView.setHasFixedSize(true)
            dialogRecyclerView.layoutManager = NonScrollLinearLayoutManager(requireContext())
            dialogRecyclerView.adapter = when (type) {
                DIALOG_TYPE_SORT_DIRECTORY,
                DIALOG_TYPE_SORT_CONTENT -> RadioRecyclerAdapter()
                else -> InformationRecyclerAdapter()
            }

            // set dialog view
            builder!!.setView(dialogView)
        }

        // create dialog
        val dialog: AlertDialog = builder!!.create()

        // set transparent background
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // set canceled on touch outside
        dialog.setCanceledOnTouchOutside(true)

        return dialog
    }

    override fun onStart() {
        super.onStart()

        val screenWidth = DeviceController.getWidthMax(requireContext())
        val width = if (type == DIALOG_TYPE_INFORMATION) screenWidth * DIALOG_WIDTH_PERCENTAGE_DEFAULT else screenWidth * DIALOG_WIDTH_PERCENTAGE_RECYCLER
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.setLayout(width.toInt(), height)

        // set cancelable
        dialog!!.setCancelable(true)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
    }

    inner class InformationRecyclerAdapter : RecyclerView.Adapter<InformationRecyclerAdapter.InformationViewHolder>() {
        private val textArray: Array<String> = context!!.resources.getStringArray(R.array.information)

        inner class InformationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.title_dialog)
            val contentTextView: TextView = itemView.findViewById(R.id.content_dialog)
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
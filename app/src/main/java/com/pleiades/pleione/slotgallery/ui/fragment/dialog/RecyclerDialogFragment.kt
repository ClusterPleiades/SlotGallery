package com.pleiades.pleione.slotgallery.ui.fragment.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_TYPE_SORT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.DIALOG_WIDTH_PERCENTAGE_RECYCLER
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.PREFS
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.controller.DeviceController

class RecyclerDialogFragment(private val type: Int) : androidx.fragment.app.DialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // initialize builder
        val builder = context?.let { AlertDialog.Builder(it) }

        // initialize activity
        val activity: FragmentActivity? = activity
        if (activity != null) {
            // initialize dialog view
            val dialogView: View = activity.layoutInflater.inflate(R.layout.fragment_dialog_recycler, null)

            // initialize recycler view
            val dialogRecyclerView: RecyclerView = dialogView.findViewById(R.id.recycler_dialog)
            dialogRecyclerView.setHasFixedSize(true)
            dialogRecyclerView.layoutManager = LinearLayoutManager(context)
            dialogRecyclerView.adapter = DialogRecyclerAdapter()

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

        val width = (DeviceController.getWidthMax(requireContext()) * DIALOG_WIDTH_PERCENTAGE_RECYCLER).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.setLayout(width, height)

        // set cancelable
        dialog!!.setCancelable(true)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
    }

    inner class DialogRecyclerAdapter : RecyclerView.Adapter<DialogRecyclerAdapter.DialogViewHolder>() {
        private val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        private val editor = prefs.edit()
        private val radioPosition = prefs.getInt(KEY_DIRECTORY_SORT_ORDER, 0)
        private lateinit var textArray: Array<String>

        inner class DialogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val radioButton: RadioButton = itemView.findViewById(R.id.radio_dialog)

            init {
                radioButton.setOnClickListener {
                    // case error
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                    when (type) {
                        DIALOG_TYPE_SORT_DIRECTORY -> {
                            editor.putInt(KEY_DIRECTORY_SORT_ORDER, position)
                            editor.apply()

                            // set fragment result
                            val resultBundle = Bundle()
                            resultBundle.putInt(KEY_DIRECTORY_SORT_ORDER, position)
                            parentFragmentManager.setFragmentResult(KEY_DIRECTORY_SORT_ORDER, resultBundle)
                        }
                    }

                    // dismiss dialog
                    dismiss()
                }
            }
        }

        // constructor
        init {
            if (type == DIALOG_TYPE_SORT_DIRECTORY) {
                textArray = context!!.resources.getStringArray(R.array.sort)
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): DialogViewHolder {
            val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_dialog, viewGroup, false)
            return DialogViewHolder(view)
        }

        override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
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
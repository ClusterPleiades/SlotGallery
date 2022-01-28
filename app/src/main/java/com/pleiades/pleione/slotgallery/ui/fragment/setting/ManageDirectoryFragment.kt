package com.pleiades.pleione.slotgallery.ui.fragment.setting

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pleiades.pleione.slotgallery.Config.Companion.SETTING_POSITION_DIRECTORY
import com.pleiades.pleione.slotgallery.R

class ManageDirectoryFragment : Fragment() {
    companion object {
        fun newInstance(): ManageDirectoryFragment {
            return ManageDirectoryFragment()
        }
    }

    private lateinit var rootView: View
    private lateinit var recyclerAdapter: ManageDirectoryRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_manage, container, false)

        // set title
        activity?.title = resources.getStringArray(R.array.setting)[SETTING_POSITION_DIRECTORY]

        // set options menu
        setHasOptionsMenu(true)

        // initialize slot recycler adapter
        recyclerAdapter = ManageDirectoryRecyclerAdapter()

        // initialize slot recycler view
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_manage)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = recyclerAdapter

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // case default
        inflater.inflate(R.menu.menu_manage, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                // TODO
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class ManageDirectoryRecyclerAdapter : RecyclerView.Adapter<ManageDirectoryRecyclerAdapter.ManageDirectoryViewHolder>() {
        inner class ManageDirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleEditText: EditText = itemView.findViewById(R.id.title_edit)
            val layout: ConstraintLayout = itemView.findViewById(R.id.layout_edit)
            private val saveButton: ImageButton = itemView.findViewById(R.id.save_edit)
            private val removeButton: ImageButton = itemView.findViewById(R.id.remove_edit)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageDirectoryViewHolder {
            return ManageDirectoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_edit, parent, false))
        }

        override fun onBindViewHolder(holder: ManageDirectoryViewHolder, position: Int) {
        }

        override fun getItemCount(): Int {
            return 1
        }
    }
}
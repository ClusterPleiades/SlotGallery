package com.pleiades.pleione.slotgallery.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.ui.activity.SettingActivity

class DirectoryFragment : Fragment() {
    companion object {
        fun newInstance(): DirectoryFragment {
            return DirectoryFragment()
        }
    }

    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // initialize root view
        rootView = inflater.inflate(R.layout.fragment_main, container, false)

        // set title
        activity?.title = ""

        // set options menu
        setHasOptionsMenu(true)

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val actionBar = (activity as AppCompatActivity).supportActionBar!!

        // case default
        inflater.inflate(R.menu.menu_directory, menu)
        actionBar.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == R.id.setting) {
            val intent = Intent(context, SettingActivity::class.java)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun onBackPressed(): Boolean {
        return false
    }
}
package com.pleiades.pleione.slotgallery.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pleiades.pleione.slotgallery.R

class SettingActivity : AppCompatActivity() {
    private lateinit var settingArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // set navigation color
        window.navigationBarColor = Color.WHITE

        // initialize appbar
        val appbar = findViewById<View>(R.id.appbar_setting)
        val toolbar: Toolbar = appbar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // initialize setting array
        settingArray = resources.getStringArray(R.array.setting)

        // initialize setting recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_setting)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SettingRecyclerAdapter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    inner class SettingRecyclerAdapter : RecyclerView.Adapter<SettingRecyclerAdapter.SettingViewHolder>() {
        inner class SettingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.title_setting)
            val contentTextView: TextView = itemView.findViewById(R.id.content_setting)

            init {
                itemView.setOnClickListener { v: View? ->
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                    // TODO
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
            return SettingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_setting, parent, false))
        }

        override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
            holder.titleTextView.text = settingArray[position]
        }

        override fun getItemCount(): Int {
            return settingArray.size
        }
    }
}
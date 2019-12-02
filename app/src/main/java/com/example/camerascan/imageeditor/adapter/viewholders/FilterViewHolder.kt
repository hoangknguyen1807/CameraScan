package com.example.camerascan.imageeditor.adapter.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.camerascan.R

class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    var icon: ImageView = itemView.findViewById<View>(R.id.filter_image) as ImageView
    @JvmField
    var text: TextView = itemView.findViewById<View>(R.id.filter_name) as TextView
}
package com.example.camerascan.imageeditor.adapter.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.camerascan.R

class ColorViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    var colorPanelView: View = itemView.findViewById(R.id.color_panel_view)
}
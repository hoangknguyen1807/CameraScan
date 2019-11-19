package com.example.camerascan.imageeditor.adapter.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.camerascan.R

class MoreViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    var moreButton: View = itemView.findViewById(R.id.color_panel_more)
}
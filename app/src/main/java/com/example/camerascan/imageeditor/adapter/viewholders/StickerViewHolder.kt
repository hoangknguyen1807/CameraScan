package com.example.camerascan.imageeditor.adapter.viewholders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.camerascan.R

class StickerViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    var image: ImageView = itemView.findViewById(R.id.img)
}
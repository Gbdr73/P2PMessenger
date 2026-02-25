package com.example.p2pmessenger.ui

import androidx.recyclerview.widget.DiffUtil

class DialogsDiffCallback: DiffUtil.ItemCallback<DialogsItem>() {
    override fun areItemsTheSame(oldItem: DialogsItem, newItem: DialogsItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DialogsItem, newItem: DialogsItem): Boolean {
        return oldItem == newItem
    }
}

package com.example.p2pmessenger.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.p2pmessenger.databinding.DialogItemBinding

class DialogsAdapter: ListAdapter<DialogsItem, DialogsViewHolder>(DialogsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogsViewHolder {
        val binding = DialogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DialogsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DialogsViewHolder, position: Int) {
        val dialogsItem = getItem(position)
        val binding = holder.binding

        binding.textName.setText(dialogsItem.name)
        binding.message.setText(dialogsItem.message)
    }

    fun submitNewList(list: List<DialogsItem>){
        submitList(list)
    }
}

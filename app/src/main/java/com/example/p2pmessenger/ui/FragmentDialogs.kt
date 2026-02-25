package com.example.p2pmessenger.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.p2pmessenger.R
import com.example.p2pmessenger.databinding.FragmentDialogsBinding
import com.example.p2pmessenger.getChatsList
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class FragmentDialogs : Fragment() {
    private var _binding: FragmentDialogsBinding? = null
    private val binding: FragmentDialogsBinding
        get() = _binding ?: throw RuntimeException("FragmentDialogsBinding == null")

    private var chatList: MutableList<DialogsItem> = mutableListOf()
    private lateinit var adapter: DialogsAdapter
    var lastId = 0
    var previousItemsCount = 0
    private var pageLoad = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogsBinding.inflate(inflater, container, false)
        adapter = DialogsAdapter()
        binding.recyclerView.adapter = adapter
        createItems()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.beginChat.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, FragmentUsername.newInstance())
                .commit()
        }
    }

    private fun createItems(){
    val dialogsList = getChatsList()
    var message: String = ""
    for (i in 0..(dialogsList.size - 1)) {
            if( dialogsList[i].messageList.size > 0 ) {
                message = dialogsList[i].messageList[dialogsList[i].messageList.size - 1]
            }
            else {
                message = ""
            }
            chatList.add(
                DialogsItem(
                    lastId++,
                    dialogsList[i].userName,
                    message,
                )
            )
        }
        adapter.submitList(chatList)
        adapter.notifyDataSetChanged()
    }
    companion object {
        fun newInstance() = FragmentDialogs()
    }
}

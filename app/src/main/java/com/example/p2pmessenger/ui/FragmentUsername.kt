package com.example.p2pmessenger.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.p2pmessenger.R
import com.example.p2pmessenger.databinding.FragmentUsernameBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class FragmentUsername : Fragment() {
    private var _binding: FragmentUsernameBinding? = null
    private val binding: FragmentUsernameBinding
        get() = _binding ?: throw RuntimeException("FragmentUsernameBinding == null")

    private val viewModel: UsernameViewModel by viewModels()
    private lateinit var context: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
        viewModel.setContext(context)
        viewModel.loadOwner()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsernameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        addTextChangedListener()
        binding.buttonOk.setOnClickListener {
            if( !viewModel.addChat(viewLifecycleOwner) )
            {
                val builder = AlertDialog.Builder(context)
                builder.apply {
                    setTitle("Error!")
                    setMessage("Username is empty!")
                    setPositiveButton("Try again", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            p0?.cancel()
                        }
                    })
                    create()
                    show()
                }
            }
        }
        binding.buttonCancel.setOnClickListener {
            openDialogs()
        }
    }

    fun observeViewModel() {
        viewModel.errorServer.observe(viewLifecycleOwner) {
            with(binding) {
                val error: String = it
                if (!error.isNullOrEmpty()) {
                    val builder = AlertDialog.Builder(context)
                    builder.apply {
                        setTitle("Error!")
                        setMessage(error)
                        setPositiveButton("Try again", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                p0?.cancel()
                            }
                        })
                        create()
                        show()
                    }
                }
            }
        }

        viewModel.canContinue.observe(viewLifecycleOwner) {
            if (it) {
                openDialogs()
            }
        }
    }

    private fun openDialogs() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, FragmentDialogs.newInstance())
            .commit()
    }

    private fun addTextChangedListener() {
        with(binding) {
            editTextName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    viewModel.setName(editTextName.text.toString())
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
    }

    companion object {
        fun newInstance() = FragmentUsername()
    }
}

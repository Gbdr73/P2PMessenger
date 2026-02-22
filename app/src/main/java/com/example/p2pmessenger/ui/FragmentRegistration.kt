package com.example.p2pmessenger.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.p2pmessenger.R
import dagger.hilt.android.AndroidEntryPoint
import com.example.p2pmessenger.databinding.FragmentRegistrationBinding
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import com.google.android.material.progressindicator.CircularProgressIndicator

@AndroidEntryPoint
class FragmentRegistration : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding: FragmentRegistrationBinding
        get() = _binding ?: throw RuntimeException("FragmentNameBinding == null")

    private lateinit var context: Context
   private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        addTextChangedListeners()
        binding.indicator.hide()
        binding.buttonNext.setOnClickListener {
            binding.indicator.show()
            viewModel.validateData(viewLifecycleOwner)
        }
        if( viewModel.isUserRegistered() )
        {
            val builder = AlertDialog.Builder(context)
            builder.apply {
                setTitle("Registration success!")
                setMessage("User already registered!")
                setPositiveButton("Next", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0?.cancel()
                        binding.indicator.hide()
                    }
                })
                create()
                show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
        viewModel.setContext(context)
        viewModel.loadUser()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun observeViewModel() {
        viewModel.errorEmptyName.observe(viewLifecycleOwner) {
            with(binding) {
                if (it) {
                    editTextName.error = String.format(
                        resources.getString(R.string.empty_field),
                        textInputName.hint.toString()
                    )
                    binding.indicator.hide()
                } else {
                    editTextName.error = null
                }
            }
        }

        viewModel.errorEmptyPassword.observe(viewLifecycleOwner) {
            with(binding) {
                if (it) {
                    editTextPassword.error = String.format(
                        resources.getString(R.string.empty_field),
                        textInputPassword.hint.toString()
                    )
                    binding.indicator.hide()
                } else {
                    editTextPassword.error = null
                }
            }
        }

        viewModel.errorServer.observe(viewLifecycleOwner) {
            with(binding) {
                val error: String = it
                if (!error.isNullOrEmpty()) {
                    binding.indicator.hide()
                    val builder = AlertDialog.Builder(context)
                    builder.apply {
                        setTitle("Registration error!")
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
                binding.indicator.hide()
                val builder = AlertDialog.Builder(context)
                builder.apply {
                    setTitle("Registration success!")
                    setMessage("Registration successfully completed!")
                    setPositiveButton("Next", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            p0?.cancel()
                        }
                    })
                    create()
                    show()
                }
/*                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, FragmentAddress.newInstance())
                    .commit()*/
            }
        }
    }

    private fun addTextChangedListeners() {
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

            editTextPassword.addTextChangedListener(object : TextWatcher {
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
                    viewModel.setPassword(editTextPassword.text.toString())
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
    }
}

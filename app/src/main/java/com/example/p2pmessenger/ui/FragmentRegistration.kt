package com.example.p2pmessenger.ui

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

@AndroidEntryPoint
class FragmentRegistration : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding: FragmentRegistrationBinding
        get() = _binding ?: throw RuntimeException("FragmentNameBinding == null")

   private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        addTextChangedListeners()
        binding.buttonNext.setOnClickListener {
            viewModel.validateData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
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
                } else {
                    editTextPassword.error = null
                }
            }
        }

        viewModel.canContinue.observe(viewLifecycleOwner) {
/*            if (it) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, FragmentAddress.newInstance())
                    .commit()
            }*/
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

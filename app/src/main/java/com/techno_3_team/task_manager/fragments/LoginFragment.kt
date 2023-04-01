package com.techno_3_team.task_manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.techno_3_team.task_manager.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {
    private var binding: LoginFragmentBinding? = null
    private val _binding: LoginFragmentBinding
        get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater)
        return _binding.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
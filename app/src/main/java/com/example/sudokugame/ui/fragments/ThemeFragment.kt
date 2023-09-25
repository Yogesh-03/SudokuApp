package com.example.sudokugame.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.sudokugame.R
import com.example.sudokugame.databinding.FragmentThemeBinding
import com.example.sudokugame.viewmodel.PlaySudoku

class ThemeFragment : Fragment() {
    private lateinit var binding:FragmentThemeBinding
    private lateinit var viewModel:PlaySudoku
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentThemeBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(this)[PlaySudoku::class.java]

        Toast.makeText(requireContext(), "Opened", Toast.LENGTH_SHORT).show()
        Log.d("THEME", "OPENED")

        binding.cvDarkTheme.setOnClickListener {
                binding.cvDarkTheme.strokeWidth = 4
                binding.cvDarkTheme.strokeColor = ContextCompat.getColor(requireContext(), R.color.blue)
                viewModel.sudokuGame.changeTheme("dark")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }


}
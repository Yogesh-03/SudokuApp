package com.example.sudokugame.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.sudokugame.databinding.FragmentNewGameDialogBinding
import com.example.sudokugame.ui.SudokuPlayActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewGameDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNewGameDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNewGameDialogBinding.inflate(layoutInflater, container, false)
        binding.easyNewGameLinearLayout.setOnClickListener {
            //progerssBar.visibility = View.VISIBLE
            //dialog?.window?.attributes?.windowAnimations  = R.drawable.slide_in
            val intent = Intent(activity, SudokuPlayActivity::class.java)
            val bundle = Bundle()
            bundle.putString("easy", "Easy")
            intent.putExtras(bundle)
            startActivity(intent)
            //progerssBar.visibility = View.INVISIBLE
        }

        binding.mediumNewGameLinearLayout.setOnClickListener {
            val intent = Intent(activity, SudokuPlayActivity::class.java)
            val bundle = Bundle()
            bundle.putString("medium", "Medium")
            intent.putExtras(bundle)
            startActivity(intent)
        }

        binding.hardNewGameLinearLayout.setOnClickListener {
            val intent = Intent(activity, SudokuPlayActivity::class.java)
            val bundle = Bundle()
            bundle.putString("hard", "Hard")
            intent.putExtras(bundle)
            startActivity(intent)
        }

        binding.expertNewGameLinearLayout.setOnClickListener {
            val intent = Intent(activity, SudokuPlayActivity::class.java)
            val bundle = Bundle()
            bundle.putString("expert", "Expert")
            intent.putExtras(bundle)
            startActivity(intent)
        }
        return binding.root
    }
}
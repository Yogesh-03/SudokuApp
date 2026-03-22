package com.gamopy.sudoku.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gamopy.sudoku.databinding.FragmentNewGameDialogBinding
import com.gamopy.sudoku.sharedpreferences.UserSettings
import com.gamopy.sudoku.ui.SudokuPlayActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewGameDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNewGameDialogBinding

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNewGameDialogBinding.inflate(layoutInflater, container, false)
        binding.pbNewGame.visibility = View.GONE

        sharedPreferences =
            requireActivity().getSharedPreferences(UserSettings().PREFERENCES, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val bundle = Bundle()
        val userSettings = UserSettings()
        val layoutList = listOf(
            binding.easyNewGameLinearLayout, binding.mediumNewGameLinearLayout,
            binding.hardNewGameLinearLayout, binding.expertNewGameLinearLayout
        )

        layoutList.forEachIndexed { index, linearLayout ->
            linearLayout.setOnClickListener {
                val intent = Intent(activity, SudokuPlayActivity::class.java)
                when (index) {
                    0 -> {
                        bundle.putString("easy", "Easy")
                        editor.putString(userSettings.CURRENT_GAME_DIFFICULTY, "Easy")
                    }

                    1 -> {
                        bundle.putString("medium", "Medium")
                        editor.putString(userSettings.CURRENT_GAME_DIFFICULTY, "Medium")
                    }

                    2 -> {
                        bundle.putString("hard", "Hard")
                        editor.putString(userSettings.CURRENT_GAME_DIFFICULTY, "Hard")
                    }

                    3 -> {
                        bundle.putString("expert", "Expert")
                        editor.putString(userSettings.CURRENT_GAME_DIFFICULTY, "Expert")
                    }
                }

                binding.pbNewGame.visibility = View.VISIBLE
                userSettings.setCurrentGame(true)
                editor.putBoolean(userSettings.IS_CURRENT_GAME, userSettings.getCurrentGame())
                editor.apply()
                intent.putExtras(bundle)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent)
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.pbNewGame.visibility = View.INVISIBLE
    }
}
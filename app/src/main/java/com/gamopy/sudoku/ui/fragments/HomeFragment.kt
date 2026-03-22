package com.gamopy.sudoku.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.gamopy.sudoku.R
import com.gamopy.sudoku.databinding.FragmentHomeBinding
import com.gamopy.sudoku.sharedpreferences.UserSettings
import com.gamopy.sudoku.ui.SudokuPlayActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val settings = UserSettings()
    private var isCurrentGame = false
    private val newGameDialogFragment = NewGameDialogFragment()
    private val cameraFragment = CameraFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        //Setting width of New Game and Continue Button to 2/3-10 of screen width
        binding.newGameButton.width =
            2 * (Resources.getSystem().displayMetrics.widthPixels) / 3 + 80
        binding.continueGameButton.width =
            2 * (Resources.getSystem().displayMetrics.widthPixels) / 3 + 80

        binding.pbGameLoad.visibility = View.GONE
        binding.newGameButton.isEnabled = true


        //Setting color of sudoku "classic" to blue
        val spannableString = SpannableString("Sudoku Classic")
        spannableString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(requireContext(), R.color.blue)
            ), 7, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.sudokuClassic.text = spannableString

        cameraFragment.isCancelable = true

        binding.ivCamera.setOnClickListener {
            //captureImage()
            cameraFragment.show(childFragmentManager, cameraFragment.tag)
//            startActivity(Intent(requireActivity(), CameraFragment::class.java))
        }



        binding.continueGameButton.setOnClickListener {
            if (isCurrentGame) {
                binding.pbGameLoad.visibility = View.VISIBLE
                binding.continueGameButton.visibility = View.INVISIBLE
                binding.newGameButton.isClickable = false

                val bundle = Bundle()
                val intent = Intent(requireActivity(), SudokuPlayActivity::class.java)
                bundle.putString("continue", "continue")
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }

        //Getting clicked on New Game
        binding.newGameButton.setOnClickListener {
            binding.newGameButton.apply {
                isEnabled = false
                alpha = 0.9F
            }

            //Setting button enables = false for 800ms to prevent multiple clicks
            Handler(Looper.getMainLooper()).postDelayed({
                binding.newGameButton.apply {
                    isEnabled = true
                    alpha = 1F
                }
            }, 800)

            //Opening the Dialog
            newGameDialogFragment.show(childFragmentManager, newGameDialogFragment.tag)
        }
        if (newGameDialogFragment.isVisible) newGameDialogFragment.dismiss()
        return binding.root
    }




    override fun onResume() {
        super.onResume()
        binding.pbGameLoad.visibility = View.GONE
        binding.newGameButton.isClickable = true
        if (newGameDialogFragment.isVisible) newGameDialogFragment.dismiss()
        if (requireActivity().getSharedPreferences(
                settings.PREFERENCES, Context.MODE_PRIVATE
            ).getBoolean(settings.IS_CURRENT_GAME, settings.getCurrentGame())
        ) {
            binding.continueGameButton.apply {
                isEnabled = true
                isClickable = true
                visibility = View.VISIBLE
                isCurrentGame = true
            }
        } else {
            binding.continueGameButton.apply {
                isEnabled = false
                isClickable = false
                visibility = View.GONE
                isCurrentGame = false
            }
        }
    }
}
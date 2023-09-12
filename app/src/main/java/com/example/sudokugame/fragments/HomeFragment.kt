package com.example.sudokugame.fragments

import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.sudokugame.R
import com.example.sudokugame.databinding.ActivityMainBinding
import com.example.sudokugame.databinding.ActivitySudokuPlayBinding
import com.example.sudokugame.fragments.dialog.NewGameDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        //Assigning Views by the IDs
        val newGameButton = view.findViewById<Button>(R.id.newGameButton)
        val continueGameButton = view.findViewById<Button>(R.id.continueGameButton)
        val sudokuGame = view.findViewById<TextView>(R.id.sudokuClassic)
        val dailyChallengeDate = view.findViewById<TextView>(R.id.dailyChallengeDate)

        //Settings current date in Daily Challenges date view
        val calendar: Calendar = Calendar.getInstance()
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val monthInWords: String = monthFormat.format(calendar.time)
        val currentDate: String = "$monthInWords-$day"
        dailyChallengeDate.text = currentDate

        //Setting width of New Game and Continue Button to 2/3-10 of screen width
        newGameButton.width = 2*(Resources.getSystem().displayMetrics.widthPixels)/3-10
        continueGameButton.width = 2*(Resources.getSystem().displayMetrics.widthPixels)/3-10

        //Setting color of sudoku "classic" to blue
        val spannableString:SpannableString = SpannableString("Sudoku Classic")
        spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.blue)),7,14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sudokuGame.text = spannableString

        //Getting clicked on New Game
        newGameButton.setOnClickListener {
            newGameButton.isEnabled = false
            newGameButton.alpha = 0.9F

            //Setting button enables = false for 800ms to prevent multiple clicks
            Handler().postDelayed({
                newGameButton.isEnabled = true
                newGameButton.alpha = 1F
            },800)
            //Opening the Dialog
            val newGameDialogFragment: NewGameDialogFragment = NewGameDialogFragment()
            newGameDialogFragment.show(childFragmentManager, newGameDialogFragment.tag)

        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
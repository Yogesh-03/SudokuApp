package com.example.sudokugame.fragments.dialog

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.cardview.widget.CardView
import com.example.sudokugame.R
import com.example.sudokugame.SudokuPlayActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class NewGameDialogFragment : BottomSheetDialogFragment() {


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
        val view =  inflater.inflate(R.layout.fragment_new_game_dialog, container, false)
        val easyNewGame = view.findViewById<CardView>(R.id.easyNewGame)
        //progerssBar = view.findViewById(R.id.progressBar)
        //progerssBar.visibility = View.INVISIBLE
        easyNewGame.setOnClickListener {
            //progerssBar.visibility = View.VISIBLE
            //dialog?.window?.attributes?.windowAnimations  = R.drawable.slide_in
            val intent = Intent(activity, SudokuPlayActivity::class.java)
            val bundle = Bundle()
            bundle.putString("easy", "Easy")
            intent.putExtras(bundle)
            startActivity(intent)
            //progerssBar.visibility = View.INVISIBLE
        }
        val mediumNewGame = view.findViewById<CardView>(R.id.mediumNewGame)
        mediumNewGame.setOnClickListener {
            val intent = Intent(activity, SudokuPlayActivity::class.java)
            val bundle = Bundle()
            bundle.putString("medium", "Medium")
            intent.putExtras(bundle)
            startActivity(intent)
        }

        val hardNewGame = view.findViewById<CardView>(R.id.hardNewGame)
        hardNewGame.setOnClickListener {
            val intent = Intent(activity, SudokuPlayActivity::class.java)
            val bundle = Bundle()
            bundle.putString("hard", "Hard")
            intent.putExtras(bundle)
            startActivity(intent)
        }

        val expertNewGame = view.findViewById<CardView>(R.id.expertNewGame)
        expertNewGame.setOnClickListener {
            val intent = Intent(activity, SudokuPlayActivity::class.java)
            val bundle = Bundle()
            bundle.putString("expert", "Expert")
            intent.putExtras(bundle)
            startActivity(intent)
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
         * @return A new instance of fragment NewGameDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewGameDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
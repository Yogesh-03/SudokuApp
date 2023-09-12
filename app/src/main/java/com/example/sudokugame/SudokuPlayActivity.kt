package com.example.sudokugame

//import android.arch.lifecycle.ViewModelProviders
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sudokugame.databinding.ActivitySudokuPlayBinding
import com.example.sudokugame.fragments.dialog.NewGameDialogFragment
import com.example.sudokugame.sudoku.Cell
import com.example.sudokugame.sudoku.SudokuBoard
import com.example.sudokugame.sudoku.SudokuGame
import com.example.sudokugame.viewmodel.PlaySudoku
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


class SudokuPlayActivity : AppCompatActivity(), SudokuBoard.OnTouchListener {

    private lateinit var binding:ActivitySudokuPlayBinding
    //Initializing Sudoku Board
    private  lateinit var  sudokuBoard:SudokuBoard
    private lateinit var viewModel: PlaySudoku
    private lateinit var themeButton:ImageButton
    private lateinit var playActivityUndoCardView:CardView
    private  lateinit var playActivityUndoIcon:ImageButton
    private lateinit var playActivitySetting:ImageButton
    private lateinit var playActivityTimer:TextView
    private lateinit var pencilButton:ImageButton
    private lateinit var buttons:List<TextView>
    private lateinit var pencilButtonCardView:CardView
    private  lateinit var difficultyHeading:TextView

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySudokuPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sudokuBoard = findViewById(R.id.sudokuBoard)

        // Assigning Id's to view
        playActivitySetting = findViewById(R.id.playActivitySettings)
        playActivityUndoCardView = findViewById(R.id.playActivityUndoCardView)
        playActivityUndoIcon = findViewById(R.id.playActivityUndoIcon)
        themeButton = findViewById(R.id.themeButton)
        playActivityTimer = findViewById(R.id.playActivityTimer)
        pencilButton = findViewById(R.id.pencilButton)
        pencilButtonCardView = findViewById(R.id.pencilButtonCardView)
        difficultyHeading = findViewById(R.id.difficultyHeading)


        val buttonsCardView = listOf(binding.oneButtonCardView, binding.twoButtonCardView, binding.threeButtonCardView, binding.fourButtonCardView, binding.fiveButtonCardView,
            binding.sixButtonCardView, binding.sevenButtonCardView, binding.eightButtonCardView, binding.nineButtonCardView)

        buttonsCardView.forEachIndexed { index, cardView ->
           cardView.setOnClickListener {
               viewModel.sudokuGame.handleInput(index+1)
               val scale:Animation = AnimationUtils.loadAnimation(this, R.anim.cell_click_anim)
               cardView.startAnimation(scale)
           }
        }

        val adView = findViewById<AdView>(R.id.adView)
        //MobileAds.initialize(this)
        //val adRequest = AdRequest.Builder().build()
        //adView.loadAd(adRequest)

        // Getting Difficulty Level
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("easy") == "Easy"){
                difficultyHeading.text = bundle.getString("easy")
            } else if (bundle.getString("medium") == "Medium"){
                difficultyHeading.text = bundle.getString("medium")
            } else if (bundle.getString("hard") == "Hard"){
                difficultyHeading.text = bundle.getString("hard")
            } else if (bundle.getString("expert") == "Expert"){
                difficultyHeading.text = bundle.getString("expert")
            }
        }

        // Getting clicked on Undo Button
        playActivityUndoCardView.setOnClickListener {
            val scale:Animation = AnimationUtils.loadAnimation(this, R.anim.cell_click_anim)
            playActivityUndoIcon.startAnimation(scale)

        }

        val view:View = layoutInflater.inflate(R.layout.activity_theme_font_pop_up, null)
        val window = PopupWindow(this)
        window.setBackgroundDrawable(null)
        window.contentView = view
        window.isFocusable = true

        themeButton.setOnClickListener {
            //window.dismiss()
            //window.showAsDropDown(themeButton)
            window.showAsDropDown(themeButton, 20,15)

        }

        playActivitySetting.setOnClickListener {
            val intent: Intent = Intent(this, SettingsActivity().javaClass )
            startActivity(intent)
        }

        sudokuBoard.registerListener(this)
        viewModel = ViewModelProvider(this)[PlaySudoku::class.java]
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this, Observer { updateNoteTakingUI(it) })
        viewModel.sudokuGame.highlightedKeysLiveData.observe(this, Observer { updateHighlightedKeys(it) })

        pencilButton.setOnClickListener {
            viewModel.sudokuGame.changeNoteTakingState()
            if (viewModel.sudokuGame.isTakingNotesLiveData.value != true) pencilButtonCardView.setBackgroundResource(R.drawable.sudoku_board_border)
            else pencilButtonCardView.setBackgroundResource(0)
        }

        binding.erase.setOnClickListener { viewModel.sudokuGame.delete() }
    }



    private fun updateCells(cells: List<Cell>?) = cells?.let {
        sudokuBoard.updateCells(cells)
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val color = if (it) ContextCompat.getColor(this, R.color.blue) else Color.BLUE
        //pencilButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun updateHighlightedKeys(set: Set<Int>?) = set?.let {
        buttons.forEachIndexed { index, button ->
            val color = if (set.contains(index + 1)) ContextCompat.getColor(this, R.color.blue) else Color.LTGRAY
            //button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
        Log.d("onCellTouched", "triggering")
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        sudokuBoard.updateSelectedCellUI(cell.first, cell.second)
        //sudokuBoard.updateSameNumberCellUI(cell.first, cell.second)
        Log.d("updateSelectedCellUI", "triggering")
    }
}





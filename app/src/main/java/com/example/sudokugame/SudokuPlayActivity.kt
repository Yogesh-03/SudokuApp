package com.example.sudokugame

//import android.arch.lifecycle.ViewModelProviders
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.text.Layout
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActionBarContextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sudokugame.databinding.ActivitySudokuPlayBinding
import com.example.sudokugame.sharedpreferences.ThemeAndFont
import com.example.sudokugame.sudoku.Cell
import com.example.sudokugame.sudoku.SudokuBoard
import com.example.sudokugame.sudoku.SudokuGame
import com.example.sudokugame.viewmodel.PlaySudoku
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.internal.PrepareOp
import java.time.Duration


class SudokuPlayActivity : AppCompatActivity(), SudokuBoard.OnTouchListener {

    private  lateinit var  sudokuBoard:SudokuBoard
    private lateinit var oneButton: TextView
    private lateinit var twoButton: TextView
    private lateinit var threeButton: TextView
    private lateinit var fourButton: TextView
    private lateinit var fiveButton: TextView
    private lateinit var sixButton: TextView
    private lateinit var sevenButton: TextView
    private lateinit var eightButton: TextView
    private lateinit var nineButton: TextView

    private lateinit var notesButton: ImageButton
    private lateinit var deleteButton: ImageButton

    private lateinit var viewModel: PlaySudoku
    private lateinit var numberButtons: List<TextView>
    private lateinit var binding: ActivitySudokuPlayBinding
    private lateinit var oneButtonCardView: CardView
    private lateinit var  twoButtonCardView:CardView
    private lateinit var  threeButtonCardView:CardView
    private lateinit var  fourButtonCardView:CardView
    private lateinit var  fiveButtonCardView:CardView
    private lateinit var  sixButtonCardView:CardView
    private lateinit var  sevenButtonCardView:CardView
    private lateinit var  eightButtonCardView:CardView
    private lateinit var  nineButtonCardView:CardView
    private lateinit var themeButton:ImageButton

    private lateinit var playActivityUndoCardView:CardView

    private  lateinit var playActivityUndoIcon:ImageButton

    private lateinit var playActivitySetting:ImageButton
    private  var  startTime:Long = 0
    private lateinit var playActivityTimer:TextView

    private lateinit var pencilButton:ImageButton
    private lateinit var buttons:List<TextView>

    private lateinit var pencilButtonCardView:CardView

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySudokuPlayBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_sudoku_play)
        sudokuBoard = findViewById(R.id.sudokuBoard)


        // Assigning Id's to view

        oneButton = findViewById(R.id.oneButton)
        twoButton = findViewById(R.id.twoButton)
        threeButton = findViewById(R.id.threeButton)
        fourButton = findViewById(R.id.fourButton)
        fiveButton = findViewById(R.id.fiveButton)
        sixButton = findViewById(R.id.sixButton)
        sevenButton = findViewById(R.id.sevenButton)
        eightButton = findViewById(R.id.eightButton)
        nineButton = findViewById(R.id.nineButton)

        oneButtonCardView = findViewById(R.id.oneButtonCardView)
        twoButtonCardView = findViewById(R.id.twoButtonCardView)
        threeButtonCardView = findViewById(R.id.threeButtonCardView)
        fourButtonCardView = findViewById(R.id.fourButtonCardView)
        fiveButtonCardView = findViewById(R.id.fiveButtonCardView)
        sixButtonCardView = findViewById(R.id.sixButtonCardView)
        sevenButtonCardView = findViewById(R.id.sevenButtonCardView)
        eightButtonCardView = findViewById(R.id.eightButtonCardView)
        nineButtonCardView = findViewById(R.id.nineButtonCardView)

        playActivitySetting = findViewById(R.id.playActivitySettings)

        playActivityUndoCardView = findViewById(R.id.playActivityUndoCardView)
        playActivityUndoIcon = findViewById(R.id.playActivityUndoIcon)

        themeButton = findViewById(R.id.themeButton)
        playActivityTimer = findViewById(R.id.playActivityTimer)
        pencilButton = findViewById(R.id.pencilButton)

        pencilButtonCardView = findViewById(R.id.pencilButtonCardView)

         buttons = listOf(oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton, nineButton)
        val buttonsCardView = listOf(oneButtonCardView, twoButtonCardView, threeButtonCardView, fourButtonCardView, fiveButtonCardView,
            sixButtonCardView, sevenButtonCardView, eightButtonCardView, nineButtonCardView)

        buttons.forEachIndexed { index , button ->
            button.setOnClickListener{
                viewModel.sudokuGame.handleInput(index+1)
                buttonsCardView.forEachIndexed { idx, cardView ->
                    if (idx == index){
                        val scale:Animation = AnimationUtils.loadAnimation(this, R.anim.cell_click_anim)
                        cardView.startAnimation(scale)
                    }
                }
            }
        }

        buttonsCardView.forEachIndexed { index, cardView ->
            cardView.setOnClickListener {
                val scale:Animation = AnimationUtils.loadAnimation(this, R.anim.cell_click_anim)
                cardView.startAnimation(scale)
            }
        }

        val adView = findViewById<AdView>(R.id.adView)
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

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
        viewModel.sudokuGame.fontLiveSize.observe(this, Observer { updateFontSize(it) })


        pencilButton.setOnClickListener {
            viewModel.sudokuGame.changeNoteTakingState()
            if (viewModel.sudokuGame.isTakingNotesLiveData.value != true) pencilButtonCardView.setBackgroundResource(R.drawable.sudoku_board_border)
            else pencilButtonCardView.setBackgroundResource(0)
        }
    }

    private fun updateFontSize(it: Float?) {
        if (it != null) {
            sudokuBoard.updateFontSize(it)
        }
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
        Log.d("updateSelectedCellUI", "triggering")
    }


}





package com.example.sudokugame

//import android.arch.lifecycle.ViewModelProviders
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sudokugame.databinding.ActivitySudokuPlayBinding
import com.example.sudokugame.fragments.dialog.NewGameDialogFragment
import com.example.sudokugame.sharedpreferences.UserSettings
import com.example.sudokugame.sudoku.Cell
import com.example.sudokugame.sudoku.SudokuBoard
import com.example.sudokugame.sudoku.SudokuGame
import com.example.sudokugame.viewmodel.PlaySudoku
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


class SudokuPlayActivity : AppCompatActivity(), SudokuBoard.OnTouchListener {

    private lateinit var binding: ActivitySudokuPlayBinding

    //Initializing Sudoku Board
    private lateinit var sudokuBoard: SudokuBoard
    private lateinit var viewModel: PlaySudoku
    private lateinit var playActivityTimer: TextView
    private lateinit var pencilButton: ImageButton
    private lateinit var pencilButtonCardView: CardView
    private lateinit var buttonsCardView: List<CardView>
    private lateinit var remainingCount: List<TextView>
    private lateinit var mp: MediaPlayer


    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySudokuPlayBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[PlaySudoku::class.java]
        setContentView(binding.root)
        sudokuBoard = findViewById(R.id.sudokuBoard)

        // Assigning Id's to view

        playActivityTimer = findViewById(R.id.playActivityTimer)
        pencilButton = findViewById(R.id.pencilButton)
        pencilButtonCardView = findViewById(R.id.pencilButtonCardView)
        mp = MediaPlayer.create(this, R.raw.click_audio)

        buttonsCardView = listOf(
            binding.oneButtonCardView,
            binding.twoButtonCardView,
            binding.threeButtonCardView,
            binding.fourButtonCardView,
            binding.fiveButtonCardView,
            binding.sixButtonCardView,
            binding.sevenButtonCardView,
            binding.eightButtonCardView,
            binding.nineButtonCardView
        )

        remainingCount = listOf(
            binding.remainingOneTextView,
            binding.remainingTwoTextView,
            binding.remainingThreeTextView,
            binding.remainingFourTextView,
            binding.remainingFiveTextView,
            binding.remainingSixTextView,
            binding.remainingSevenTextView,
            binding.remainingEightTextView,
            binding.remainingNineTextView
        )



        buttonsCardView.forEachIndexed { index, cardView ->
            cardView.setOnClickListener {
                viewModel.sudokuGame.handleInput(index + 1)
                val scale: Animation = AnimationUtils.loadAnimation(this, R.anim.cell_click_anim)
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
            if (bundle.getString("easy") == "Easy") {
                binding.sudokuPlayMaterialToolbar.title = bundle.getString("easy")
            } else if (bundle.getString("medium") == "Medium") {
                binding.sudokuPlayMaterialToolbar.title = bundle.getString("medium")
            } else if (bundle.getString("hard") == "Hard") {
                binding.sudokuPlayMaterialToolbar.title = bundle.getString("hard")
            } else if (bundle.getString("expert") == "Expert") {
                binding.sudokuPlayMaterialToolbar.title = bundle.getString("expert")
            }
        }

        // Theme window pop up
        val view: View = layoutInflater.inflate(R.layout.activity_theme_font_pop_up, null)
        val window = PopupWindow(this)
        window.setBackgroundDrawable(null)
        window.contentView = view
        window.isFocusable = true

        //Getting clicked on App Bar menu Items
        binding.sudokuPlayMaterialToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.appBarTheme -> {
                    window.showAsDropDown(binding.sudokuPlayMaterialToolbar, 0, 0, Gravity.END)
                    true
                }

                R.id.appBarSettings -> {
                    val intent: Intent = Intent(this, SettingsActivity().javaClass)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        //Getting clicked on back button
        binding.sudokuPlayMaterialToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        sudokuBoard.registerListener(this)
        viewModel.sudokuGame.selectedCellLiveData.observe(
            this,
            Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.isTakingNotesLiveData.observe(
            this,
            Observer { updateNoteTakingUI(it) })
        viewModel.sudokuGame.highlightedKeysLiveData.observe(
            this,
            Observer { updateHighlightedKeys(it) })
        viewModel.sudokuGame.isTimerVisible.observe(this, Observer {
            if (it) {
                binding.playActivityTimer.visibility = View.VISIBLE
            } else binding.playActivityTimer.visibility = View.INVISIBLE
            Log.d("ValueTimer", it.toString())
        })

        SudokuGame.mistakes.observe(this, Observer {
            binding.mistakesTextView.text = "Mistakes: $it/3"
            if (it >= 4) {
                Toast.makeText(this, "More than 3 mistakes are made", Toast.LENGTH_SHORT).show()
            }
        })

        SudokuGame.textSize.observe(this, Observer {
            updateCells(viewModel.sudokuGame.cellsLiveData.value)
        })

        binding.pencilButtonCardView.setOnClickListener {
            viewModel.sudokuGame.changeNoteTakingState()
            if (viewModel.sudokuGame.isTakingNotesLiveData.value != true) pencilButtonCardView.setBackgroundResource(
                R.drawable.sudoku_board_border
            )
            else pencilButtonCardView.setBackgroundResource(0)

            if (getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).getBoolean(
                    UserSettings().AUDIO_EFFECT,
                    UserSettings().getCustomAudioEffect()
                )
            ) {
                mp.start()
            }
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
        buttonsCardView.forEachIndexed { index, button ->
            val color = if (set.contains(index + 1)) ContextCompat.getColor(
                this,
                R.color.blue
            ) else Color.LTGRAY
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

    override fun onResume() {
        super.onResume()
        if (getSharedPreferences(
                UserSettings().PREFERENCES,
                MODE_PRIVATE
            ).getBoolean(UserSettings().TIMER, UserSettings().getCustomTimer())
        ) {
            binding.playActivityTimer.visibility = View.VISIBLE
        } else {
            binding.playActivityTimer.visibility = View.INVISIBLE
        }

    }
}





package com.example.sudokugame.ui


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sudokugame.R
import com.example.sudokugame.database.SudokuRoomDatabase
import com.example.sudokugame.databinding.ActivitySudokuPlayBinding
import com.example.sudokugame.repository.SudokuRepository
import com.example.sudokugame.sharedpreferences.ThemePreferences
import com.example.sudokugame.sharedpreferences.UserSettings
import com.example.sudokugame.sudoku.Cell
import com.example.sudokugame.sudoku.SudokuBoard
import com.example.sudokugame.viewmodel.PlaySudoku
import com.example.sudokugame.viewmodel.SudokuDataViewModel
import com.example.sudokugame.viewmodel.SudokuDataViewModelFactory
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Objects


class SudokuPlayActivity : AppCompatActivity(), SudokuBoard.OnTouchListener {

    private lateinit var binding: ActivitySudokuPlayBinding

    private lateinit var viewModel: PlaySudoku
    private lateinit var dataViewModel: SudokuDataViewModel
    private lateinit var buttonsCardView: List<CardView>
    private lateinit var remainingCountTextViewList: List<TextView>
    private lateinit var mp: MediaPlayer
    private lateinit var dateFormat: SimpleDateFormat


    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySudokuPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PlaySudoku::class.java]
        val dao = SudokuRoomDatabase.getDatabase(application).getSudokuDao()
        val repository = SudokuRepository(dao)
        val factory = SudokuDataViewModelFactory(repository)
        dataViewModel = ViewModelProvider(this, factory)[SudokuDataViewModel::class.java]
        dateFormat = SimpleDateFormat("dd LLL yyyy", Locale.US)

        mp = MediaPlayer.create(this, R.raw.click_audio)

        buttonsCardView = listOf(
            binding.oneButtonCardView, binding.twoButtonCardView,
            binding.threeButtonCardView, binding.fourButtonCardView,
            binding.fiveButtonCardView, binding.sixButtonCardView,
            binding.sevenButtonCardView, binding.eightButtonCardView, binding.nineButtonCardView
        )

        remainingCountTextViewList = listOf(
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

        remainingCountTextViewList.forEachIndexed { index, textView ->
            textView.text = viewModel.sudokuGame.getRemainingNumberCount()[index].toString()
        }

        buttonsCardView.forEachIndexed { index, cardView ->
            if (viewModel.sudokuGame.getRemainingNumberCount()[index].toString() == "0") {
                cardView.visibility = View.INVISIBLE
                cardView.isClickable = false
            }
            cardView.setOnClickListener {
                viewModel.sudokuGame.handleInput(index + 1)
                remainingCountTextViewList.forEachIndexed { idx, textView ->
                    textView.text = viewModel.sudokuGame.getRemainingNumberCount()[idx].toString()
                    if (textView.text.toString().trim() == "0" && idx == index) {
                        cardView.visibility = View.INVISIBLE
                        cardView.isClickable = false
                    }
                }
                val scale: Animation = AnimationUtils.loadAnimation(this, R.anim.cell_click_anim)
                cardView.startAnimation(scale)
            }
        }

        val adView = findViewById<AdView>(R.id.adView)
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // Getting Difficulty Level
        val bundle = intent.extras
        setCurrentDifficulty(bundle)



        val gameOverMistakesDialog = Dialog(this)
        gameOverMistakesDialog.setContentView(R.layout.dialog_game_over_mistakes)
        gameOverMistakesDialog.setCancelable(false)

        val gameOverMistakeHomeTextView = gameOverMistakesDialog.findViewById<TextView>(R.id.gameOverMistakesHomeTextView)

        gameOverMistakeHomeTextView.setOnClickListener { openMainActivity(gameOverMistakesDialog) }

        Objects.requireNonNull<Window>(gameOverMistakesDialog.window).setBackgroundDrawable(AppCompatResources.getDrawable(this,
            R.drawable.dialog_background_inset
        ))
        gameOverMistakesDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val pauseDialog = Dialog(this)
        pauseDialog.setContentView(R.layout.dialog_pause)
        pauseDialog.setCancelable(false)

        val pauseDifficulty = pauseDialog.findViewById<TextView>(R.id.pauseDifficultyTextView)
        pauseDifficulty.text = binding.sudokuPlayMaterialToolbar.title

        val pauseMistakes = pauseDialog.findViewById<TextView>(R.id.pauseMistakesTextView)
        val resumeButton = pauseDialog.findViewById<Button>(R.id.resumeGame)

        resumeButton.setOnClickListener { pauseDialog.dismiss() }

        Objects.requireNonNull<Window>(pauseDialog.window).setBackgroundDrawable(AppCompatResources.getDrawable(this,
            R.drawable.dialog_background_inset
        ))
        pauseDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val gameCompletedDialog = Dialog(this)
        gameCompletedDialog.setContentView(R.layout.dialog_game_completed)
        gameCompletedDialog.setCancelable(false)

        val gameCompleteHomeTextView =
            gameCompletedDialog.findViewById<TextView>(R.id.gameCompletedHomeTextView)
        gameCompleteHomeTextView.setOnClickListener {
            openMainActivity(gameCompletedDialog)
        }

        Objects.requireNonNull<Window>(gameCompletedDialog.window).setBackgroundDrawable(
            AppCompatResources.getDrawable(this, R.drawable.dialog_background_inset)
        )

        gameCompletedDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )



        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUpView = inflater.inflate(R.layout.fragment_theme, null)
        val darkTheme = popUpView.findViewById<MaterialCardView>(R.id.cvDarkTheme)
        val lightTheme = popUpView.findViewById<MaterialCardView>(R.id.cvLightTheme)

        val edt:SharedPreferences.Editor = getSharedPreferences(ThemePreferences().THEME_PREF, MODE_PRIVATE).edit()

        darkTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            darkTheme.strokeWidth = 8
            darkTheme.strokeColor = ContextCompat.getColor(this, R.color.blue)
            lightTheme.strokeWidth = 0
            lightTheme.strokeColor = ContextCompat.getColor(this, R.color.blue)
            edt.putString(ThemePreferences().THEME, "dark")
            edt.apply()
            //viewModel.sudokuGame.changeTheme("dark")

        }

        lightTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            darkTheme.strokeWidth = 0
            darkTheme.strokeColor = ContextCompat.getColor(this, R.color.blue)
            lightTheme.strokeWidth = 8
            lightTheme.strokeColor = ContextCompat.getColor(this, R.color.blue)
            edt.putString(ThemePreferences().THEME, "light")
            edt.apply()
            //viewModel.sudokuGame.changeTheme("dark")
        }

        if (getSharedPreferences(ThemePreferences().THEME_PREF, MODE_PRIVATE).getString(ThemePreferences().THEME, ThemePreferences().getCurrentTheme()) == "light"){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            darkTheme.strokeWidth = 0
            darkTheme.strokeColor = ContextCompat.getColor(this, R.color.blue)
            lightTheme.strokeWidth = 8
            lightTheme.strokeColor = ContextCompat.getColor(this, R.color.blue)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            darkTheme.strokeWidth = 8
            darkTheme.strokeColor = ContextCompat.getColor(this, R.color.blue)
            lightTheme.strokeWidth = 0
            lightTheme.strokeColor = ContextCompat.getColor(this, R.color.blue)
        }

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true

        val popupWindow = PopupWindow(popUpView, width, height, focusable)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var widthScreen = displayMetrics.widthPixels
        var heightScreen = displayMetrics.heightPixels

        viewModel.sudokuGame.theme.observe(this, Observer {
            if(it == "dark"){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        })

        viewModel.sudokuGame.remainingValuesCount.observe(this) {
            //Game Completed
            if (it == 0) {
                updateGameStats()
                gameCompletedDialog.show()
            }
        }


        //Getting clicked on App Bar menu Items
        binding.sudokuPlayMaterialToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.appBarTheme -> {
                    popupWindow.showAsDropDown(binding.sudokuPlayMaterialToolbar.findViewById(R.id.appBarTheme),width,0, Gravity.START)
                    true
                }

                R.id.appBarSettings -> {
                    val intent = Intent(this, SettingsActivity().javaClass)
                    startActivity(intent)
                    true
                }

                R.id.appBarPause -> {
                    pauseMistakes.text = binding.mistakesTextView.text.subSequence(9, 11)
                    pauseDialog.show()
                    true
                }

                else -> false
            }
        }

        //Getting clicked on back button
        binding.sudokuPlayMaterialToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.sudokuBoard.registerListener(this)

        viewModel.sudokuGame.selectedCellLiveData.observe(this) { updateSelectedCellUI(it) }

        viewModel.sudokuGame.cellsLiveData.observe(this) { updateCells(it) }

        viewModel.sudokuGame.isTakingNotesLiveData.observe(this) { updateNoteTakingUI(it) }

        viewModel.sudokuGame.highlightedKeysLiveData.observe(this) { updateHighlightedKeys(it) }

        viewModel.sudokuGame.isTimerVisible.observe(this) {
            binding.playActivityTimer.visibility =  if(it) View.VISIBLE else View.INVISIBLE
        }

        viewModel.sudokuGame.mistakes.observe(this) {
            if (getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).getBoolean(UserSettings().SCREEN_TIMEOUT, UserSettings().getCustomScreenTimeout())){
                binding.mistakesTextView.text = "Mistakes: ${it}/3"
                if (it >= 3) {
                    viewModel.sudokuGame.exitGameMistakes()
                    gameOverMistakesDialog.show()
                }
            } else {
                binding.mistakesTextView.text = "Mistakes: ${it}"
            }
        }

        binding.pencilButtonCardView.setOnClickListener {
            viewModel.sudokuGame.changeNoteTakingState()
            togglePencilButtonBackground(viewModel.sudokuGame.isTakingNotesLiveData.value)
            if (getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).getBoolean(UserSettings().AUDIO_EFFECT, UserSettings().getCustomAudioEffect()))
                mp.start()

        }

        binding.erase.setOnClickListener { viewModel.sudokuGame.delete() }
    }

    private fun togglePencilButtonBackground(state:Boolean?){
        if (!state!!) {
            binding.pencilButtonCardView.setBackgroundResource(R.drawable.sudoku_board_border)
            binding.pencilButton.drawable.setTintList(ContextCompat.getColorStateList(this,
                R.color.pencil_icon_color
            ))
        } else {
            binding.pencilButtonCardView.setBackgroundResource(0)
            binding.pencilButton.drawable.setTintList(ContextCompat.getColorStateList(this,
                R.color.icons_color
            ))
        }

    }

    private fun setCurrentDifficulty(bundle: Bundle?) {
        if (bundle != null) {
            if (bundle.getString("easy") == "Easy")
                binding.sudokuPlayMaterialToolbar.title = bundle.getString("easy")
            else if (bundle.getString("medium") == "Medium")
                binding.sudokuPlayMaterialToolbar.title = bundle.getString("medium")
            else if (bundle.getString("hard") == "Hard")
                binding.sudokuPlayMaterialToolbar.title = bundle.getString("hard")
            else if (bundle.getString("expert") == "Expert")
                binding.sudokuPlayMaterialToolbar.title = bundle.getString("expert")

        }
    }

    private fun saveData(){
        dataViewModel.setInputDifficulty(binding.sudokuPlayMaterialToolbar.title.toString().trim())
        viewModel.sudokuGame.mistakes.value?.let { it1 -> dataViewModel.setInputMistakes(it1) }
        dataViewModel.setInputTime("1:15")
        dataViewModel.setInputIsCompleted(true)
        dataViewModel.setInputHintsUsed(0)
        dataViewModel.setDate(dateFormat.format(Calendar.getInstance().time))
        dataViewModel.save()
    }

    private fun updateGameStats(){
        when (binding.sudokuPlayMaterialToolbar.title.trim()) {
            "Easy" -> {
                saveData()
            }

            "Medium" -> {
                saveData()
            }

            "Hard" -> {
               saveData()
            }

            "Expert" -> {
                saveData()
            }
        }
    }


    private fun updateCells(cells: List<Cell>?) = cells?.let {
        binding.sudokuBoard.updateCells(cells)
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val color = if (it) ContextCompat.getColor(this, R.color.blue) else Color.BLUE
        //pencilButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun updateHighlightedKeys(set: Set<Int>?) = set?.let {
        buttonsCardView.forEachIndexed { index, button ->
            val color = if (set.contains(index + 1)) ContextCompat.getColor(this, R.color.blue)
            else Color.LTGRAY
            //button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    private fun openMainActivity(dialog: Dialog) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        viewModelStore.clear()
        dialog.dismiss()
        startActivity(intent)
        finish()
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        binding.sudokuBoard.updateSelectedCellUI(cell.first, cell.second)
    }

    override fun onResume() {
        super.onResume()
        if (getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).getBoolean(UserSettings().TIMER, UserSettings().getCustomTimer())) {
            binding.playActivityTimer.visibility = View.VISIBLE
        } else {
            binding.playActivityTimer.visibility = View.INVISIBLE
        }

        if (getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).getBoolean(UserSettings().HIGHLIGHT_USED_NUMBERS, UserSettings().getCustomHighlightUsedNumbers())) {
            remainingCountTextViewList.forEachIndexed { index, textView ->
                textView.visibility = View.VISIBLE
            }
        } else {
            remainingCountTextViewList.forEachIndexed { index, textView ->
                textView.visibility = View.GONE
            }
        }
    }
}





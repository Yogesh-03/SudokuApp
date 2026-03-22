package com.gamopy.sudoku.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gamopy.sudoku.R
import com.gamopy.sudoku.data.Resource
import com.gamopy.sudoku.database.SudokuRoomDatabase
import com.gamopy.sudoku.databinding.ActivitySudokuPlayBinding
import com.gamopy.sudoku.di.DaggerComponent
import com.gamopy.sudoku.model.GameState
import com.gamopy.sudoku.repository.SudokuRepository
import com.gamopy.sudoku.sharedpreferences.UserSettings
import com.gamopy.sudoku.sudoku.Cell
import com.gamopy.sudoku.sudoku.SudokuBoard
import com.gamopy.sudoku.ui.dialogs.AutoSolverDialog
import com.gamopy.sudoku.ui.fragments.NewGameDialogFragment
import com.gamopy.sudoku.viewmodel.PlaySudoku
import com.gamopy.sudoku.viewmodel.PlaySudokuViewModelFactory
import com.gamopy.sudoku.viewmodel.SudokuDataViewModel
import com.gamopy.sudoku.viewmodel.SudokuDataViewModelFactory
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Objects


@AndroidEntryPoint
class SudokuPlayActivity : AppCompatActivity(), SudokuBoard.OnTouchListener {

    private lateinit var binding: ActivitySudokuPlayBinding

    private lateinit var viewModel: PlaySudoku
    private lateinit var dataViewModel: SudokuDataViewModel
    private lateinit var buttonsCardView: List<CardView>
    private lateinit var remainingCountTextViewList: List<TextView>
    private lateinit var mp: MediaPlayer
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor
    private lateinit var autoSolverDialog : AutoSolverDialog
    private var userSettings = UserSettings()
    private var rewardedAd: RewardedAd? = null
    private var TAG = "MainActivity"

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySudokuPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val b = intent.extras
        val cc = b?.getString("continue") ?: "0"

        val fact = PlaySudokuViewModelFactory(application, cc, b!!)
        viewModel = ViewModelProvider(this, fact)[PlaySudoku::class.java]

        sharedPreferences = getSharedPreferences(userSettings.PREFERENCES, MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val dao = SudokuRoomDatabase.getDatabase(application).getSudokuDao()
        val easyDao = SudokuRoomDatabase.getDatabase(application).getEasySudokuDao()
        val mediumDao = SudokuRoomDatabase.getDatabase(application).getMediumSudokuDao()
        val hardDao = SudokuRoomDatabase.getDatabase(application).getHardSudokuDao()
        val expertDao = SudokuRoomDatabase.getDatabase(application).getExpertSudokuDao()

        val repository = SudokuRepository(dao, easyDao, mediumDao, hardDao, expertDao)
        val factory = SudokuDataViewModelFactory(repository)
        DaggerComponent.create().inject(repository)

        dataViewModel = ViewModelProvider(this, factory)[SudokuDataViewModel::class.java]

        dateFormat = SimpleDateFormat("dd LLL yyyy", Locale.getDefault())

        //Audio for clicking on pencil
        mp = MediaPlayer.create(this, R.raw.click_audio)

        buttonsCardView = listOf(
            binding.oneButtonCardView, binding.twoButtonCardView,
            binding.threeButtonCardView, binding.fourButtonCardView,
            binding.fiveButtonCardView, binding.sixButtonCardView,
            binding.sevenButtonCardView, binding.eightButtonCardView, binding.nineButtonCardView
        )

        // List of remaining number Text views
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


        //Getting clicked on Card View Numbers for Input
        buttonsCardView.forEachIndexed { index, cardView ->
            cardView.setOnClickListener {
                viewModel.sudokuGame.handleInput(index + 1)
                showAnimation(cardView)
            }
        }

        //Observing remaining count of each Number
        this.lifecycleScope.launch {
            viewModel.sudokuGame.mutableList.collect {
                Log.d("Rem Num List: ", it.toString())
                updateRemainingNumberCount(it)
            }
        }

        //Make Ad Request and show Ad
        initializeBannerAd()
        initializedRewardAds()

        // Setting Difficulty Level on App Bar Title
        setCurrentDifficulty()

        // Get the previous state of the stopwatch
        // if the activity has been
        // destroyed and recreated.
//        if (savedInstanceState != null) {
//            viewModel.sudokuGame.seconds = savedInstanceState.getInt("seconds")
//            viewModel.sudokuGame.running = savedInstanceState.getBoolean("running")
//            viewModel.sudokuGame.wasRunning = savedInstanceState.getBoolean("wasRunning")
//        }

        //Runs Timer for Sudoku
        //runTimer()
        if (!viewModel.sudokuGame.wasRunning) viewModel.sudokuGame.runTimer()

        //Dialog when more than 3 mistakes are made
        val gameOverMistakesDialog = Dialog(this)
        initializeDialog(gameOverMistakesDialog, R.layout.dialog_game_over_mistakes, false)

        val gameOverMistakeHomeTextView =
            gameOverMistakesDialog.findViewById<TextView>(R.id.gameOverMistakesHomeTextView)
        gameOverMistakeHomeTextView.setOnClickListener { openMainActivity(gameOverMistakesDialog) }

        val gameOverMistakesNewGame =
            gameOverMistakesDialog.findViewById<Button>(R.id.gameOverMistakesNewGameButton)
        val newGameDialogFragment = NewGameDialogFragment()

        gameOverMistakesNewGame.setOnClickListener {
            newGameDialogFragment.show(supportFragmentManager, newGameDialogFragment.tag)
        }

        //Dialog when clicked on pause icon
        val pauseDialog = Dialog(this)
        initializeDialog(pauseDialog, R.layout.dialog_pause, false)

        val pauseDifficulty = pauseDialog.findViewById<TextView>(R.id.pauseDifficultyTextView)
        val pauseMistakes = pauseDialog.findViewById<TextView>(R.id.pauseMistakesTextView)
        val pauseTime = pauseDialog.findViewById<TextView>(R.id.pauseTimeTextView)
        val resumeButton = pauseDialog.findViewById<Button>(R.id.resumeGame)

        pauseDifficulty.text = viewModel.sudokuGame.difficultLevel
        resumeButton.setOnClickListener {
            viewModel.sudokuGame.resumeTimer()
            pauseDialog.dismiss()
        }

        //Dialog when clicked on auto solve Dialog
        autoSolverDialog = AutoSolverDialog(this)

        autoSolverDialog.onSolveButtonClicked = {
            lifecycleScope.launch {
            viewModel.sudokuGame.autoSolverSudoku()
            }
        }

        //Dialog when game is completed, all cells are filled correctly
        val gameCompletedDialog = Dialog(this)
        initializeDialog(gameCompletedDialog, R.layout.dialog_game_completed, false)

        val gameCompleteHomeTextView =
            gameCompletedDialog.findViewById<TextView>(R.id.gameCompletedHomeTextView)
        gameCompleteHomeTextView.setOnClickListener { openMainActivity(gameCompletedDialog) }

        val gameCompletedMistakes =
            gameCompletedDialog.findViewById<TextView>(R.id.gameCompletedMistakesTextView)
        val gameCompletedDifficulty =
            gameCompletedDialog.findViewById<TextView>(R.id.gameCompletedDifficultyTextView)
        val gameCompletedTime =
            gameCompletedDialog.findViewById<TextView>(R.id.gameCompletedTimeTextView)
        val gameCompletedTextView =
            gameCompletedDialog.findViewById<TextView>(R.id.gameCompletedWinTextView)
        val gameCompletedNewGame =
            gameCompletedDialog.findViewById<Button>(R.id.gameCompletedNewGameButton)

        gameCompletedNewGame.setOnClickListener {
            newGameDialogFragment.show(supportFragmentManager, newGameDialogFragment.tag)
        }


        //Pop Up View when clicked on Theme icon to change theme of app
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUpView = inflater.inflate(R.layout.fragment_theme, null)
        val darkTheme = popUpView.findViewById<MaterialCardView>(R.id.cvDarkTheme)
        val lightTheme = popUpView.findViewById<MaterialCardView>(R.id.cvLightTheme)
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        val popupWindow = PopupWindow(popUpView, width, height, focusable)


        //Getting clicked on Dark Theme card view in theme pop up window
        //Sets dark theme
        darkTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            delegate.applyDayNight()
            applyStrokeOnDarkThemeCardView(darkTheme, lightTheme)
            editor.putBoolean(UserSettings().THEME, true)
            editor.apply()
        }

        //Getting clicked on light Theme card view in theme pop up window
        //Sets light theme
        lightTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            delegate.applyDayNight()
            applyStrokeOnLightThemeCardView(darkTheme, lightTheme)
            editor.putBoolean(UserSettings().THEME, false)
            editor.apply()
        }

        // Sets stroke according to app theme on light and dark card views
        setDefaultStrokeOnThemeCardViewPopUp(darkTheme, lightTheme)

        //Observe count of remaining values, values to be filled
        viewModel.sudokuGame.remainingValuesCount.observe(this) {
            //Game Completed
            //All cells are filled with correct value
            if (it == 0) {
                //Add data to local database
                updateGameStats(true)

                //Game won't be resumed from continue button
                editor.putBoolean(userSettings.IS_CURRENT_GAME, false).apply()
                gameCompletedMistakes.text = viewModel.sudokuGame.mistakes.value.toString()
                gameCompletedDifficulty.text = viewModel.sudokuGame.difficultLevel
                gameCompletedTime.text = viewModel.sudokuGame.time.value
                viewModel.sudokuGame.pauseTimer()
                gameCompletedTextView.text =
                    if (viewModel.sudokuGame.mistakes.value == 0) "Perfect win!"
                    else "Game completed"
                gameCompletedDialog.show()
            }
        }


        //Listen to score update to firebase
        dataViewModel.scoreUpdateResult.observe(this) {
            when (it) {
                Resource.Empty -> {
                    Toast.makeText(
                        this,
                        "Successfully uploaded score on Database",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Failure -> {
                    Toast.makeText(this, "Error uploading score on Database", Toast.LENGTH_SHORT)
                        .show()
                }

                Resource.Loading -> {

                }

                is Resource.Success -> {

                }

                null -> {

                }

            }

        }


        //Getting clicked on App Bar menu Items
        binding.sudokuPlayMaterialToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                //Clicked on Theme icon
                R.id.appBarTheme -> {
                    //show dialog_theme as dropdown
                    popupWindow.showAsDropDown(
                        binding.sudokuPlayMaterialToolbar.findViewById(R.id.appBarTheme),
                        width,
                        0,
                        Gravity.START
                    )
                    true
                }

                //Clicked on Settings icon
                R.id.appBarSettings -> {
                    val intent = Intent(this, SettingsActivity().javaClass)
                    startActivity(intent)
                    true
                }

                //clicked on pause icon
                R.id.appBarPause -> {
                    pauseMistakes.text = binding.mistakesTextView.text.subSequence(9, 11)
                    viewModel.sudokuGame.pauseTimer()
                    pauseTime.text = viewModel.sudokuGame.time.value
                    pauseDialog.show()
                    true
                }

                R.id.appBarSolver -> {
                    autoSolverDialog.show()
                    true
                }

                else -> false
            }
        }

        binding.hintsCardView.setOnClickListener {
            if (viewModel.sudokuGame.hintsRemaining.value!! > 0) viewModel.sudokuGame.handleHints()
            else {
                rewardedAd?.let { ad ->
                    ad.show(this, OnUserEarnedRewardListener { rewardItem ->
                        // Handle the reward.
                        val rewardAmount = rewardItem.amount
                        val rewardType = rewardItem.type

                        viewModel.sudokuGame.hintsRemaining.value =
                            viewModel.sudokuGame.hintsRemaining.value!!.plus(
                                rewardAmount
                            )
                        // Log.d(TAG, "User earned the reward.")
                    })
                } ?: run {
                    //Log.d(TAG, "The rewarded ad wasn't ready yet.")
                }
                initializedRewardAds()
            }
        }

        viewModel.sudokuGame.hintsRemaining.observe(this, Observer {
            if (it == 0) {
                binding.tvRemainingHints.visibility = View.GONE
                binding.ivRemainingHints.visibility = View.VISIBLE
            } else {
                binding.tvRemainingHints.visibility = View.VISIBLE
                binding.ivRemainingHints.visibility = View.GONE
            }
            binding.tvRemainingHints.text = it.toString()
        })

        //Getting clicked on back button
        binding.sudokuPlayMaterialToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.undoCardView.setOnClickListener { viewModel.sudokuGame.undo() }

        binding.sudokuBoard.registerListener(this)

        viewModel.sudokuGame.time.observe(this, Observer {
            binding.playActivityTimer.text = "Time: " + it
        })

        //Observing data of selected cell
        viewModel.sudokuGame.selectedCellLiveData.observe(this) { updateSelectedCellUI(it) }

        //Observing data of cells
        viewModel.sudokuGame.cellsLiveData.observe(this) { updateCells(it) }

        //Observing Notes taking state
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this) { updateNoteTakingUI(it) }

        viewModel.sudokuGame.highlightedKeysLiveData.observe(this) { updateHighlightedKeys(it) }

        viewModel.sudokuGame.hintCellLiveData.observe(this) {
            updateSelectedCellUI(it)
        }

        //Observing Timer for visibility when changes from settings
        viewModel.sudokuGame.isTimerVisible.observe(this) {
            binding.playActivityTimer.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }

        //Observing mistakes count
        viewModel.sudokuGame.mistakes.observe(this) {
            //Checking is mistakes limit enabled in settings
            sharedPreferences.getBoolean(
                userSettings.SCREEN_TIMEOUT,
                userSettings.getCustomScreenTimeout()
            ).run {
                binding.mistakesTextView.text = "Mistakes: ${it}"
                if (this && it >= 3) {
                    viewModel.sudokuGame.exitGameMistakes()
                    editor.putBoolean(userSettings.IS_CURRENT_GAME, false).apply()
                    updateGameStats(false)
                    gameOverMistakesDialog.show()
                }
            }

        }

        binding.pencilButtonCardView.setOnClickListener {
            if (viewModel.sudokuGame.selectedRow >= 0 && viewModel.sudokuGame.selectedCol >= 0) {
                viewModel.sudokuGame.changeNoteTakingState()
                togglePencilButtonBackground(viewModel.sudokuGame.isTakingNotesLiveData.value)

                //Checking if Audio Effects enabled in settings
                if (sharedPreferences.getBoolean(
                        userSettings.AUDIO_EFFECT,
                        userSettings.getCustomAudioEffect()
                    )
                )
                    mp.start()
            }

        }

        //Getting clicked on erase icon
        binding.erase.setOnClickListener { viewModel.sudokuGame.delete() }
    }

    /**
     * @param it
     */
    private fun updateRemainingNumberCount(it: MutableList<Int>) {
        it.forEachIndexed { index, i ->
            buttonsCardView[index].apply {
                visibility = if (i == 0) View.INVISIBLE else View.VISIBLE
                isClickable = i != 0
            }

            remainingCountTextViewList[index].text = i.toString()
        }
    }

    /**
     * Sets stroke on card view of theme pop up windows according to current theme of app
     *
     * @param darkTheme
     * @param lightTheme
     */
    private fun setDefaultStrokeOnThemeCardViewPopUp(
        darkTheme: MaterialCardView,
        lightTheme: MaterialCardView
    ) {
        sharedPreferences.getBoolean(userSettings.THEME, userSettings.getCurrentTheme())
            .let { isDarkTheme ->
                if (isDarkTheme) applyStrokeOnDarkThemeCardView(darkTheme, lightTheme)
                else applyStrokeOnLightThemeCardView(darkTheme, lightTheme)
            }
    }

    // Initialize Rewards Ad
    private fun initializedRewardAds() {
        RewardedAd.load(this, getString(R.string.reward_ad_unit_ad),
            AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Log.d(TAG, p0.toString())
                    rewardedAd = null
                }

                override fun onAdLoaded(p0: RewardedAd) {
                    super.onAdLoaded(p0)
                    Log.d(TAG, "Ad was loaded.")
                    rewardedAd = p0
                }
            })
        rewardedAdFullScreenContentCallback()
    }

    private fun rewardedAdFullScreenContentCallback() {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                Log.d(TAG, "Ad dismissed fullscreen content.")
                rewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                Log.e(TAG, "Ad failed to show fullscreen content.")
                rewardedAd = null
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }
    }

    /***
     * Initialize Banner Ad
     */

    private fun initializeBannerAd() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        Log.d("Bannder Ad" , "OK")
    }

    /**
     * @param cardView
     * Shows animation on selected cardView
     */
    private fun showAnimation(cardView: CardView) {
        val scale: Animation = AnimationUtils.loadAnimation(this, R.anim.cell_click_anim)
        cardView.startAnimation(scale)
    }

    /**
     * Apply Stroke on light theme card view and removes stroke from light theme card View Theme Pop Up Window
     *
     * @param darkTheme
     * @param lightTheme
     */
    private fun applyStrokeOnDarkThemeCardView(
        darkTheme: MaterialCardView,
        lightTheme: MaterialCardView
    ) {
        darkTheme.apply {
            strokeWidth = 8
            strokeColor = ContextCompat.getColor(this@SudokuPlayActivity, R.color.blue)
        }
        lightTheme.apply {
            strokeWidth = 0
            strokeColor = ContextCompat.getColor(this@SudokuPlayActivity, R.color.blue)
        }
    }

    /**
     * Apply Stroke on light theme card view and removes stroke from dark theme card view in Theme Pop Up Window
     *
     * @param darkTheme
     * @param lightTheme
     */
    private fun applyStrokeOnLightThemeCardView(
        darkTheme: MaterialCardView,
        lightTheme: MaterialCardView
    ) {
        with(darkTheme) {
            strokeWidth = 0
            strokeColor = ContextCompat.getColor(this@SudokuPlayActivity, R.color.blue)
        }

        with(lightTheme) {
            strokeWidth = 8
            strokeColor = ContextCompat.getColor(this@SudokuPlayActivity, R.color.blue)
        }

    }

    /**
     * sets content and cancelable to a Dialog View
     * Sets background drawable on dialog
     *
     * @param dialog
     * @param layout
     * @param isCancelable
     */
    private fun initializeDialog(dialog: Dialog, layout: Int, isCancelable: Boolean) {
        dialog.setContentView(layout)
        dialog.setCancelable(isCancelable)

        Objects.requireNonNull<Window>(dialog.window).setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.dialog_background_inset
            )
        )
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

//    override fun onSaveInstanceState(savedInstanceState: Bundle) {
//        super.onSaveInstanceState(savedInstanceState)
//
//        //Putting timer details in saved instance
//        savedInstanceState.apply {
//            putInt("seconds", viewModel.sudokuGame.seconds)
//            putBoolean("running", viewModel.sudokuGame.running)
//            putBoolean("wasRunning", viewModel.sudokuGame.wasRunning)
//        }
//
//    }

    // If the activity is paused,
    // stop the Timer.
    override fun onPause() {
        super.onPause()
        editor.putString(
            userSettings.CURRENT_GAME_MISTAKES,
            viewModel.sudokuGame.mistakes.value.toString()
        )

        viewModel.sudokuGame.timerOnPause()

        if (viewModel.sudokuGame.remainingValuesCount.value != 0) {
            saveGameState(
                GameState(
                    viewModel.sudokuGame.board.getCellList(),
                    viewModel.sudokuGame.seconds,
                    viewModel.sudokuGame.getUserActionHistory(),
                    viewModel.sudokuGame.mistakes.value!!,
                    viewModel.sudokuGame.hintsRemaining.value!!,
                    viewModel.sudokuGame.seconds,
                    viewModel.sudokuGame.running,
                    viewModel.sudokuGame.wasRunning,
                    viewModel.sudokuGame.difficultLevel
                )
            )
        }

    }

    /**
     * Use to change background of Pencil Card View
     * @param state
     */
    private fun togglePencilButtonBackground(state: Boolean?) {
        if (!state!!) {
            //Background is  Visible
            binding.pencilButtonCardView.setBackgroundResource(R.drawable.sudoku_board_border)
            binding.pencilButton.drawable.setTintList(
                ContextCompat.getColorStateList(this, R.color.pencil_icon_color)
            )
        } else {
            //Background is null
            binding.pencilButtonCardView.setBackgroundResource(0)
            binding.pencilButton.drawable.setTintList(
                ContextCompat.getColorStateList(this, R.color.icons_color)
            )
        }
    }

    /**
     * Sets selected difficulty on title of App Bar
     */
    private fun setCurrentDifficulty() {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("continue") == "continue") binding.sudokuPlayMaterialToolbar.title =
                viewModel.sudokuGame.difficultLevel
            else {
                viewModel.sudokuGame.difficultLevel = bundle.getString("easy")
                    ?: bundle.getString("medium")
                            ?: bundle.getString("hard")
                            ?: bundle.getString("expert")
                binding.sudokuPlayMaterialToolbar.title = viewModel.sudokuGame.difficultLevel
            }
        }
        //Log.d("Difficulty Level: ", viewModel.sudokuGame.difficultLevel!!)

    }

    // On Game completion, puts data into SudokuDataViewModel
    private fun saveData(completed: Boolean) {
        viewModel.sudokuGame.mistakes.value?.let { it1 -> dataViewModel.setInputMistakes(it1) }
        dataViewModel.apply {
            setInputDifficulty(binding.sudokuPlayMaterialToolbar.title.toString().trim())
            setInputTime(binding.playActivityTimer.text.substring(6, 11))
            setInputIsCompleted(completed)
            setInputHintsUsed(0)
            setDate(dateFormat.format(Calendar.getInstance().time))
            save()
        }
    }

    /**
     * @param value
     * On completion of game, the value brain / brain count will be increase by value parameter.
     */
    private fun updateBrainCount(value: Int) {
        val count = sharedPreferences.getInt(UserSettings().BRAIN, UserSettings().getCustomBrain())
        editor.putInt(UserSettings().BRAIN, count + value)
        editor.apply()

        // update score to firebase firestore
        dataViewModel.updateFirebaseBrainPoints(count + value)
    }

    private fun saveEasyData(completed: Boolean) {
        viewModel.sudokuGame.mistakes.value?.let { it1 -> dataViewModel.setInputMistakes(it1) }
        setGameData(completed)
        dataViewModel.saveEasy()
    }

    private fun saveMediumData(completed: Boolean) {
        setGameData(completed)
        dataViewModel.saveMedium()
    }

    private fun saveHardData(completed: Boolean) {
        setGameData(completed)
        dataViewModel.saveHard()
    }

    private fun saveExpertData(completed: Boolean) {
        setGameData(completed)
        dataViewModel.saveExpert()
    }

    private fun setGameData(completed: Boolean) {
        viewModel.sudokuGame.mistakes.value?.let { it1 -> dataViewModel.setInputMistakes(it1) }
        dataViewModel.apply {
            setInputDifficulty(binding.sudokuPlayMaterialToolbar.title.toString().trim())
            setInputTime(binding.playActivityTimer.text.substring(6, 11))
            setInputIsCompleted(completed)
            setInputHintsUsed(0)
            setTime(viewModel.sudokuGame.seconds)
        }
    }

    /**
     * @param completed
     * ture for game completed and false for game not completed
     * On completion of game data will be saved and barin count will be incremented acoording to the difficulty level.
     *
     * @author Yogesh
     */
    private fun updateGameStats(completed: Boolean) {
        //dataViewModel.setDate(dateFormat.format(Calendar.getInstance()))
        dataViewModel.setDate("0000")
        when (binding.sudokuPlayMaterialToolbar.title.trim()) {
            "Easy" -> {
                saveData(completed)
                saveEasyData(completed)
                updateBrainCount(1)

            }

            "Medium" -> {
                saveData(completed)
                saveMediumData(completed)
                updateBrainCount(2)

            }

            "Hard" -> {
                saveData(completed)
                saveHardData(completed)
                updateBrainCount(3)

            }

            "Expert" -> {
                saveData(completed)
                saveExpertData(completed)
                updateBrainCount(4)

            }
        }
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        binding.sudokuBoard.updateCells(cells)
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val color = if (it) ContextCompat.getColor(this, R.color.blue) else Color.BLUE
        //binding.pencilButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun updateHighlightedKeys(set: Set<Int>?) = set?.let {
        buttonsCardView.forEachIndexed { index, button ->
            val color = if (set.contains(index + 1)) ContextCompat.getColor(this, R.color.blue)
            else Color.LTGRAY
            //button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    /**
     * Opens Main Activity, clears the activity back stack and dismisses the Dialog
     * @param dialog
     */
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

    /**
     * Updates UI of selected Cell
     * @param cell      Pair of row and column of selected cell
     */
    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        binding.sudokuBoard.updateSelectedCellUI(cell.first, cell.second)
    }


    override fun onResume() {
        super.onResume()

        setTimerVisibility()
        setRemainingCountTextViewVisibility()

        viewModel.sudokuGame.timerOnResume()
    }

    /**
     * Setting visibility of remaining count number from settings
     */
    private fun setRemainingCountTextViewVisibility() {
        val visibility = if (sharedPreferences.getBoolean(
                userSettings.HIGHLIGHT_USED_NUMBERS,
                userSettings.getCustomHighlightUsedNumbers()
            )
        ) View.VISIBLE else View.GONE

        remainingCountTextViewList.forEach { textView ->
            textView.visibility = visibility
        }
    }

    /**
     * setting visibility of timer from settings
     * Only changing visibility, timer will continue running
     */
    private fun setTimerVisibility() {
        val isTimerEnabled =
            sharedPreferences.getBoolean(UserSettings().TIMER, UserSettings().getCustomTimer())
        binding.playActivityTimer.visibility = if (isTimerEnabled) View.VISIBLE else View.INVISIBLE
    }

    /**
     * @param gameState
     * @return String
     */
    private fun serializeGameState(gameState: GameState): String {
        val gson = Gson()
        return gson.toJson(gameState)
    }

    /**
     * Save the game state
     *@param gameState
     */
    private fun saveGameState(gameState: GameState) {
        val sharedPreferences = getSharedPreferences("SudokuGame", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("gameState", serializeGameState(gameState))
        editor.apply()
        //Log.d("Game State: ", "Saved")
    }
}





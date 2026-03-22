package com.gamopy.sudoku.sharedpreferences

import android.app.Application

class UserSettings : Application() {

    internal val PREFERENCES: String = "preferences"

    internal val TIMER: String = "timer"
    internal val SCREEN_TIMEOUT: String = "screenTimeout"
    internal val HIGHLIGHT_SAME_NUMBERS: String = "highlightSameNumbers"
    internal val HIGHLIGHT_USED_NUMBERS: String = "highlightUsedNumbers"
    internal val HIGHLIGHT_WRONG_INPUT: String = "highlightWrongInput"
    internal val AUDIO_EFFECT: String = "audioEffect"
    internal val DARK_SYNC: String = "darkSync"


    internal val THEME = "theme"


    internal val IS_CURRENT_GAME = "iscurrentgame"
    internal val CURRENT_GAME_DIFFICULTY = "currentgamedifficulty"
    internal val CURRENT_GAME_TIME = "currentgametime"
    internal val CURRENT_GAME_MISTAKES = "currentgamemistakes"
    internal val CURRENT_SUDOKU_LIST = "currentsudokulist"
    internal val CURRENT_SUDOKU_SOLUTION_LIST = "currentsudokusolutionlist"

    internal val BRAIN = "brain"

    private var isCurrentGame = false
    fun getCurrentGame(): Boolean {
        return isCurrentGame
    }

    fun setCurrentGame(state: Boolean) {
        this.isCurrentGame = state
    }

    private var darkTheme = false
    fun getCurrentTheme(): Boolean {
        return darkTheme
    }

    fun setCurrentTheme(theme: Boolean) {
        this.darkTheme = theme
    }

    //Getter and Setter for Timer
    private var customTimer: Boolean = true
    fun getCustomTimer(): Boolean {
        return customTimer;
    }

    fun setCustomTimer(value: Boolean) {
        customTimer = value
    }

    //Getter and Setter for Screen Timeout
    private var customScreenTimeout: Boolean = true

    //Getter
    fun getCustomScreenTimeout(): Boolean {
        return customScreenTimeout
    }

    //setter
    fun setCustomScreenTimeout(value: Boolean) {
        customScreenTimeout = value
    }

    // Getter and Setter for Highlight Same Numbers
    private var customHighlightSameNumbers: Boolean = true
    fun getCustomHighlightSameNumbers(): Boolean {
        return customHighlightSameNumbers
    }

    fun setCustomHighlightSameNumbers(value: Boolean) {
        customHighlightSameNumbers = value
    }

    //Getter and Setter for Higlight Used Numbers
    private var customHighlightUsedNumbers: Boolean = true
    fun getCustomHighlightUsedNumbers(): Boolean {
        return customHighlightUsedNumbers
    }

    fun setCustomHighlightUsedNumbers(value: Boolean) {
        customHighlightUsedNumbers = value
    }

    //Getter and Setter for Dark Sync
    private var customDarkSyncInput: Boolean = true
    fun getCustomDarkSync(): Boolean {
        return customDarkSyncInput
    }

    fun setCustomDarkSync(value: Boolean) {
        customDarkSyncInput = value
    }

    //Getter and Setter for Highlight Wrong Input
    private var customHighlightWrongInput: Boolean = true
    fun getCustomHighlightWrongInput(): Boolean {
        return customHighlightWrongInput
    }

    fun setCustomHighlightWrongInput(value: Boolean) {
        customHighlightWrongInput = value
    }

    //Getter and Setter for Audio Effect
    private var customAudioEffect: Boolean = true
    fun getCustomAudioEffect(): Boolean {
        return customAudioEffect
    }

    fun setCustomAudioEffect(value: Boolean) {
        this.customAudioEffect = value
    }

    //Getter and Setter for Brain
    private var customBrain:Int = 0

    fun getCustomBrain():Int{
        return customBrain
    }

    fun setCustomBrain(value:Int){
        this.customBrain = value
    }

}
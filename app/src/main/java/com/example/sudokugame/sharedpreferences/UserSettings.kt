package com.example.sudokugame.sharedpreferences

import android.app.Application
import kotlin.properties.Delegates

class UserSettings: Application() {
    internal val PREFERENCES:String = "preferences"
    internal val TIMER:String = "timer"
    internal val SCREEN_TIMEOUT:String = "screenTimeout"
    internal val HINTS:String = "hints"
    internal val HIGHLIGHT_SAME_NUMBERS:String = "highlightSameNumbers"
    internal val HIGHLIGHT_USED_NUMBERS:String = "highlightUsedNumbers"
    internal val HIGHLIGHT_WRONG_INPUT:String = "highlightWrongInput"
    internal val AUDIO_EFFECT:String = "audioEffect"

    //Getter and Setter for Timer
    private var customTimer:Boolean = true
     fun getCustomTimer():Boolean{
        return customTimer;
    }

    fun setCustomTimer(value:Boolean){
        customTimer = value
    }

    //Getter and Setter for Screen Timeout
    private var customScreenTimeout:Boolean = true
    //Getter
    fun getCustomScreenTimeout():Boolean{
        return customScreenTimeout
    }
    //setter
    fun setCustomScreenTimeout(value:Boolean){
        customScreenTimeout = value
    }

    //Getter and Setter for Screen Hints
    private var customHints:Boolean = true
        fun getCustomHints():Boolean {
            return customHints
        }

    fun setCustomHints(value:Boolean){
        customHints = value
    }

    // Getter and Setter for Highlight Same Numbers
    private var customHighlightSameNumbers:Boolean = true
    fun getCustomHighlightSameNumbers():Boolean{
        return customHighlightSameNumbers
    }

    fun setCustomHighlightSameNumbers(value:Boolean){
        customHighlightSameNumbers = value
    }

    //Getter and Setter for Higlight Used Numbers
    private var customHighlightUsedNumbers:Boolean = true
    fun getCustomHighlightUsedNumbers():Boolean{
        return customHighlightUsedNumbers
    }

    fun setCustomHighlightUsedNumbers(value:Boolean){
        customHighlightUsedNumbers = value
    }

    //Getter and Setter for Highlight Wrong Input
    private var customHighlightWrongInput:Boolean = true
    fun getCustomHighlightWrongInput():Boolean{
        return customHighlightWrongInput
    }

    fun setCustomHighlightWrongInput(value:Boolean){
        customHighlightWrongInput = value
    }

    //Getter and Setter for Audio Effect
    private var customAudioEffect:Boolean = true
    fun getCustomAudioEffect():Boolean{
        return customAudioEffect
    }

    fun setCustomAudioEffect(value:Boolean){
        this.customAudioEffect = value
    }
}
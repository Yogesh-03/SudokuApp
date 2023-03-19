package com.example.sudokugame.popupwindow

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sudokugame.R
import com.example.sudokugame.sharedpreferences.ThemeAndFont
import com.example.sudokugame.sharedpreferences.UserSettings
import com.example.sudokugame.sudoku.SudokuGame
import com.google.android.material.slider.Slider

class ThemeFontPopUpActivity : AppCompatActivity() {
    private lateinit var sudokuFontSize:Slider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_font_pop_up)

        sudokuFontSize = findViewById(R.id.sudokuFontSize)

        val sharedPreferences:SharedPreferences = getSharedPreferences(ThemeAndFont().themeFontPreferences, MODE_PRIVATE)
        val themeAndFont = ThemeAndFont()
        sudokuFontSize.addOnChangeListener { slider, value, fromUser ->
            val editor:SharedPreferences.Editor = getSharedPreferences(ThemeAndFont().themeFontPreferences, MODE_PRIVATE).edit()
            if(value == 16F){
                editor.putFloat(themeAndFont.smallTextPreference, themeAndFont.getCustomSmallText())
                editor.apply()
                ThemeAndFont().setCurrentTextSize(sharedPreferences.getFloat(ThemeAndFont().smallTextPreference, themeAndFont.getCustomSmallText()))
                SudokuGame().settingFontLiveSize(themeAndFont.getCurrentTextSize())
            } else if(value == 18F){
                editor.putFloat(themeAndFont.mediumTextPreference, themeAndFont.getCustomMediumText())
                editor.apply()
                ThemeAndFont().setCurrentTextSize(sharedPreferences.getFloat(ThemeAndFont().mediumTextPreference, themeAndFont.getCustomMediumText()))
                SudokuGame().settingFontLiveSize(themeAndFont.getCurrentTextSize())
            } else if(value == 20F){
                editor.putFloat(themeAndFont.largeTextPreference, themeAndFont.getCustomLargeText())
                editor.apply()
                ThemeAndFont().setCurrentTextSize(sharedPreferences.getFloat(ThemeAndFont().largeTextPreference, themeAndFont.getCustomLargeText()))
                SudokuGame().settingFontLiveSize(themeAndFont.getCurrentTextSize())
            } else if(value == 22F){
                editor.putFloat(themeAndFont.extraLargeTextPreference, themeAndFont.getCustomExtraLargeText())
                editor.apply()
                ThemeAndFont().setCurrentTextSize(sharedPreferences.getFloat(ThemeAndFont().extraLargeTextPreference, themeAndFont.getCustomExtraLargeText()))
                SudokuGame().settingFontLiveSize(themeAndFont.getCurrentTextSize())
            }


        }
    }
}

//TAF Activity on Change --> update in shared Preference and move to VM --> Sudoku Game --> Sudoku Play(get Font size from Sudoku Game), Observer
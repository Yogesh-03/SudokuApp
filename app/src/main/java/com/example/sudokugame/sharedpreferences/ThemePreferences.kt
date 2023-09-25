package com.example.sudokugame.sharedpreferences

import android.app.Application

class ThemePreferences:Application() {
    val THEME_PREF:String = "themepref"
    val THEME = "theme"


    private var currentTheme = "light"

    fun getCurrentTheme():String{
        return currentTheme
    }

    fun setCurrentTheme(theme:String){
        this.currentTheme = theme
    }



}
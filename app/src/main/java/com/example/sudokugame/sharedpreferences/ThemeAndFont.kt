package com.example.sudokugame.sharedpreferences

import android.app.Application

class ThemeAndFont:Application() {
    val themeFontPreferences:String = "themeFontPreferences"
    val smallTextPreference = "smallText"
    val mediumTextPreference = "mediumText"
    val largeTextPreference = "largePreference"
    val extraLargeTextPreference = "extraLargeTextPreference"
    val currentTextPreference = "currentTextPreference"

    private var customSmallText = 2F
    fun getCustomSmallText():Float {return customSmallText}
    fun setCustomSmallText(value:Float){customSmallText = value}

    private var customMediumText = 1.8F
    fun getCustomMediumText():Float {return customMediumText}
    fun setCustomMediumText(value:Float){customMediumText = value}

    private var customLargeText = 1.5F
    fun getCustomLargeText():Float {return customLargeText}
    fun setCustomLargeText(value:Float){customLargeText = value}

    private var customExtraLargeText = 1.3F
    fun getCustomExtraLargeText():Float{return customExtraLargeText}
    fun setCustomExtraLargeText(value:Float){customExtraLargeText = value}

    private var currentTextSize = customSmallText
    fun getCurrentTextSize():Float{return currentTextSize}
    fun setCurrentTextSize(value:Float){ currentTextSize = value}

}
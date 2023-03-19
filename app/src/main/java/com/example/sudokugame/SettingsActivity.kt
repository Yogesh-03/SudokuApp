package com.example.sudokugame

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import com.example.sudokugame.fragments.ProfileFragment
import com.example.sudokugame.sharedpreferences.UserSettings
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var settingsTimerSwitch:SwitchMaterial
    private lateinit var settingsScreenTimeoutSwitch:SwitchMaterial
    private lateinit var settingsHintsSwitch:SwitchMaterial
    private lateinit var settingsHighlightSameNumbersSwitch:SwitchMaterial
    private lateinit var settingsHighlightUsedNumbersSwitch:SwitchMaterial
    private lateinit var settingsHighlightWrongInputSwitch:SwitchMaterial
    private lateinit var settings:UserSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //Finding View's by IDs
        val backToProfile = findViewById<ImageButton>(R.id.backToProfileFromSettings)
        settingsTimerSwitch = findViewById(R.id.settingsTimerSwitch)
        settingsScreenTimeoutSwitch = findViewById(R.id.settingsScreenTimeoutSwitch)
        settingsHintsSwitch = findViewById(R.id.settingsHintsSwitch)
        settingsHighlightSameNumbersSwitch = findViewById(R.id.settingsHighlightSameNumbersSwitch)
        settingsHighlightUsedNumbersSwitch = findViewById(R.id.settingsHighlightUsedNumbersSwitch)
        settingsHighlightWrongInputSwitch = findViewById(R.id.settingsHighlightWrongInputSwitch)

        settings= UserSettings()

        //Loading Shared Preferences
        loadUserSettingsSharedPreferences()

        //Getting Clicked on Material Switches
        initSettingsSwitchListener()

        //Getting Clicked on Back Button
        backToProfile.setOnClickListener {
            val intent = Intent(this, supportFragmentManager.beginTransaction().replace(R.id.fragContainerMain, ProfileFragment()).commit()::class.java)
            startActivity(intent)
        }
    }

    private fun initSettingsSwitchListener() {

        //Getting Clicked on  Timer Switch
        settingsTimerSwitch.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked){
                settings.setCustomTimer(true)
            } else {
                settings.setCustomTimer(false)
            }
            val editor:SharedPreferences.Editor  = getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.TIMER, settings.getCustomTimer())
            editor.apply()
        }

        //Getting Clicked on Screen Timeout Switch
        settingsScreenTimeoutSwitch.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked){
                settings.setCustomScreenTimeout(true)
            } else {
                settings.setCustomScreenTimeout(false)
            }
            val editor:SharedPreferences.Editor  = getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.SCREEN_TIMEOUT, settings.getCustomScreenTimeout())
            editor.apply()
        }

        //Getting Clikced on HInts Switch
        settingsHintsSwitch.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked){
                settings.setCustomHints(true)
            } else {
                settings.setCustomHints(false)
            }
            val editor:SharedPreferences.Editor  = getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.HINTS, settings.getCustomHints())
            editor.apply()
        }

        //Getting Clicked on Highlight Same Numbers Switch
        settingsHighlightSameNumbersSwitch.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked){
                settings.setCustomHighlightSameNumbers(true)
            } else {
                settings.setCustomHighlightSameNumbers(false)
            }
            val editor:SharedPreferences.Editor  = getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.HIGHLIGHT_SAME_NUMBERS, settings.getCustomHighlightSameNumbers())
            editor.apply()
        }

        //Getting clicked on Highlight Used Numbers Switch
        settingsHighlightUsedNumbersSwitch.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked){
                settings.setCustomHighlightUsedNumbers(true)
            } else {
                settings.setCustomHighlightUsedNumbers(false)
            }
            val editor:SharedPreferences.Editor  = getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.HIGHLIGHT_USED_NUMBERS, settings.getCustomHighlightUsedNumbers())
            editor.apply()
        }

        //Getting Clicked on Highlight wrong Input Switch
        settingsHighlightWrongInputSwitch.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked){
                settings.setCustomHighlightWrongInput(true)
            } else {
                settings.setCustomHighlightWrongInput(false)
            }
            val editor:SharedPreferences.Editor  = getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.HIGHLIGHT_WRONG_INPUT, settings.getCustomHighlightWrongInput())
            editor.apply()
        }
    }

    private fun loadUserSettingsSharedPreferences() {
        val sharedPreferences:SharedPreferences = getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE)
        settings.setCustomTimer(sharedPreferences.getBoolean(settings.TIMER, settings.getCustomTimer()))
        settingsTimerSwitch.isChecked = settings.getCustomTimer()

        settings.setCustomScreenTimeout(sharedPreferences.getBoolean(settings.SCREEN_TIMEOUT,settings.getCustomScreenTimeout()))
        settingsScreenTimeoutSwitch.isChecked = settings.getCustomScreenTimeout()

        settings.setCustomHints(sharedPreferences.getBoolean(settings.HINTS, settings.getCustomHints()))
        settingsHintsSwitch.isChecked = settings.getCustomHints()

        settings.setCustomHighlightSameNumbers(sharedPreferences.getBoolean(settings.HIGHLIGHT_SAME_NUMBERS, settings.getCustomHighlightSameNumbers()))
        settingsHighlightSameNumbersSwitch.isChecked = settings.getCustomHighlightSameNumbers()

        settings.setCustomHighlightUsedNumbers(sharedPreferences.getBoolean(settings.HIGHLIGHT_USED_NUMBERS, settings.getCustomHighlightUsedNumbers()))
        settingsHighlightUsedNumbersSwitch.isChecked = settings.getCustomHighlightUsedNumbers()

        settings.setCustomHighlightWrongInput(sharedPreferences.getBoolean(settings.HIGHLIGHT_WRONG_INPUT, settings.getCustomHighlightWrongInput()))
        settingsHighlightWrongInputSwitch.isChecked = settings.getCustomHighlightWrongInput()

    }
}
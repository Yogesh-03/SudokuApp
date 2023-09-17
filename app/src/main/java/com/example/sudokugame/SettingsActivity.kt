package com.example.sudokugame


import android.app.Dialog
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.sudokugame.databinding.ActivitySettingsBinding
import com.example.sudokugame.sharedpreferences.UserSettings

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySettingsBinding
    private lateinit var settings:UserSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settings= UserSettings()

        //Loading Shared Preferences
        loadUserSettingsSharedPreferences()

        //Getting Clicked on Material Switches
        initSettingsSwitchListener()

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_feedback)
        val sendFeedback: Button = dialog.findViewById(R.id.feedbackSendButton)
        val cancelFeedback:Button = dialog.findViewById(R.id.feedbackCancelButton)

        binding.settingsFeedbackLinearLayout.setOnClickListener { dialog.show() }

        sendFeedback.setOnClickListener {
            Toast.makeText(this, "Feedback Sent", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        cancelFeedback.setOnClickListener { dialog.dismiss() }

        binding.materialToolBar.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun initSettingsSwitchListener() {


        //Getting Clicked on  Timer Switch
        binding.settingsTimerSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked){
                settings.setCustomTimer(true)
            } else {
                settings.setCustomTimer(false)
            }
            val editor:SharedPreferences.Editor  = getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.TIMER, checked)
            editor.apply()
        }

        //Getting Clicked on Screen Timeout Switch
        binding.settingsScreenTimeoutSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked){
                settings.setCustomScreenTimeout(true)
            } else {
                settings.setCustomScreenTimeout(false)
            }
            val editor:SharedPreferences.Editor  = getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.SCREEN_TIMEOUT, settings.getCustomScreenTimeout())
            editor.apply()
        }

        //Getting clicked on Audio Effect Switch
        binding.settingsScreenAudioEffectSwitch.setOnCheckedChangeListener { _, b ->
            if (b){
                settings.setCustomAudioEffect(true)
            } else {
                settings.setCustomAudioEffect(false)
            }

            val editor:SharedPreferences.Editor  = getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.AUDIO_EFFECT, settings.getCustomAudioEffect())
            editor.apply()
        }

        //Getting Clicked on HInts Switch
        binding.settingsHintsSwitch.setOnCheckedChangeListener { _, checked ->
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
        binding.settingsHighlightSameNumbersSwitch.setOnCheckedChangeListener { _, checked ->
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
        binding.settingsHighlightUsedNumbersSwitch.setOnCheckedChangeListener { _, checked ->
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
        binding.settingsHighlightWrongInputSwitch.setOnCheckedChangeListener { _, checked ->
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

        binding.settingsTimerSwitch.isChecked = sharedPreferences.getBoolean(settings.TIMER, settings.getCustomTimer())

        binding.settingsScreenTimeoutSwitch.isChecked = sharedPreferences.getBoolean(settings.SCREEN_TIMEOUT,settings.getCustomScreenTimeout())

        binding.settingsScreenAudioEffectSwitch.isChecked = sharedPreferences.getBoolean(settings.AUDIO_EFFECT, settings.getCustomAudioEffect())

        binding.settingsHintsSwitch.isChecked = sharedPreferences.getBoolean(settings.HINTS, settings.getCustomHints())

        binding.settingsHighlightSameNumbersSwitch.isChecked = sharedPreferences.getBoolean(settings.HIGHLIGHT_SAME_NUMBERS, settings.getCustomHighlightSameNumbers())

        binding.settingsHighlightUsedNumbersSwitch.isChecked = sharedPreferences.getBoolean(settings.HIGHLIGHT_USED_NUMBERS, settings.getCustomHighlightUsedNumbers())

        binding.settingsHighlightWrongInputSwitch.isChecked = sharedPreferences.getBoolean(settings.HIGHLIGHT_WRONG_INPUT, settings.getCustomHighlightWrongInput())

    }
}
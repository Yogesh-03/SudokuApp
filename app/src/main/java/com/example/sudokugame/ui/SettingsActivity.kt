package com.example.sudokugame.ui


import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.sudokugame.R
import com.example.sudokugame.databinding.ActivitySettingsBinding
import com.example.sudokugame.firebase.FirebaseSingleton
import com.example.sudokugame.sharedpreferences.UserSettings
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Objects


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settings: UserSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        setContentView(binding.root)

        settings = UserSettings()

        //Loading Shared Preferences
        loadUserSettingsSharedPreferences()

        //Getting Clicked on Material Switches
        initSettingsSwitchListener()

        val feedbackDialog = Dialog(this)
        feedbackDialog.setContentView(R.layout.dialog_feedback)
        val sendFeedback: Button = feedbackDialog.findViewById(R.id.feedbackSendButton)
        val cancelFeedback: Button = feedbackDialog.findViewById(R.id.feedbackCancelButton)
        val feedbackText: EditText = feedbackDialog.findViewById(R.id.tvFeedback)
        val pbFeedback:ProgressBar = feedbackDialog.findViewById(R.id.pbFeedback)
        pbFeedback.visibility = View.GONE
        sendFeedback.visibility = View.VISIBLE
        sendFeedback.isClickable = true
        val map: MutableMap<String, Any> = HashMap()


        Objects.requireNonNull<Window>(feedbackDialog.window).setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.dialog_background_inset
            )
        )
        feedbackDialog.window
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.settingsFeedbackLinearLayout.setOnClickListener { feedbackDialog.show() }

        binding.llAboutSettings.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
            overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
        }
        binding.llHowToPlaySettings.setOnClickListener {
            startActivity(Intent(this, HowToPlayActivity::class.java))
        }

        sendFeedback.setOnClickListener {
            pbFeedback.visibility = View.VISIBLE
            sendFeedback.visibility = View.INVISIBLE
            sendFeedback.isClickable = false
            val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            var answer: String = current.format(formatter)
            Log.d("answer", answer)

            map.put("feedback", feedbackText.text.toString())
            map.put("date", answer)
            FirebaseSingleton.getDb(applicationContext).collection("Feedback").add(map)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                    Toast.makeText(this, "Feedback Sent", Toast.LENGTH_SHORT).show()
                    feedbackDialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "There is some technical error. Try after some time.",
                        Toast.LENGTH_SHORT
                    ).show()
                    pbFeedback.visibility = View.GONE
                    sendFeedback.visibility = View.VISIBLE
                    sendFeedback.isClickable = true
                    Log.w(TAG, "Error adding document", e)
                }

        }
        cancelFeedback.setOnClickListener { feedbackDialog.dismiss() }

        binding.materialToolBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun initSettingsSwitchListener() {

        //Getting Clicked on  Timer Switch
        binding.settingsTimerSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                settings.setCustomTimer(true)
            } else {
                settings.setCustomTimer(false)
            }
            val editor: SharedPreferences.Editor =
                getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.TIMER, checked)
            editor.apply()
        }

        //Getting Clicked on Screen Timeout Switch
        binding.settingsScreenTimeoutSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                settings.setCustomScreenTimeout(true)
            } else {
                settings.setCustomScreenTimeout(false)
            }
            val editor: SharedPreferences.Editor =
                getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.SCREEN_TIMEOUT, settings.getCustomScreenTimeout())
            editor.apply()
        }

        //Getting clicked on Audio Effect Switch
        binding.settingsScreenAudioEffectSwitch.setOnCheckedChangeListener { _, b ->
            if (b) {
                settings.setCustomAudioEffect(true)
            } else {
                settings.setCustomAudioEffect(false)
            }

            val editor: SharedPreferences.Editor =
                getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.AUDIO_EFFECT, settings.getCustomAudioEffect())
            editor.apply()
        }

        //Getting Clicked on HInts Switch
        binding.settingsHintsSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                settings.setCustomHints(true)
            } else {
                settings.setCustomHints(false)
            }
            val editor: SharedPreferences.Editor =
                getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(settings.HINTS, settings.getCustomHints())
            editor.apply()
        }

        //Getting Clicked on Highlight Same Numbers Switch
        binding.settingsHighlightSameNumbersSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                settings.setCustomHighlightSameNumbers(true)
            } else {
                settings.setCustomHighlightSameNumbers(false)
            }
            val editor: SharedPreferences.Editor =
                getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(
                settings.HIGHLIGHT_SAME_NUMBERS,
                settings.getCustomHighlightSameNumbers()
            )
            editor.apply()

        }

        //Getting clicked on Highlight Used Numbers Switch
        binding.settingsHighlightUsedNumbersSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                settings.setCustomHighlightUsedNumbers(true)
            } else {
                settings.setCustomHighlightUsedNumbers(false)
            }
            val editor: SharedPreferences.Editor =
                getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(
                settings.HIGHLIGHT_USED_NUMBERS,
                settings.getCustomHighlightUsedNumbers()
            )
            editor.apply()
        }

        //Getting Clicked on Highlight wrong Input Switch
        binding.settingsHighlightWrongInputSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                settings.setCustomHighlightWrongInput(true)
            } else {
                settings.setCustomHighlightWrongInput(false)
            }
            val editor: SharedPreferences.Editor =
                getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(
                settings.HIGHLIGHT_WRONG_INPUT,
                settings.getCustomHighlightWrongInput()
            )
            editor.apply()
        }
    }

    private fun loadUserSettingsSharedPreferences() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(UserSettings().PREFERENCES, MODE_PRIVATE)

        binding.settingsTimerSwitch.isChecked =
            sharedPreferences.getBoolean(settings.TIMER, settings.getCustomTimer())

        binding.settingsScreenTimeoutSwitch.isChecked =
            sharedPreferences.getBoolean(settings.SCREEN_TIMEOUT, settings.getCustomScreenTimeout())

        binding.settingsScreenAudioEffectSwitch.isChecked =
            sharedPreferences.getBoolean(settings.AUDIO_EFFECT, settings.getCustomAudioEffect())

        binding.settingsHintsSwitch.isChecked =
            sharedPreferences.getBoolean(settings.HINTS, settings.getCustomHints())

        binding.settingsHighlightSameNumbersSwitch.isChecked = sharedPreferences.getBoolean(
            settings.HIGHLIGHT_SAME_NUMBERS,
            settings.getCustomHighlightSameNumbers()
        )

        binding.settingsHighlightUsedNumbersSwitch.isChecked = sharedPreferences.getBoolean(
            settings.HIGHLIGHT_USED_NUMBERS,
            settings.getCustomHighlightUsedNumbers()
        )

        binding.settingsHighlightWrongInputSwitch.isChecked = sharedPreferences.getBoolean(
            settings.HIGHLIGHT_WRONG_INPUT,
            settings.getCustomHighlightWrongInput()
        )

    }
}
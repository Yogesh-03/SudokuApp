package com.gamopy.sudoku.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.gamopy.sudoku.R
import com.gamopy.sudoku.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBarPrivacyPolicy.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.webViewPrivacyPolicy.apply {
            loadUrl("https://sites.google.com/view/gamopy-sudoku-privacypolicy/home")
            webViewClient = WebViewClient()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                webViewClient.onReceivedError(
                    this,
                    404,
                    "Error loading page",
                    "https://sites.google.com/view/gamopy-sudoku-privacypolicy/home"
                )
            }
        }
    }
}
package com.example.sudokugame.ui

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.example.sudokugame.R
import com.example.sudokugame.databinding.ActivityHowToPlayBinding

class HowToPlayActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHowToPlayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHowToPlayBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        setContentView(binding.root)

        binding.materialToolbarHowToPlay.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)

        val dots = arrayOfNulls<TextView>(4)
        for (i in dots.indices) {
             val index: Int = i
            dots[index] = TextView(this)
            dots[index]?.text = HtmlCompat.fromHtml("&#8226", HtmlCompat.FROM_HTML_MODE_LEGACY)
            dots[index]?.textSize = 60f
            dots[index]?.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            dots[index]?.setOnClickListener {
                binding.viewPagerHowToPlay.setCurrentItem(
                    index,
                    true
                )
            }
            binding.layoutDots.addView(dots[index], layoutParams)
        }
    }
}
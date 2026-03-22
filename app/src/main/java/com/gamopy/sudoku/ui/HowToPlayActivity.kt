package com.gamopy.sudoku.ui


import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.gamopy.sudoku.R
import com.gamopy.sudoku.adapter.HowToPlayViewPagerAdapter
import com.gamopy.sudoku.databinding.ActivityHowToPlayBinding

class HowToPlayActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHowToPlayBinding

    private val slideLayouts = intArrayOf(
        R.layout.layout_how_to_play_one, R.layout.layout_how_to_play_two,
        R.layout.layout_how_to_play_three, R.layout.layout_how_to_play_four
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        binding = ActivityHowToPlayBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        setContentView(binding.root)

        binding.materialToolbarHowToPlay.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val pagerAdapter = HowToPlayViewPagerAdapter(this, slideLayouts)
        val viewPager: ViewPager = binding.root.findViewById(R.id.viewPagerHowToPlay)
        viewPager.adapter = pagerAdapter

        addDotsIndicator(0)

        binding.cvBack.apply {
            visibility = View.INVISIBLE
            isClickable = false
        }


        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

                when (position) {
                    0 -> {
                        binding.cvBack.apply {
                            visibility = View.INVISIBLE
                            isClickable = false
                        }

                        binding.cvNext.apply {
                            visibility = View.VISIBLE
                            isClickable = true
                        }
                    }

                    3 -> {
                        binding.cvNext.apply {
                            visibility = View.INVISIBLE
                            isClickable = false
                        }

                        binding.cvBack.apply {
                            visibility = View.VISIBLE
                            isClickable = true
                        }
                    }

                    else -> {
                        binding.cvBack.apply {
                            visibility = View.VISIBLE
                            isClickable = true
                        }

                        binding.cvNext.apply {
                            visibility = View.VISIBLE
                            isClickable = true
                        }
                    }
                }

                addDotsIndicator(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.cvNext.setOnClickListener {
            if (getItem(0) < (slideLayouts.size-1)) {
                binding.viewPagerHowToPlay.setCurrentItem(getItem(1), true)
            }
        }

        binding.cvBack.setOnClickListener {
            if (getItem(0) < slideLayouts.size) {
                binding.viewPagerHowToPlay.setCurrentItem(getItem(-1), true)
            }
        }
    }

    /**
     * Adds indicators for View Pager
     * @param position   -> position of dot
     */
    private fun addDotsIndicator(position: Int) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        binding.layoutDots.removeAllViews()

        val dots = arrayOfNulls<TextView>(slideLayouts.size)
        for (i in dots.indices) {
            val index: Int = i
            dots[index] = TextView(this)
            dots[index]?.text = HtmlCompat.fromHtml("&#8226", HtmlCompat.FROM_HTML_MODE_LEGACY)
            dots[index]?.textSize = 60f
            dots[index]?.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            dots[index]?.setOnClickListener { binding.viewPagerHowToPlay.setCurrentItem(index, true) }
            binding.layoutDots.addView(dots[index], layoutParams)
        }

        if (dots.isNotEmpty()) {
            dots[position]!!.setTextColor(ContextCompat.getColor(this, R.color.blue))
        }
    }

    /**
     * @param i
     * @return Int  ith index from current index position
     */
    private fun getItem(i: Int): Int {
        return binding.viewPagerHowToPlay.currentItem + i
    }
}
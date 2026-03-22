package com.gamopy.sudoku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class HowToPlayViewPagerAdapter(context: android.content.Context, slideLayouts: IntArray) :
    PagerAdapter() {

    private var context: android.content.Context
    private var slideLayouts: IntArray

    init {
        this.context = context
        this.slideLayouts = slideLayouts
    }

    override fun getCount(): Int {
        return slideLayouts.size;
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(this.context)
        val view: View = inflater.inflate(this.slideLayouts[position], container, false)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
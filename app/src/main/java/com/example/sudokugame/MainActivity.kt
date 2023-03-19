package com.example.sudokugame

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.graphics.drawable.VectorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.example.sudokugame.fragments.HomeFragment
import com.example.sudokugame.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.jetbrains.annotations.NotNull

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationViewMain)
        val fragContainer = findViewById<FrameLayout>(R.id.fragContainerMain)

        replaceFragment(HomeFragment())
        bottomNav.selectedItemId = R.id.bottom_nav_home

        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.bottom_nav_home ->{
                    replaceFragment(HomeFragment())
                }
                R.id.bottom_nav_profile -> replaceFragment(ProfileFragment())
                else -> {

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragContainerMain, fragment)
        fragmentTransaction.commit()
    }
}


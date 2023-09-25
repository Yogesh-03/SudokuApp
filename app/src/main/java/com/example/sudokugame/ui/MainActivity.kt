package com.example.sudokugame.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sudokugame.R
import com.example.sudokugame.databinding.ActivityMainBinding
import com.example.sudokugame.ui.fragments.HomeFragment
import com.example.sudokugame.ui.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeFragment(HomeFragment(), HomeFragment::class.simpleName)

        binding.bottomNavigationViewMain.selectedItemId = R.id.bottom_nav_home

        binding.bottomNavigationViewMain.setOnItemSelectedListener {
            when(it.itemId){
                R.id.bottom_nav_home -> changeFragment(HomeFragment(), HomeFragment::class.simpleName)

                R.id.bottom_nav_profile -> changeFragment(ProfileFragment(), ProfileFragment::class.simpleName)

            }
           true
        }
    }

    private fun changeFragment(fragment: Fragment?, tagFragmentName: String?) {
        val mFragmentManager = supportFragmentManager
        val fragmentTransaction = mFragmentManager.beginTransaction()
        val currentFragment = mFragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }
        var fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName)
        if (fragmentTemp == null) {
            fragmentTemp = fragment
            fragmentTransaction.add(
                R.id.fragContainerMain,
                fragmentTemp!!,
                tagFragmentName
            ).addToBackStack(null)
        } else {
            fragmentTransaction.show(fragmentTemp)
        }
        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commit()
    }
}


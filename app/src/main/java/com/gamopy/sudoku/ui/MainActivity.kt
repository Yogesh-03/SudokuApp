package com.gamopy.sudoku.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.gamopy.sudoku.R
import com.gamopy.sudoku.databinding.ActivityMainBinding
import com.gamopy.sudoku.sharedpreferences.UserSettings
import com.gamopy.sudoku.ui.fragments.HomeFragment
import com.gamopy.sudoku.ui.fragments.ProfileFragment
import com.gamopy.sudoku.ui.fragments.StatsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentFragment = 0

    //private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(this)
//    private val listener: InstallStateUpdatedListener = InstallStateUpdatedListener { installState ->
//        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
//            // After the update is downloaded, show a notification
//            // and request user confirmation to restart the app.
//            Log.d(TAG, "An update has been downloaded")
//            //showSnackBarForCompleteUpdate()
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        //
        getSharedPreferences(
            UserSettings().PREFERENCES,
            MODE_PRIVATE
        ).getBoolean(UserSettings().THEME, UserSettings().getCurrentTheme()).let {
            if (!it) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        // appUpdateManager.registerListener(listener)

//        appUpdateManager.startUpdateFlowForResult(
//            // Pass the intent that is returned by 'getAppUpdateInfo()'.
//            appUpdateInfo,
//            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
//            AppUpdateType.FLEXIBLE,
//            // The current activity making the update request.
//            this,
//            // Include a request code to later monitor this update request.
//            501)

        setContentView(binding.root)

        // Set "Home Fragment" as default fragment to be shown.
        changeFragment(HomeFragment(), HomeFragment::class.simpleName)
        binding.bottomNavigationViewMain.selectedItemId = R.id.bottom_nav_home

        binding.bottomNavigationViewMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_nav_home -> {
                    //appUpdateManager.unregisterListener(listener)
                    currentFragment = 0
                    changeFragment(
                        HomeFragment(), HomeFragment::class.simpleName
                    )
                }

                R.id.bottom_nav_profile -> {
                    //appUpdateManager.unregisterListener(listener)
                    currentFragment = 1
                    changeFragment(ProfileFragment(), ProfileFragment::class.simpleName)
                }

                R.id.bottom_nav_stats -> {
                    currentFragment = 2
                    changeFragment(StatsFragment(), StatsFragment::class.simpleName)
                }

            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentFragment == 1) binding.bottomNavigationViewMain.selectedItemId =
            R.id.bottom_nav_profile
        else if (currentFragment == 0) binding.bottomNavigationViewMain.selectedItemId =
            R.id.bottom_nav_home
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (currentFragment == 1) {
            super.onBackPressed()
            binding.bottomNavigationViewMain.selectedItemId = R.id.bottom_nav_home
            currentFragment = 0
        } else if (currentFragment == 0) finish()
    }

//    private fun checkUpdate() {
//        // Returns an intent object that you use to check for an update.
//        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
//        // Checks that the platform will allow the specified type of update.
//        Log.d(TAG, "Checking for updates")
//        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
//                // Request the update.
//                Log.d(TAG, "Update available")
//            } else {
//                Log.d(TAG, "No Update available")
//            }
//        }
//    }

    private fun changeFragment(fragment: Fragment?, tagFragmentName: String?) {
        val mFragmentManager = supportFragmentManager
        val fragmentTransaction = mFragmentManager.beginTransaction()
        val currentFragment = mFragmentManager.primaryNavigationFragment
        if (currentFragment != null) fragmentTransaction.hide(currentFragment)

        var fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName)
        if (fragmentTemp == null) {
            fragmentTemp = fragment
            fragmentTransaction.add(
                R.id.fragContainerMain,
                fragmentTemp!!,
                tagFragmentName
            ).addToBackStack(null)
        } else fragmentTransaction.show(fragmentTemp)

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commit()
    }
}


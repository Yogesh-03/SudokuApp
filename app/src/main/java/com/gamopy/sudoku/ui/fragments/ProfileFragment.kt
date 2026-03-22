package com.gamopy.sudoku.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gamopy.sudoku.R
import com.gamopy.sudoku.ui.SettingsActivity
import com.gamopy.sudoku.adapter.MyRecyclerViewAdapter
import com.gamopy.sudoku.database.SudokuRoomDatabase
import com.gamopy.sudoku.databinding.FragmentProfileBinding
import com.gamopy.sudoku.repository.SudokuRepository
import com.gamopy.sudoku.ui.ProfileActivity
import com.gamopy.sudoku.viewmodel.SudokuDataViewModel
import com.gamopy.sudoku.viewmodel.SudokuDataViewModelFactory
import com.google.android.material.tabs.TabLayout

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var dataViewModel: SudokuDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        val dao = SudokuRoomDatabase.getDatabase(requireActivity().application).getSudokuDao()
        val easyDao =
            SudokuRoomDatabase.getDatabase(requireActivity().application).getEasySudokuDao()
        val mediumDao =
            SudokuRoomDatabase.getDatabase(requireActivity().application).getMediumSudokuDao()
        val hardDao =
            SudokuRoomDatabase.getDatabase(requireActivity().application).getHardSudokuDao()
        val expertDao =
            SudokuRoomDatabase.getDatabase(requireActivity().application).getExpertSudokuDao()
        val repository = SudokuRepository(dao, easyDao, mediumDao, hardDao, expertDao)
        val factory = SudokuDataViewModelFactory(repository)
        dataViewModel = ViewModelProvider(this, factory)[SudokuDataViewModel::class.java]

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        displayEastStats()
                    }

                    1 -> {
                        displayMediumStats()
                    }

                    2 -> {
                        displayHardStats()
                    }

                    3 -> {
                        displayExpertStats()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })



        binding.profileMaterialToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profileSettingItem -> {
                    val intent = Intent(requireActivity(), SettingsActivity().javaClass)
                    startActivity(intent)
                    true
                }

                R.id.profileIcon -> {
                    val intent = Intent(requireActivity(), ProfileActivity().javaClass)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        initRecView()

        return binding.root
    }

    private fun initRecView() {
        binding.recView.layoutManager = LinearLayoutManager(requireContext())
        displayEastStats()
    }

    private fun displayEasyAllData() {
        dataViewModel.easyAllData.observe(this) {
            binding.recView.adapter = MyRecyclerViewAdapter(it, dataViewModel)
        }
    }

    private fun displayMediumAllData() {
        dataViewModel.mediumAllData.observe(this) {
            binding.recView.adapter = MyRecyclerViewAdapter(it, dataViewModel)
        }
    }

    private fun displayHardAllData() {
        dataViewModel.hardAllData.observe(this) {
            binding.recView.adapter = MyRecyclerViewAdapter(it, dataViewModel)
        }
    }

    private fun displayExpertAllData() {
        dataViewModel.expertAllData.observe(this) {
            binding.recView.adapter = MyRecyclerViewAdapter(it, dataViewModel)
        }
    }

    private fun displayEastStats() {
        dataViewModel.easyTotalWins.observe(this) {
            binding.tvTotalWins.text = it.toString()
        }

        dataViewModel.easyPerfectWins.observe(this) {
            binding.tvPerfectWins.text = it.toString()
        }
        dataViewModel.easyGamesPlayed.observe(this) {
            binding.tvGamesPlayed.text = it.toString()
        }
        dataViewModel.easyBestTime.observe(this) {
            setTime(it)
        }
        displayEasyAllData()
    }


    private fun displayMediumStats() {
        dataViewModel.mediumTotalWins.observe(this) {
            binding.tvTotalWins.text = it.toString()
        }
        dataViewModel.mediumPerfectWins.observe(this) {
            binding.tvPerfectWins.text = it.toString()
        }
        dataViewModel.mediumGamesPlayed.observe(this) {
            binding.tvGamesPlayed.text = it.toString()
        }
        dataViewModel.mediumBestTime.observe(this) {
            setTime(it)
        }
        displayMediumAllData()
    }

    private fun setTime(it: Int?) {
        val minutes = (it?.rem(3600))?.div(60) ?: 0
        val secs = it?.rem(60) ?: 0
        binding.tvBestTime.text = if (it == null) "--" else {
            if (minutes == 0) (secs - 1).toString() + "S"
            else minutes.toString() + "M:" + (secs - 1).toString() + "S"
        }
    }

    private fun displayHardStats() {
        dataViewModel.hardTotalWins.observe(this) {
            binding.tvTotalWins.text = it.toString()
        }
        dataViewModel.hardPerfectWins.observe(this) {
            binding.tvPerfectWins.text = it.toString()
        }
        dataViewModel.hardGamesPlayed.observe(this) {
            binding.tvGamesPlayed.text = it.toString()
        }
        dataViewModel.hardBestTime.observe(this) {
            setTime(it)
        }
        displayHardAllData()
    }

    private fun displayExpertStats() {
        dataViewModel.expertTotalWins.observe(this) {
            binding.tvTotalWins.text = it.toString()
        }
        dataViewModel.expertPerfectWins.observe(this) {
            binding.tvPerfectWins.text = it.toString()
        }
        dataViewModel.expertGamesPlayed.observe(this) {
            binding.tvGamesPlayed.text = it.toString()
        }
        dataViewModel.expertBestTime.observe(this) {
            setTime(it)
        }
        displayExpertAllData()
    }
}



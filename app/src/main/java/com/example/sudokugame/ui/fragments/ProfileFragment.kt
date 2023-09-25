package com.example.sudokugame.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sudokugame.R
import com.example.sudokugame.ui.SettingsActivity
import com.example.sudokugame.adapter.MyRecyclerViewAdapter
import com.example.sudokugame.database.SudokuRoomDatabase
import com.example.sudokugame.databinding.FragmentProfileBinding
import com.example.sudokugame.repository.SudokuRepository
import com.example.sudokugame.viewmodel.SudokuDataViewModel
import com.example.sudokugame.viewmodel.SudokuDataViewModelFactory
import com.google.android.material.tabs.TabLayout

class ProfileFragment : Fragment() {
    private lateinit var binding:FragmentProfileBinding

    private lateinit var dataViewModel: SudokuDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        val dao = SudokuRoomDatabase.getDatabase(requireActivity().application).getSudokuDao()
        val repository = SudokuRepository(dao)
        val factory = SudokuDataViewModelFactory(repository)
        dataViewModel = ViewModelProvider(this, factory)[SudokuDataViewModel::class.java]

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
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
                when(tab?.position){

                }

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })



        binding.profileMaterialToolbar.setOnMenuItemClickListener { menuItem->
            when(menuItem.itemId){
                R.id.profileSettingItem -> {
                    val intent = Intent(requireActivity(), SettingsActivity().javaClass)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        initRecView()

        return binding.root
    }

    private fun initRecView(){
        binding.recView.layoutManager = LinearLayoutManager(requireContext())
        displayEastStats()
    }

    private fun displayEasyAllData(){
        dataViewModel.easyAllData.observe(this){
            binding.recView.adapter = MyRecyclerViewAdapter(it, dataViewModel)
        }
    }
    private fun displayMediumAllData(){
        dataViewModel.mediumAllData.observe(this){
            binding.recView.adapter = MyRecyclerViewAdapter(it, dataViewModel)
        }
    }

    private fun displayHardAllData(){
        dataViewModel.hardAllData.observe(this){
            binding.recView.adapter = MyRecyclerViewAdapter(it, dataViewModel)
        }
    }

    private fun displayExpertAllData(){
        dataViewModel.expertAllData.observe(this){
            binding.recView.adapter = MyRecyclerViewAdapter(it, dataViewModel)
        }
    }

    private fun displayEastStats(){
        dataViewModel.easyTotalWins.observe(this){
            binding.tvTotalWins.text = it.toString()
        }

        dataViewModel.easyPerfectWins.observe(this){
            binding.tvPerfectWins.text = it.toString()
        }
        dataViewModel.easyGamesPlayed.observe(this){
            binding.tvGamesPlayed.text = it.toString()
        }
        displayEasyAllData()
    }


    private fun displayMediumStats(){
        dataViewModel.mediumTotalWins.observe(this){
            binding.tvTotalWins.text = it.toString()
        }
        dataViewModel.mediumPerfectWins.observe(this){
            binding.tvPerfectWins.text = it.toString()
        }
        dataViewModel.mediumGamesPlayed.observe(this){
            binding.tvGamesPlayed.text = it.toString()
        }
        displayMediumAllData()
    }

    private fun displayHardStats(){
        dataViewModel.hardTotalWins.observe(this){
            binding.tvTotalWins.text = it.toString()
        }
        dataViewModel.hardPerfectWins.observe(this){
            binding.tvPerfectWins.text = it.toString()
        }
        dataViewModel.hardGamesPlayed.observe(this){
            binding.tvGamesPlayed.text = it.toString()
        }
        displayHardAllData()
    }

    private fun displayExpertStats(){
        dataViewModel.expertTotalWins.observe(this){
            binding.tvTotalWins.text = it.toString()
        }
        dataViewModel.expertPerfectWins.observe(this){
            binding.tvPerfectWins.text = it.toString()
        }
        dataViewModel.expertGamesPlayed.observe(this){
            binding.tvGamesPlayed.text = it.toString()
        }
        displayExpertAllData()
    }

}



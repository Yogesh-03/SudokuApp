package com.example.sudokugame.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sudokugame.R
import com.example.sudokugame.SettingsActivity
import com.example.sudokugame.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding:FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        binding.profileMaterialToolbar.setOnMenuItemClickListener { menuItem->
            when(menuItem.itemId){
                R.id.profileSettingItem -> {
                    val intent: Intent = Intent(requireActivity(), SettingsActivity().javaClass)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }




        return binding.root
    }

}
package com.example.quickriddles

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.quickriddles.databinding.FragmentAboutBinding

/**
 * A simple [Fragment] subclass.
 */
class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentAboutBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_about, container, false)

        binding.backButton.setOnClickListener {

            activity?.onBackPressed()
        }
        return binding.root
    }

}

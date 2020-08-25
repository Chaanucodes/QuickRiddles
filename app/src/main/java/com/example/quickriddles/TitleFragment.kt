package com.example.quickriddles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.quickriddles.databinding.FragmentTitleBinding

/**
 * A simple [Fragment] subclass.
 */
class TitleFragment : Fragment() {

    lateinit var binding : FragmentTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_title, container, false)

        binding.aboutButton.setOnClickListener { v : View->
            v.findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToAboutFragment())
        }

        binding.exitButton.setOnClickListener {
            activity?.finish()
        }

        binding.rulesButton.setOnClickListener { v ->
            v.findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToRulesFragment())
        }

        binding.playButton.setOnClickListener { v ->
            v.findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToGameFragment())
        }
        return binding.root
    }


}

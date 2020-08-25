package com.example.quickriddles.game

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.method.KeyListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment

import com.example.quickriddles.R
import com.example.quickriddles.databinding.FragmentGameBinding
import kotlinx.android.synthetic.main.fragment_game.*

/**
 * A simple [Fragment] subclass.
 */
class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    private lateinit var mKeyListener : KeyListener

    private lateinit var dialog : DialogGameFragment


    private lateinit var mgr: InputMethodManager
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentGameBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)

        dialog = DialogGameFragment()


        binding.gameViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        mKeyListener = binding.editText.keyListener

        binding.backButton.setOnClickListener {
           dialog.show(childFragmentManager, "THIS IS TAG")
            mgr= activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.hideSoftInputFromWindow(binding.editText.windowToken, 0)
        }

        binding.editText.doOnTextChanged { text, start, count, after ->
            viewModel.onCheckAnswer(text!!.trim().toString())
        }

        viewModel.imageViewVisibility.observe(viewLifecycleOwner, Observer { i->

            if(i == View.VISIBLE){
                mKeyListener = binding.editText.keyListener
                binding.editText.apply {
                    keyListener = null
                    setText("${viewModel.riddle.value?.answer} : Correct response")
                    setTextColor(resources.getColor(R.color.colorAccent))
                }
                mgr= activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                mgr.hideSoftInputFromWindow(binding.editText.windowToken, 0)
                binding.skipButton.isEnabled = false
            }else{
                activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

                binding.editText.apply {
                    keyListener = mKeyListener
                    setTextColor(resources.getColor(R.color.white))
                    setText("")
                }
                mgr = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                mgr.showSoftInput(binding.editText, 0)
                binding.skipButton.isEnabled = true
            }
        })


        viewModel.freezeButton.observe(viewLifecycleOwner, Observer { b ->
            binding.skipButton.isEnabled = !b
        })

        viewModel.currentHint.observe(viewLifecycleOwner, Observer { s->
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).apply {
                setGravity(0,0,0)
                show()
            }
        })

        viewModel.hintsLeft.observe(viewLifecycleOwner, Observer { n ->
                if(n<1){binding.hintImage.visibility = View.INVISIBLE}
        })

        viewModel.finishEvent.observe(viewLifecycleOwner, Observer { hasFinished ->
            if (hasFinished) onGameFinished(viewModel.score.value)
        })
        return binding.root
    }

    private fun onGameFinished(value: Int?) {
        Toast.makeText(activity, "Game has just finished", Toast.LENGTH_SHORT).show()
        NavHostFragment.findNavController(this).navigate(GameFragmentDirections.actionGameFragmentToCongratulationsFragment(value!!))
    }

}

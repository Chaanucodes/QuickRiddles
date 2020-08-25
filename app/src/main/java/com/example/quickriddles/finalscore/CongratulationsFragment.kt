package com.example.quickriddles.finalscore

import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.quickriddles.R
import com.example.quickriddles.databinding.FragmentCongratulationsBinding
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.fragment_congratulations.*


/**
 * A simple [Fragment] subclass.
 */
class CongratulationsFragment : Fragment() {

    private lateinit var binding: FragmentCongratulationsBinding
    private lateinit var mMediaPlayer : MediaPlayer
    private var currentVideoPosition : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_congratulations , container, false)

        val args = CongratulationsFragmentArgs.fromBundle(requireArguments())

        val anima =  AnimationUtils.loadAnimation(activity, R.anim.bottom_animation)
        binding.finalScore.startAnimation(anima)

        binding.finalScore.text = when(args.score){
            -10 -> "${args.score}!!! Am I a joke to you?"
            in(-9..0) -> "Not gonna lie. A score of ${args.score} is not good."
            in(1..2) -> "With a little practice, you can do better than scoring ${args.score}"
            in(3..7) -> "Cool. You scored ${args.score}. Not bad at all"
            in (8..9) -> "Woah. Very nice! You scored ${args.score}"

            else -> "Absolutely magnificent!!! A perfect ${args.score}"
        }

        val uri = Uri.parse("android.resource://${activity?.packageName}/${R.raw.vidback1}")
        binding.videoView.setVideoURI(uri)
        binding.videoView.start()

        binding.backButtonCongrats.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.videoView.setOnPreparedListener{ mediaPlayer ->
            mMediaPlayer = mediaPlayer
            // We want our video to play over and over so we set looping to true.
            mMediaPlayer.isLooping = true
            // We then seek to the current position if it has been set and play the video.
            if (currentVideoPosition != 0) {
                mMediaPlayer.seekTo(currentVideoPosition)
                mMediaPlayer.start()
            }
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        currentVideoPosition = mMediaPlayer.getCurrentPosition();
        binding.videoView.pause();
    }

    override fun onResume() {
        super.onResume()
        binding.videoView.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.release()
    }

}

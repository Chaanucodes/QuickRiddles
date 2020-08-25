package com.example.quickriddles.game


import android.os.CountDownTimer
import android.os.Handler
import android.text.format.DateUtils
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.quickriddles.R


class GameViewModel : ViewModel() {

    companion object {

        // Time when the game is over
        private const val DONE = 0L

        // Countdown time interval
        private const val ONE_SECOND = 1000L

        // Total time for the game
        private const val COUNTDOWN_TIME = 31000L

    }

    private val _currentTime = MutableLiveData<Long>()
    val currentTime : LiveData<Long>
        get() = _currentTime

    private lateinit var timer : CountDownTimer

    val currentTimeString = Transformations.map(currentTime){ time->
        DateUtils.formatElapsedTime(time)
    }

    private var _riddle = MutableLiveData<Riddles>()
    val riddle: LiveData<Riddles>
        get() = _riddle

    private var _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private var _finishEvent = MutableLiveData<Boolean>()
    val finishEvent: LiveData<Boolean>
        get() = _finishEvent

    private var _image = MutableLiveData<Int>()
    val image: LiveData<Int>
        get() = _image

    private var _textViewVisibility = MutableLiveData<Int>()
    val textViewVisibility: LiveData<Int>
        get() = _textViewVisibility

    private var _imageViewVisibility = MutableLiveData<Int>()
    val imageViewVisibility: LiveData<Int>
        get() = _imageViewVisibility

    private var _freezeButton = MutableLiveData<Boolean>()
    val freezeButton: LiveData<Boolean>
        get() = _freezeButton

    private var _currentHint = MutableLiveData<String>()
    val currentHint: LiveData<String>
        get() = _currentHint

    private var _hintsLeft = MutableLiveData<Int>()
    val hintsLeft: LiveData<Int>
        get() = _hintsLeft

    private var _bulbColor = MutableLiveData<Int>()
    val bulbColor: LiveData<Int>
        get() = _bulbColor

    private var hintUsers = mutableListOf<Riddles>()

    lateinit var riddlesList: MutableList<Riddles>

    init {
        resetList()
        nextRiddle()
        _imageViewVisibility.value = View.GONE
        _textViewVisibility.value = View.VISIBLE
        _score.value = 0
        _hintsLeft.value = 3
        _bulbColor.value = R.drawable.ic_hint_bulb
    }

    private fun nextRiddle() {
        if (!riddlesList.isEmpty()) {
            _riddle.value = riddlesList.removeAt(0)
            if(_textViewVisibility.value != View.VISIBLE){
                _textViewVisibility.value = View.VISIBLE
            }
            if(_imageViewVisibility.value !=View.GONE){
                _imageViewVisibility.value = View.GONE
            }
            startTimer()
        } else onGameFinish()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished/ ONE_SECOND
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _image.value = _riddle.value?.picture
                if(_textViewVisibility.value != View.GONE){
                    _textViewVisibility.value = View.GONE
                }
                if(_imageViewVisibility.value != View.VISIBLE){
                    _imageViewVisibility.value = View.VISIBLE
                }
                Handler().postDelayed({
                    onSkipAnswer()
                }, 2000)
            }
        }

        timer.start()
    }

    fun onCheckAnswer(answer: String): Boolean {
        return if (answer.equals(_riddle.value?.answer, true)) {
            if(_textViewVisibility.value != View.GONE){
                _textViewVisibility.value = View.GONE
            }
            if(_imageViewVisibility.value != View.VISIBLE){
                _imageViewVisibility.value = View.VISIBLE
            }
            _image.value = _riddle.value?.picture
            timer.cancel()
            Handler().postDelayed({
                nextRiddle()
            }, 2000)
            _score.value = (score.value)?.plus(1)
            true
        } else false
    }

    //to create and shuffle the list
    private fun resetList() {
        riddlesList = mutableListOf(
            Riddles("I am here, I am there, I rhyme with air", "Air", R.drawable.air),
            Riddles("If there isn't a single thing present somewhere, I will be there", "nothing", R.drawable.nothing),
            Riddles(
                "I was probably built for getting some extra space but I am mostly used for kidnapping",
                "van",
                R.drawable.van
            ),
            Riddles(
                "I have branches yet I have no leaves, no trunk and no fruit. What am I?",
                "bank",
                R.drawable.bank
            ),
            Riddles("If it weren't for me, we wouldn't have any questions", "curiosity", R.drawable.curiosity),
            Riddles(
                "I am something you have consumed at various points in your life and I am also a device",
                "tablet",
                R.drawable.tablet
            ),
            Riddles("Just a 3 letter word still I make people fail, and sometimes succeed", "try", R.drawable.trying),
            Riddles("What water can you eat and chew?", "Watermelon", R.drawable.watermelon),
            Riddles(
                "I can be liquid or solid, sometimes I bubble and you can find me in every home. What am I? ",
                "soap",
                R.drawable.soap
            ),
            Riddles("You answer me, although I never ask you questions", "phone", R.drawable.phone)
        )

        riddlesList.shuffle()
    }

    private fun onGameFinish() {
        _finishEvent.value = true
    }

    fun onSkipAnswer() {
        _score.value = (score.value)?.minus(1)
        _freezeButton.value = true
        timer.cancel()

        Handler().postDelayed({
            _freezeButton.value = false
            nextRiddle()
        }, 300)
    }

    fun onGetHint() {
        if (_hintsLeft.value != 0) {
            hintUsers.add(_riddle.value!!)
            _hintsLeft.value =_hintsLeft.value!!.minus(1)

            when (hintUsers.size) {
                1 -> {_currentHint.value = getLengthValue()
                    _bulbColor.value = R.drawable.ic_hint_bulb_orange}
                2 -> {
                    if (hintUsers[0] == hintUsers[1]){
                        _currentHint.value = "First letter : ${_riddle.value!!.answer.first()}"
                    } else {
                        _currentHint.value = getLengthValue()
                    }
                    _bulbColor.value = R.drawable.ic_hint_bulb_red
                }
                3 -> {
                    when {
                        hintUsers[0] == hintUsers[2] -> {
                            _currentHint.value = "Last letter : ${_riddle.value!!.answer.last()}"
                        }
                        hintUsers[1] == hintUsers[2] -> {
                            _currentHint.value = "First letter : ${_riddle.value!!.answer.first()}"
                        }
                        else -> {
                            _currentHint.value = getLengthValue()
                        }
                    }
                }
                else -> _currentHint.value = "Exception!"
            }
        }
    }

    private fun getLengthValue() = "Total letters : ${_riddle.value!!.answer.length}"

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }
}




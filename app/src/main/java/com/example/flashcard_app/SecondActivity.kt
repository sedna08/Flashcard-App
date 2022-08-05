package com.example.flashcard_app

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.*
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AlphaAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.flashcard_app.databinding.ActivitySecondBinding
import com.example.flashcard_app.databinding.AlertCompleteSetBinding
import kotlin.math.hypot


class SecondActivity : AppCompatActivity() {

    private lateinit var timer : CountDownTimer
    private var currentColor = 1

    companion object {
        lateinit var flashcardDBHelper : FlashcardDBHelper
        var questionList = ArrayList<String>()
        var answerList = ArrayList<String>()
        lateinit var numOfCards: String
        lateinit var tableName: String
        lateinit var binding: ActivitySecondBinding
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing database variable
        flashcardDBHelper = FlashcardDBHelper(this)

        // initialize timer
        timer = object : CountDownTimer(0,0){
            override fun onTick(p0: Long) { }
            override fun onFinish() { }
        }

        var setCardView = 0
        var score = 0
        var currentCard = 0
        var scoreStatus = 0
        var setSound = 0
        var done = false

        val bundle: Bundle? = intent.extras
        val playMode = bundle!!.getString("user_mode")
        val name =  bundle!!.getString("tableName")
        val buttonClick = AlphaAnimation(1f, 0.5f)

        tableName = name.toString()
        getContents(binding)

        // code to display all Information for 1st set
        binding.tvCardInstruction.text = questionList.get(currentCard)
        binding.tvAnswer.text = answerList.get(currentCard)
        binding.tvPoints.text = "0/" + numOfCards.toInt()

        /*  note for timer:
            unit = milliseconds
            (no. of ms, interval)
        */
        val musicTime = R.raw.time
        val playmusicTime = MediaPlayer.create(this, musicTime)

        if (playMode == "1") {
            binding.tvTimer.alpha = 1F
            timer = object : CountDownTimer(12000,1000){
                override fun onTick(p0: Long) {
                    // displays no. of seconds left every 1 second
                    ("" + p0/1000 + "s").also { binding.tvTimer.text = it }
                }

                override fun onFinish() {
                    // prgram touch to activate flcard on touch listener
                    if (setSound == 0) {
                        val motionEvent = MotionEvent.obtain(
                            SystemClock.uptimeMillis(),
                            SystemClock.uptimeMillis() + 100,
                            MotionEvent.ACTION_BUTTON_PRESS,
                            0.0f,
                            0.0f,
                            0
                        )
                        binding.flCard.dispatchTouchEvent(motionEvent)
                        playmusicTime?.start()
                    }
                }
            }
        }

        // correct button listener          : 0->no choice, 1->correct, 3->wrong
        binding.imgvCorrect.setOnClickListener {
            if (scoreStatus == 0 || scoreStatus == 2) {
                setButtonColor(binding.imgvCorrect, "#66DE93")
                setButtonColor(binding.imgvWrong, "#F5F4F4")
                scoreStatus = 1
            }
        }

        // wrong button listener
        binding.imgvWrong.setOnClickListener {
            if (scoreStatus == 0 || scoreStatus == 1) {
                setButtonColor(binding.imgvCorrect, "#F5F4F4")
                setButtonColor(binding.imgvWrong, "#FF616D")
                scoreStatus = 2
            }
        }

        // animation on press
        binding.flCard.setOnTouchListener { v, event ->
            if(setCardView==0) {
                binding.cvAnswer.isVisible = true
                //binding.cvQuestion.isVisible = false
                binding.cvQuestion.isClickable = false
                setCardView = 1
                ViewAnimationUtils.createCircularReveal(
                    binding.tvAnswer,
                    event.x.toInt(),
                    event.y.toInt(),
                    0f,
                    hypot(v.width.toFloat(), v.height.toFloat())
                )
                    .setDuration(1000)
                    .start()
            }
            else if(setCardView==1) {
                binding.cvQuestion.isVisible = true
                binding.cvAnswer.isVisible = false
                binding.cvAnswer.isClickable = false
                setCardView = 0
            }
            false

        }

        // next button listener
        binding.btnNext.setOnClickListener{
            binding.btnNext.startAnimation(buttonClick)
            if(scoreStatus==0) {
                Toast.makeText(this, "ERROR! Must check if correct or wrong",
                    Toast.LENGTH_SHORT).show()
            }
            else {
                if (currentCard+1 == numOfCards.toInt()) {
                    setSound = 1
                    if(done == false) {
                        score += 1
                        ("$score/" + numOfCards.toInt()).also { binding.tvPoints.text = it }
                    }

                    //play music
                    val playmusic = MediaPlayer.create(this, R.raw.win)
                    playmusic?.start()

                    val mDialogView = AlertCompleteSetBinding.inflate(layoutInflater)
                    mDialogView.tvScore.text = "$score/" + numOfCards.toInt()
                    mDialogView.tvDescription.text = "This is your score for the $tableName Flash Card Set."

                    val mBuilder = android.app.AlertDialog.Builder(this)
                        .setView(mDialogView.root)

                    val mAlertDialog = mBuilder.show()

                    done = true

                    mDialogView.button.setOnClickListener {
                        mAlertDialog.dismiss()
                        val intent = Intent(this, ThirdActivity::class.java)
                        intent.putExtra("tableName",tableName)
                        intent.putExtra("numOfCards",numOfCards)
                        startActivity(intent)
                    }

                }
                else if (currentCard+1 != numOfCards.toInt()) {
                    currentCard += 1
                    if (scoreStatus == 1) {
                        score += 1
                        ("$score/" + numOfCards.toInt()).also { binding.tvPoints.text = it }
                        val music = R.raw.right
                        val playmusic = MediaPlayer.create(this, music)
                        playmusic?.start()
                    }
                    else if (scoreStatus == 2){
                        val music = R.raw.wrong
                        val playmusic = MediaPlayer.create(this, music)
                        playmusic?.start()
                    }

                    //change color of flashcard
                    colorSet(binding.tvCardInstruction)
                    binding.cvQuestion.isVisible = true
                    binding.cvAnswer.isVisible = false
                    binding.cvAnswer.isClickable = false
                    setCardView = 0

                    // define new question and answer
                    binding.tvCardInstruction.text = questionList.get(currentCard)
                    binding.tvAnswer.text = answerList.get(currentCard)

                    // reset button colors
                    setButtonColor(binding.imgvCorrect, "#F5F4F4")
                    setButtonColor(binding.imgvWrong, "#F5F4F4")
                    scoreStatus = 0


                    if (playMode == "1") {
                        onStop()
                        onStart()
                    }

                }

            }
        }


        binding.btnExit.setOnClickListener {
            score = 0
            if (playMode == "1") {
                onStop()
            }
            val intent = Intent(this,ThirdActivity::class.java)
            intent.putExtra("tableName",tableName)
            intent.putExtra("numOfCards",numOfCards)
            startActivity(intent)
        }



    }

    private fun setButtonColor(view: View, color: String) {
        val gradientDrawable = (view.getBackground() as GradientDrawable).mutate()
        (gradientDrawable as GradientDrawable).setColor(Color.parseColor(color))

    }

    fun colorSet(txt: TextView){
        val colorList = arrayListOf<Int>(R.color.pastel1,R.color.pastel2,R.color.pastel3,R.color.pastel4, R.color.pastel5)
        if(currentColor == 4) currentColor = 0
        else currentColor += 1
        txt.setBackgroundResource(colorList.get(currentColor))

    }

    private fun getContents(binding: ActivitySecondBinding) {
        var setQuestions = ArrayList<String>()
        var setAnswers = ArrayList<String>()
        var setNum: Int
        val flashcards = flashcardDBHelper.readAllQuestions(tableName)

        if(flashcards.isNotEmpty()) {
            try {
                flashcards.forEach {
                    setQuestions.add(it.question)
                    setAnswers.add(it.answer)
                }
                //Toast.makeText(this,"Fetched Flashcards", Toast.LENGTH_SHORT).show()
            } catch(e: Exception) {
                Toast.makeText(this,"$e", Toast.LENGTH_SHORT).show()
            }
            questionList = setQuestions
            answerList = setAnswers

            setNum = flashcardDBHelper.getCount(tableName)
            numOfCards = setNum.toString()
        }
        else {
            questionList = setQuestions
            answerList = setAnswers
            setNum = 0
            Toast.makeText(this,"Empty Flashcard Set", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        timer.start()
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

}
package com.example.flashcard_app

import android.media.MediaPlayer
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.flashcard_app.databinding.ActivitySecondBinding
//import com.example.flashcard_app.databinding.ActivityThirdBinding

class SecondActivity : AppCompatActivity() {

    private lateinit var timer : CountDownTimer

    companion object {
        lateinit var flashcardDBHelper : FlashcardDBHelper
        var questionList = ArrayList<String>()
        var answerList = ArrayList<String>()
        lateinit var numOfCards: String
        lateinit var tableName: String
        lateinit var binding: ActivitySecondBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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

        var score = 0
        var currentCard = 0
        var scoreStatus = 0
        val bundle: Bundle? = intent.extras
        val playMode = bundle!!.getString("user_mode")
        val name =  bundle!!.getString("tableName")
        tableName = name.toString()

        getContents(binding)

        // code to display 1st question here
        binding.tvCardInstruction.text = questionList.get(currentCard)

        /*  note for timer:
            unit = milliseconds
            (no. of ms, interval)
        */
        val musicTime = R.raw.time
        val playmusicTime = MediaPlayer.create(this, musicTime)

        if (playMode == "1") {
            binding.textView3.alpha = 1F
            timer = object : CountDownTimer(12000,1000){
                override fun onTick(p0: Long) {
                    // displays no. of seconds left every 1 second
                    ("Time left: " + p0/1000).also { binding.textView3.text = it }
                }

                override fun onFinish() {
                    // code to display answer
                    binding.tvAnswer.text = answerList.get(currentCard)
                    binding.tvAnswer.alpha = 1F
                    playmusicTime?.start()
                }
            }
        }

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, _ ->
            if (binding.radioBtnCheck.isChecked) {
                scoreStatus = 1
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            }
            else if (binding.radioBtnWrong.isChecked) {
                scoreStatus = 0
                Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnShow.setOnClickListener {
            binding.tvAnswer.text = answerList.get(currentCard)
            binding.tvAnswer.alpha = 1F
        }

        binding.btnNext.setOnClickListener {
            if (binding.radioBtnCheck.isChecked == false && binding.radioBtnWrong.isChecked == false) {
                Toast.makeText(this, "ERROR! Must check if correct or wrong", Toast.LENGTH_SHORT).show()
            }
            else {
                currentCard += 1
                if (currentCard >= numOfCards.toInt()) {
                    currentCard = (numOfCards.toInt() - 1)
                    AlertDialog.Builder(this)
                        .setTitle("End of Flashcards")
                        .setMessage("Return to Menu")
                        .setPositiveButton("OK") { dialog, which ->
                            val intent = Intent(this, ThirdActivity::class.java)
                            intent.putExtra("tableName",tableName)
                            intent.putExtra("numOfCards",numOfCards)
                            startActivity(intent)
                        }.show()
                }
                else {
                    if (scoreStatus == 1) {
                        score += 1
                        ("Score: " + score).also { binding.textView4.text = it }
                        val music = R.raw.right
                        val playmusic = MediaPlayer.create(this, music)
                        playmusic?.start()
                    }
                    else {
                        val music = R.raw.wrong
                        val playmusic = MediaPlayer.create(this, music)
                        playmusic?.start()
                    }

                    binding.tvAnswer.alpha = 0F
                    binding.tvCardInstruction.text = questionList.get(currentCard)

                    if (playMode == "1") {
                        onStop()
                        onStart()
                    }
                    binding.radioBtnCheck.isChecked = false
                    binding.radioBtnWrong.isChecked = false
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
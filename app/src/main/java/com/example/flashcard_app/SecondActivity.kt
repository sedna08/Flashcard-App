package com.example.flashcard_app

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flashcard_app.databinding.ActivitySecondBinding
import com.example.flashcard_app.databinding.ActivityThirdBinding

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

        var score = 0
        val bundle: Bundle? = intent.extras
        val playMode = bundle!!.getString("user_mode")
        val name =  bundle!!.getString("tableName")
        tableName = name.toString()

        getContents(binding)


        // code to display 1st question here
        binding.tvCardInstruction.text = questionList.get(0)

        // to access answer "nth" answer
        // answerList.get(n).toString()

        // to access max questions
        // use numOfCards variable

        /*  note for timer:
            unit = milliseconds
            5000 = time left
            1000 = interval of tick
        */

        if (playMode == "1") {
            timer = object : CountDownTimer(5000,1000){
                override fun onTick(p0: Long) {
                    // displays no. of seconds left every 1 second
                    ("Time left: " + p0/1000).also { binding.textView3.text = it }
                }

                override fun onFinish() {
                    // code to display next question
                    binding.textView2.text = answerList.get(0) // remove this
                    // Just play Sound here after time is done
                }

            }
        }


        binding.radioGroup.setOnCheckedChangeListener { radioGroup, _ ->
            if (binding.radioBtnCheck.isChecked) {
                score = score + 1
                ("Score: " + score).also { binding.textView4.text = it }
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            }
            else if (binding.radioBtnWrong.isChecked) {
                Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnShow.setOnClickListener {
            // code to display answer
        }

        binding.btnNext.setOnClickListener {
            if (binding.radioBtnCheck.isChecked == false && binding.radioBtnWrong.isChecked == false) {
                Toast.makeText(this, "ERROR! Must check if correct or wrong", Toast.LENGTH_SHORT).show()
            }
            else {

                // code to display next question
                if (playMode == "1") {
                    onStop()
                    onStart()
                }
            }
            TODO("Condition to Check for Max Questions")
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
                Toast.makeText(this,"Fetched Flashcards", Toast.LENGTH_SHORT).show()
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
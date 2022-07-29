package com.example.flashcard_app

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flashcard_app.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private lateinit var timer : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var score = 0
        val bundle: Bundle? = intent.extras
        val playMode = bundle!!.getString("user_mode")

        // code to display 1st question here

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
                    TODO("Not yet implemented")
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
        }

        binding.btnExit.setOnClickListener {
            score = 0
            if (playMode == "1") {
                onStop()
            }
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
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
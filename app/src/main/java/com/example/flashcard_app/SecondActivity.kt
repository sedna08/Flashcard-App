package com.example.flashcard_app
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flashcard_app.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var score = 0

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, _ ->
            if (binding.radioBtnCheck.isChecked) {
                score = score + 1
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            }
            else if (binding.radioBtnWrong.isChecked) {
                Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnNext.setOnClickListener {
            if (binding.radioBtnCheck.isChecked == false && binding.radioBtnWrong.isChecked == false) {
                Toast.makeText(this, "ERROR! Must check if correct or wrong", Toast.LENGTH_SHORT).show()
            }
            else {
                // display next question
            }
        }

        binding.btnExit.setOnClickListener {
            score = 0
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }


    }

}
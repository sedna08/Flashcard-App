package com.example.flashcard_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flashcard_app.databinding.ActivityMainBinding
import com.example.flashcard_app.databinding.AlertDialogBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide();
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, ThirdActivity::class.java)
        startActivity(intent)

    }
}
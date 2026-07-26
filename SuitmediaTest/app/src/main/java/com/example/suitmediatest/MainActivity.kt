package com.example.suitmediatest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.suitmediatest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCheck.setOnClickListener {
            val sentence = binding.etSentence.text.toString()
            if (sentence.isEmpty()) {
                binding.etSentence.error = "Please enter a sentence"
                return@setOnClickListener
            }

            val message = if (isPalindrome(sentence)) "isPalindrome" else "not palindrome"
            AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }

        binding.btnNext.setOnClickListener {
            val name = binding.etName.text.toString()
            if (name.isEmpty()) {
                binding.etName.error = "Please enter your name"
                return@setOnClickListener
            }

            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("USER_NAME", name)
            startActivity(intent)
        }
    }

    private fun isPalindrome(text: String): Boolean {
        val cleanText = text.replace("\\s+".toRegex(), "").lowercase()
        return cleanText == cleanText.reversed()
    }
}

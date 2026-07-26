package com.example.suitmediatest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.suitmediatest.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    // Callback ThirdActivity
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedName = result.data?.getStringExtra("SELECTED_USER_NAME")
            if (!selectedName.isNullOrEmpty()) {
                binding.tvSelectedUser.text = selectedName
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("USER_NAME") ?: "User"
        binding.tvName.text = name

        binding.btnChooseUser.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            launcher.launch(intent)
        }
    }
}

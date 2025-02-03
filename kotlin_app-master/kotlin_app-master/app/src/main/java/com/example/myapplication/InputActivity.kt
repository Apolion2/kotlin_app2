package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.WeightEntry
import com.example.myapplication.databinding.ActivityInputBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        val weightDao = db.weightDao()

        binding.saveButton.setOnClickListener {
            val weightText = binding.weightInput.text.toString()
            if (weightText.isNotEmpty()) {
                val weight = weightText.toFloatOrNull()
                if (weight == null) {
                    Toast.makeText(this, "Будь ласка, введіть коректне число!", Toast.LENGTH_SHORT).show()
                } else if (weight > 200) {
                    Toast.makeText(this, "Помилка: максимальна допустима вага — 200 кг!", Toast.LENGTH_LONG).show()
                } else {
                    val dateTime = getCurrentDateTime()

                    // Зберігаємо в базу даних
                    lifecycleScope.launch {
                        weightDao.insertWeight(WeightEntry(weight = weight, date = dateTime))
                    }

                    Toast.makeText(this, "Вага збережена!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Toast.makeText(this, "Будь ласка, введіть вагу!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}

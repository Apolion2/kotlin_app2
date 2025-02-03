package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.WeightAdapter
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.WeightEntry
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var inputActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        val weightDao = db.weightDao()

        // Реєструємо ActivityResultLauncher
        inputActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val userWeight = data?.getStringExtra("USER_WEIGHT")

                if (!userWeight.isNullOrEmpty()) {
                    binding.weightDisplay.text = getString(R.string.weight_display, userWeight)

                    // Зберігаємо у базу даних
                    lifecycleScope.launch {
                        val dateTime = getCurrentDateTime()
                        val weightEntry = WeightEntry(weight = userWeight.toFloat(), date = dateTime)
                        weightDao.insertWeight(weightEntry)
                        updateWeightLog()
                    }
                }
            }
        }

        // Налаштування RecyclerView для логів змін ваги
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Завантажуємо історію ваги при запуску
        updateWeightLog()

        // Завантажуємо останню вагу при старті
        updateWeightDisplay()

        // Обробка натискання кнопки "Введення даних"
        binding.menuInput.setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
            inputActivityLauncher.launch(intent)
        }
    }

    // Оновлення списку змін ваги
    private fun updateWeightLog() {
        val db = AppDatabase.getDatabase(this)
        val weightDao = db.weightDao()

        lifecycleScope.launch {
            weightDao.getAllWeights().collect { weights ->
                binding.recyclerView.adapter = WeightAdapter(weights)
            }
        }
    }

    // Оновлення відображення останньої ваги
    @SuppressLint("StringFormatMatches")
    private fun updateWeightDisplay() {
        val db = AppDatabase.getDatabase(this)
        val weightDao = db.weightDao()

        lifecycleScope.launch {
            weightDao.getAllWeights().collect { weights ->
                if (weights.isNotEmpty()) {
                    val latestWeight = weights.first()
                    binding.weightDisplay.text = getString(R.string.weight_display, latestWeight.weight)

                    // Лог для перевірки
                    android.util.Log.d("DB_FETCH", "Остання вага: ${latestWeight.weight} кг, ${latestWeight.date}")
                } else {
                    binding.weightDisplay.text = "Ваша вага: N/A кг"
                }
            }
        }
    }

    // Отримуємо поточну дату і час
    private fun getCurrentDateTime(): String {
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}

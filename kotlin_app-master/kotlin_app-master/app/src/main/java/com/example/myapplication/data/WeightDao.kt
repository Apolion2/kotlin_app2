package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Insert
    suspend fun insertWeight(weightEntry: WeightEntry)

    // Використовуємо Flow для отримання даних у реальному часі
    @Query("SELECT * FROM weight_logs ORDER BY id DESC")
    fun getAllWeights(): Flow<List<WeightEntry>>
}

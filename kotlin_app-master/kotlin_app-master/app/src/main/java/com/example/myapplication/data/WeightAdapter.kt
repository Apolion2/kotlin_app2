package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.WeightEntry
import com.example.myapplication.databinding.ItemWeightBinding

class WeightAdapter(private val weights: List<WeightEntry>) :
    RecyclerView.Adapter<WeightAdapter.WeightViewHolder>() {

    class WeightViewHolder(private val binding: ItemWeightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(weightEntry: WeightEntry) {
            binding.weightText.text = "${weightEntry.weight} кг"
            binding.dateText.text = weightEntry.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val binding = ItemWeightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        holder.bind(weights[position])
    }

    override fun getItemCount(): Int = weights.size
}

package com.example.sudokugame.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sudokugame.R
import com.example.sudokugame.database.SudokuEntity
import com.example.sudokugame.viewmodel.SudokuDataViewModel
import com.google.android.material.imageview.ShapeableImageView

class MyRecyclerViewAdapter(private val sudokuEntityList: List<SudokuEntity>, private val dataViewModel: SudokuDataViewModel) :
    RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sudoku_data_item, parent,false)
        return MyViewHolder(view, dataViewModel)
    }

    override fun getItemCount(): Int {
      return  sudokuEntityList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(sudokuEntityList[position])
        holder.position.text = position.plus(1).toString().plus(".")
    }
}

class MyViewHolder(itemView: View, private val dataViewModel: SudokuDataViewModel) : ViewHolder(itemView){
    private val timeTaken = itemView.findViewById<TextView>(R.id.tvTimeTaken)
    private val date = itemView.findViewById<TextView>(R.id.tvDate)
    val position: TextView = itemView.findViewById(R.id.tvPosition)
//   private val isCompleted = itemView.findViewById<TextView>(R.id.tvIsCompleted)
//   private val hintsUsed = itemView.findViewById<TextView>(R.id.tvHintsUsed)
    private val deleteData = itemView.findViewById<ImageView>(R.id.ivDelete)
    fun bind(sudokuEntity: SudokuEntity){
        date.text = sudokuEntity.date
        timeTaken.text = sudokuEntity.timeTaken
     //   mistakes.text = sudokuEntity.mistakes.toString()
//        isCompleted.text = sudokuEntity.isCompleted.toString()
//        hintsUsed.text = sudokuEntity.hintsUsed.toString()

        deleteData.setOnClickListener {
            dataViewModel.delete(sudokuEntity)
        }
    }
}
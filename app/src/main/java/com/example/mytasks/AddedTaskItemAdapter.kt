package com.example.mytasks

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddedTaskItemAdapter(val context: Context, var addedTaskItemList:ArrayList<AddedTaskItemData>): RecyclerView.Adapter<AddedTaskItemAdapter.AddedTaskItemDataViewHolder>() {

    inner class AddedTaskItemDataViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val content: TextView = view.findViewById(R.id.taskContentArea)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddedTaskItemAdapter.AddedTaskItemDataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.edit_task_list_item, parent, false)
        return AddedTaskItemDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddedTaskItemAdapter.AddedTaskItemDataViewHolder, position: Int) {

        val list = addedTaskItemList[position]

        holder.content.text = list.taskContent
    }

    override fun getItemCount(): Int {
        return addedTaskItemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAllItems() {
        addedTaskItemList.clear()
        notifyDataSetChanged()
    }
}
package com.example.mytasks

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskListHeadAdapter(val context: Context, var dataTaskHeadList:ArrayList<TaskListHeadData>): RecyclerView.Adapter<TaskListHeadAdapter.TaskListHeadDataViewHolder>() {

    inner class TaskListHeadDataViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val taskListName: TextView = view.findViewById(R.id.taskListName)
        val taskListDescription: TextView = view.findViewById(R.id.textDescription)
        val idTaskList: TextView = view.findViewById(R.id.idView)
        val allTasksTaskList: TextView = view.findViewById(R.id.allTasks)
        val dateTaskList: TextView = view.findViewById(R.id.taskDateView)
        val dateToCompleteTaskList: TextView = view.findViewById(R.id.taskDateToCompleteView)
        val progress: ProgressBar = view.findViewById(R.id.progressBar)
        val completedChecker: TextView = view.findViewById(R.id.completedView)
        val delete: ImageButton = view.findViewById(R.id.deleteListOfTasks)
        val viewTasks: ImageButton = view.findViewById(R.id.viewListOfTasks)
        val tagView: ImageView = view.findViewById(R.id.tagItem)
        val generalCard: LinearLayout = view.findViewById(R.id.cardCascade)
        val descriptionCard: LinearLayout = view.findViewById(R.id.descriptionCascade)
        val nameCard: LinearLayout = view.findViewById(R.id.nameCascade)
        val greenIcon: ImageView = view.findViewById(R.id.completedIcon)
        val calendar: ImageView = view.findViewById(R.id.calendarIcon)
        val taskList: ImageView = view.findViewById(R.id.taskIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListHeadAdapter.TaskListHeadDataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.task_head_item, parent, false)
        return TaskListHeadDataViewHolder(view)
    }

    private val tagList: MutableList<String> = mutableListOf()
    private val completedList: MutableList<String> = mutableListOf()

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskListHeadAdapter.TaskListHeadDataViewHolder, position: Int) {

        val tasksListHeadDBHelper = TaskHeadDB(context)
        val tasksListDBHelper = TasksDB(context)

        val list = dataTaskHeadList[position]

        holder.taskListName.text = list.task_list_name
        holder.taskListDescription.text = list.task_list_description
        holder.idTaskList.text = list.id.toString()
        holder.allTasksTaskList.text = list.all_tasks.toString()
        holder.dateTaskList.text = list.date_today
        holder.dateToCompleteTaskList.text = list.must_completed
        holder.completedChecker.text = list.tasks_completed.toString()

        holder.greenIcon.visibility = View.GONE

        tagList.clear()
        completedList.clear()

        val dataBase1 = tasksListHeadDBHelper.readableDatabase
        val idSelection = holder.idTaskList.text.toString()
        val tagColor = arrayOf("TagType", "IsCompleted")
        val Cursor = dataBase1.query("TaskHeadsTable", tagColor, idSelection, null, null, null, null)

        while (Cursor.moveToNext()) {
            val tags = Cursor.getString(Cursor.getColumnIndexOrThrow("TagType"))
            val completed = Cursor.getString(Cursor.getColumnIndexOrThrow("IsCompleted"))
            tagList.add(tags)
            completedList.add(completed)
        }

        Cursor.close()
        dataBase1.close()

        holder.taskListName.isSelected = true

        val el1 = context.getString(R.string.withoutTag)
        val el2 = context.getString(R.string.greenTag)
        val el3 = context.getString(R.string.yellowTag)
        val el4 = context.getString(R.string.redTag)

        when (list.tag_data) {
            el1 -> {
                holder.tagView.visibility = View.GONE
                holder.generalCard.setBackgroundResource(R.drawable.sky_square_border)
                holder.descriptionCard.setBackgroundResource(R.drawable.task_list_description_border)
                holder.nameCard.setBackgroundColor(ContextCompat.getColor(context, R.color.sky))
                holder.viewTasks.setBackgroundColor(ContextCompat.getColor(context, R.color.sky))
                holder.calendar.setColorFilter(ContextCompat.getColor(context, R.color.sky))
                holder.taskList.setColorFilter(ContextCompat.getColor(context, R.color.sky))
            }
            el2 -> {
                holder.tagView.visibility = View.VISIBLE
                holder.tagView.setColorFilter(ContextCompat.getColor(context, R.color.grass))
                holder.generalCard.setBackgroundResource(R.drawable.green_square_border)
                holder.descriptionCard.setBackgroundResource(R.drawable.green_description_border)
                holder.nameCard.setBackgroundColor(ContextCompat.getColor(context, R.color.lime))
                holder.viewTasks.setBackgroundColor(ContextCompat.getColor(context, R.color.grass))
                holder.calendar.setColorFilter(ContextCompat.getColor(context, R.color.grass))
                holder.taskList.setColorFilter(ContextCompat.getColor(context, R.color.grass))
            }
            el3 -> {
                holder.tagView.visibility = View.VISIBLE
                holder.tagView.setColorFilter(ContextCompat.getColor(context, R.color.danger))
                holder.generalCard.setBackgroundResource(R.drawable.yellow_square_border)
                holder.descriptionCard.setBackgroundResource(R.drawable.yellow_description_border)
                holder.nameCard.setBackgroundColor(ContextCompat.getColor(context, R.color.lemon))
                holder.viewTasks.setBackgroundColor(ContextCompat.getColor(context, R.color.danger))
                holder.calendar.setColorFilter(ContextCompat.getColor(context, R.color.danger))
                holder.taskList.setColorFilter(ContextCompat.getColor(context, R.color.danger))
            }
            el4 -> {
                holder.tagView.visibility = View.VISIBLE
                holder.tagView.setColorFilter(ContextCompat.getColor(context, R.color.red))
                holder.generalCard.setBackgroundResource(R.drawable.red_square_border)
                holder.descriptionCard.setBackgroundResource(R.drawable.red_description_border)
                holder.nameCard.setBackgroundColor(ContextCompat.getColor(context, R.color.ruby))
                holder.viewTasks.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                holder.calendar.setColorFilter(ContextCompat.getColor(context, R.color.red))
                holder.taskList.setColorFilter(ContextCompat.getColor(context, R.color.red))
            }
        }

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        val dateNow = formattedDate.toString()
        val dateMustCompleted = holder.dateToCompleteTaskList.text.toString()

        //println("$dateNow, $dateMustCompleted")

        val idData = holder.idTaskList.text.toString()

        val database = tasksListHeadDBHelper.readableDatabase

        val selectionId = "Id = $idData"
        val projection = arrayOf("AllTasks", "CompletedTasks")
        val cursor = database.query("TaskHeadsTable", projection, selectionId, null, null, null, null)

        var allTasks = 0
        var completedTasks = 0

        while (cursor.moveToNext()) {
            allTasks = cursor.getInt(cursor.getColumnIndexOrThrow("AllTasks"))
            completedTasks = cursor.getInt(cursor.getColumnIndexOrThrow("CompletedTasks"))
        }

        cursor.close()
        database.close()

        holder.progress.max = allTasks
        holder.progress.progress = completedTasks
        holder.completedChecker.text = "$completedTasks/$allTasks ✓"

        val completedPercent = holder.progress.progress

        val Database1 = tasksListHeadDBHelper.writableDatabase

        val completedAnimation = AnimationUtils.loadAnimation(context, R.anim.blink_animation)

        if (completedPercent == allTasks) {
            completedList[position] = "Yes"
            holder.greenIcon.visibility = View.VISIBLE
            holder.greenIcon.startAnimation(completedAnimation)
            val completedValues = ContentValues().apply {
                put("IsCompleted", "Yes")
            }
            Database1.update("TaskHeadsTable", completedValues, selectionId, null)
            Database1.close()
        } else {
            completedList[position] = "No"
            holder.greenIcon.visibility = View.GONE
            val completedValues = ContentValues().apply {
                put("IsCompleted", "No")
            }
            Database1.update("TaskHeadsTable", completedValues, selectionId, null)
            Database1.close()
        }

        holder.itemView.post {

            if (dateMustCompleted == dateNow) {
                val database1 = tasksListHeadDBHelper.writableDatabase
                val database2 = tasksListDBHelper.writableDatabase

                val selectionId = "Id = $idData"

                database1.delete("TaskHeadsTable", selectionId, null)

                val gateKey = arrayOf("GateKey")
                val cursor1 = database1.query("TaskHeadsTable", gateKey, selectionId, null, null, null, null)

                var gateKeyValue = ""

                while (cursor1.moveToNext()) {
                    gateKeyValue = cursor1.getString(cursor1.getColumnIndexOrThrow("GateKey"))
                }

                cursor1.close()

                val selectionGateKey = "GateKeyToOpen = '$gateKeyValue'"
                database2.delete("TasksTable", selectionGateKey, null)

                database1.close()
                database2.close()
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val positionsToRemove = listOf(pos)

                    if (positionsToRemove.all { it < dataTaskHeadList.size && it < tagList.size && it < completedList.size }) {

                        positionsToRemove.sortedDescending().forEach { position ->
                            dataTaskHeadList.removeAt(position)
                            tagList.removeAt(position)
                            completedList.removeAt(position)
                            notifyItemRemoved(position)
                        }
                    }
                }

                val deletedList = context.getString(R.string.deletedList)

                val alertDeleteBar = Snackbar.make(
                    (context as Activity).findViewById(android.R.id.content),
                    "$deletedList\uD83D\uDCCB❌",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setBackgroundTint(ContextCompat.getColor(context, R.color.red))
                    .setTextColor(Color.WHITE)
                    .setDuration(2000)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                alertDeleteBar.show()
            }
        }

        fun deleteThisTasksList() {
            val database1 = tasksListHeadDBHelper.writableDatabase
            val database2 = tasksListDBHelper.writableDatabase
            val layoutInflater: LayoutInflater = LayoutInflater.from(context)
            val builder = AlertDialog.Builder(context)
            val title = context.getString(R.string.deleteThis)
            builder.setTitle(title)
            val inflater = layoutInflater
            val dialog = inflater.inflate(R.layout.alert_delete_tasks_list, null)
            builder.setView(dialog)

            fun searchAndDelete() {
                database1.delete("TaskHeadsTable", selectionId, null)

                val gateKey = arrayOf("GateKey")
                val cursor1 = database1.query("TaskHeadsTable", gateKey, selectionId, null, null, null, null)

                var gateKeyValue = ""

                while (cursor1.moveToNext()) {
                    gateKeyValue = cursor1.getString(cursor1.getColumnIndexOrThrow("GateKey"))
                }

                cursor1.close()

                val selectionGateKey = "GateKeyToOpen = '$gateKeyValue'"
                database2.delete("TasksTable", selectionGateKey, null)

                database1.close()
                database2.close()

                val pos = holder.adapterPosition
                dataTaskHeadList.removeAt(pos)
                tagList.removeAt(pos)
                completedList.removeAt(pos)
                notifyItemRemoved(pos)

                val deleted = context.getString(R.string.thisListDeleted)

                val alertBar = Snackbar.make((context as Activity).findViewById(android.R.id.content), "$deleted\uD83D\uDCCB❌", Snackbar.LENGTH_INDEFINITE)
                    .setBackgroundTint(ContextCompat.getColor(context, R.color.lime))
                    .setTextColor(Color.WHITE)
                    .setDuration(2000)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                alertBar.show()
            }

            val yes = context.getString(R.string.yes)
            val no = context.getString(R.string.no)

            builder.setPositiveButton(yes) { dialog, _ ->
                searchAndDelete()
            }

            builder.setNegativeButton(no) { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }

        holder.delete.setOnClickListener {
            deleteThisTasksList()
        }

        fun viewTasksList() {
            val id = holder.idTaskList.text.toString()
            val name = holder.taskListName.text.toString()
            val transferIdAndName = Intent(context, TasksListView::class.java).apply {
                putExtra("TasksListId", id)
                putExtra("TasksListName", name)
            }
            context.startActivity(transferIdAndName)
        }

        holder.viewTasks.setOnClickListener {
            viewTasksList()
        }
    }

    override fun getItemCount(): Int {
        return dataTaskHeadList.size
    }

    fun updateTagListAndIsCompletedList(list1: MutableList<String>, list2: MutableList<String>) {
        list1.clear()
        list2.clear()
        list1.addAll(tagList)
        list2.addAll(completedList)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: ArrayList<TaskListHeadData>) {
        dataTaskHeadList = newList
        notifyDataSetChanged()
    }
}
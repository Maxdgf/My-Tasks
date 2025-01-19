package com.example.mytasks

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TasksListViewAdapter(val context: Context, var taskItemList:ArrayList<TasksListViewData>): RecyclerView.Adapter<TasksListViewAdapter.TaskItemDataViewHolder>() {

    inner class TaskItemDataViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val taskChecker: CheckBox = view.findViewById(R.id.taskChecker)
        val taskText: TextView = view.findViewById(R.id.taskContentArea)
        val textBg: LinearLayout = view.findViewById(R.id.textBgLayer)
        val taskNum: TextView = view.findViewById(R.id.taskNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksListViewAdapter.TaskItemDataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.task_list_item, parent, false)
        return TaskItemDataViewHolder(view)
    }

    private val tasksIsCheckedList: MutableList<Boolean> = mutableListOf()

    override fun onBindViewHolder(holder: TasksListViewAdapter.TaskItemDataViewHolder, position: Int) {

        val tasksListHeadDBHelper = TaskHeadDB(context)
        val tasksListDBHelper = TasksDB(context)

        val list = taskItemList[position]

        val elementPos = position + 1

        holder.taskText.text = list.taskContent
        holder.taskChecker.isChecked = list.isChecked.toBoolean()
        holder.taskNum.text = elementPos.toString()

        println(list.isChecked)

        when (holder.taskChecker.isChecked) {
            true -> {
                holder.taskChecker.text = "✓"
                holder.taskChecker.setBackgroundColor(Color.GREEN)
                holder.textBg.setBackgroundColor(Color.GREEN)
            }
            false -> {
                holder.taskChecker.text = "✖"
                holder.taskChecker.setBackgroundColor(Color.RED)
                holder.textBg.setBackgroundColor(Color.RED)
            }
        }

        val activity: TasksListView = holder.view.context as TasksListView
        val map = activity.getId()

        val id = map["currentId"].toString()

        val DataBase1 = tasksListHeadDBHelper.readableDatabase
        val Database2 = tasksListDBHelper.readableDatabase

        val selectionId = "Id = $id"
        val GateKey = arrayOf("GateKey")
        val Cursor1 = DataBase1.query("TaskHeadsTable", GateKey, selectionId, null, null, null, null)

        var keyGate = ""

        while (Cursor1.moveToNext()) {
            keyGate = Cursor1.getString(Cursor1.getColumnIndexOrThrow("GateKey"))
        }

        Cursor1.close()
        DataBase1.close()

        val selectionGateKey = "GateKeyToOpen = '$keyGate'"
        val checkedStates = arrayOf("IsChecked")
        val Cursor2 = Database2.query("TasksTable", checkedStates, selectionGateKey, null, null, null, null)

        var checks = ""

        while (Cursor2.moveToNext()) {
            checks = Cursor2.getString(Cursor2.getColumnIndexOrThrow("IsChecked"))
        }

        val checkedList = checks.split(",").map { it.trim().toBoolean() }

        tasksIsCheckedList.clear()
        tasksIsCheckedList.addAll(checkedList)

        fun switchChecker() {
            val state = holder.taskChecker.isChecked

            val database1 = tasksListHeadDBHelper.readableDatabase
            val database2 = tasksListDBHelper.writableDatabase

            val gateKey = arrayOf("GateKey")
            val cursor1 = database1.query("TaskHeadsTable", gateKey, selectionId, null, null, null, null)

            var gateKeyValue = ""

            while (cursor1.moveToNext()) {
                gateKeyValue = cursor1.getString(cursor1.getColumnIndexOrThrow("GateKey"))
            }

            cursor1.close()
            database1.close()

            val selectionGateKey2 = "GateKeyToOpen = '$gateKeyValue'"

            val pos = holder.adapterPosition
            tasksIsCheckedList[pos] = state
            notifyItemChanged(pos)

            val checkedListToSave = tasksIsCheckedList.joinToString(",")

            val checkedValues = ContentValues().apply {
                put("IsChecked", checkedListToSave)
            }
            database2.update("TasksTable", checkedValues, selectionGateKey2, null)
            database2.close()

            when (state) {
                true -> {
                    holder.taskChecker.text = "✓"
                    holder.taskChecker.setBackgroundColor(Color.GREEN)
                    holder.textBg.setBackgroundColor(Color.GREEN)
                    list.isChecked = "true"
                }
                false -> {
                    holder.taskChecker.text = "✖"
                    holder.taskChecker.setBackgroundColor(Color.RED)
                    holder.textBg.setBackgroundColor(Color.RED)
                    list.isChecked = "false"
                }
            }

            val Database1 = tasksListHeadDBHelper.writableDatabase

            val completedCount = tasksIsCheckedList.count { it }

            val completedValues = ContentValues().apply {
                put("CompletedTasks", completedCount)
            }
            Database1.update("TaskHeadsTable", completedValues, selectionId, null)
            Database1.close()
        }

        holder.taskChecker.setOnClickListener {
            switchChecker()
        }
    }

    override fun getItemCount(): Int {
        return taskItemList.size
    }
}
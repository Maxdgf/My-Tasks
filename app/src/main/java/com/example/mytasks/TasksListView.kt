package com.example.mytasks

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TasksListView : AppCompatActivity() {

    private val tasksDBHelper = TasksDB(this)
    private val tasksHeadDBHelper = TaskHeadDB(this)

    private lateinit var backToMenu: ImageButton
    private lateinit var tasksArea: RecyclerView
    private lateinit var listName: TextView
    private lateinit var listId: TextView
    private lateinit var allCount: TextView
    private lateinit var valuesList: ArrayList<TasksListViewData>
    private lateinit var recyclerViewAdapter: TasksListViewAdapter

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_list_view)

        valuesList = ArrayList()

        backToMenu = findViewById(R.id.backToMenu)
        tasksArea = findViewById(R.id.tasksListView)
        listName = findViewById(R.id.nameView)
        listId = findViewById(R.id.idView)
        allCount = findViewById(R.id.countView)

        recyclerViewAdapter = TasksListViewAdapter(this, valuesList)
        tasksArea.layoutManager = LinearLayoutManager(this)
        tasksArea.adapter = recyclerViewAdapter

        backToMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            valuesList.clear()
            startActivity(intent)
            finish()
        }

        val intent = intent

        val tasksListId = intent.getStringExtra("TasksListId").toString()
        val tasksListName = intent.getStringExtra("TasksListName").toString()

        listName.text = tasksListName
        listId.text = "Id: $tasksListId"

        listName.isSelected = true

        valuesList.clear()

        val database2 = tasksDBHelper.readableDatabase
        val database1 = tasksHeadDBHelper.readableDatabase

        val selectionId = "Id = $tasksListId"
        val gateKey = arrayOf("GateKey")
        val cursor1 = database1.query("TaskHeadsTable", gateKey, selectionId, null, null, null, null)

        var gateKeyValue = ""

        while (cursor1.moveToNext()) {
            gateKeyValue = cursor1.getString(cursor1.getColumnIndexOrThrow("GateKey"))
        }

        cursor1.close()
        database1.close()

        val selectionGateKey = "GateKeyToOpen = '$gateKeyValue'"
        val projection = arrayOf("TaskContent", "IsChecked")
        val cursor2 = database2.query("TasksTable", projection, selectionGateKey, null, null, null, null)

        var text = ""
        var isChecked = ""

        while (cursor2.moveToNext()) {
            text = cursor2.getString(cursor2.getColumnIndexOrThrow("TaskContent"))
            isChecked = cursor2.getString(cursor2.getColumnIndexOrThrow("IsChecked"))

            println("text: $text")
            println("isChecked: $isChecked")
        }

        val textList = text.split(",").map { it.trim() }
        val isCheckedList = isChecked.split(",").map { it.trim() }

        val dataLists = textList.zip(isCheckedList)

        dataLists.forEach { pair ->
            val txt = pair.first
            val check = pair.second
            valuesList.add(TasksListViewData(txt, check))
        }

        cursor2.close()
        database2.close()

        recyclerViewAdapter.notifyDataSetChanged()

        val tasks = getString(R.string.count)
        val count = recyclerViewAdapter.itemCount.toString()
        allCount.text = "$tasks $count"
    }

    fun getId(): HashMap<String, String> {
        val map = HashMap<String, String>()
        val tasksListId = intent.getStringExtra("TasksListId").toString()
        map["currentId"] = tasksListId
        return map
    }
}
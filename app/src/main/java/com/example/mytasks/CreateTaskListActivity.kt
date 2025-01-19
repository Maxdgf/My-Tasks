package com.example.mytasks

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class CreateTaskListActivity : AppCompatActivity() {

    private val taskListHeadDBHelper = TaskHeadDB(this)
    private val tasksDBHelper = TasksDB(this)

    private lateinit var backToMenu: ImageButton
    private lateinit var taskName: EditText
    private lateinit var taskDescription: EditText
    private lateinit var datePicker: DatePicker
    private lateinit var tasksRecycler: RecyclerView
    private lateinit var addTaskContent: EditText
    private lateinit var addTask: ImageButton
    private lateinit var clearTaskList: ImageButton
    private lateinit var itemCounter: TextView
    private lateinit var applyData: Button
    private lateinit var addTaskList: ArrayList<AddedTaskItemData>
    private lateinit var recyclerViewAdapter: AddedTaskItemAdapter
    private lateinit var tagSpinner: Spinner
    private lateinit var spinTagAdapter: selectTagAdapter
    private lateinit var descriptionSymCounter: TextView

    @SuppressLint("MissingInflatedId", "SetTextI18n", "NotifyDataSetChanged", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_task_list)

        addTaskList = ArrayList()

        backToMenu = findViewById(R.id.backToMenu)
        taskName = findViewById(R.id.taskListName)
        taskDescription = findViewById(R.id.taskListDescription)
        datePicker = findViewById(R.id.datePicker)
        tasksRecycler = findViewById(R.id.addedTasksView)
        addTaskContent = findViewById(R.id.taskContent)
        addTask = findViewById(R.id.addTask)
        clearTaskList = findViewById(R.id.clearTaskList)
        itemCounter = findViewById(R.id.taskCounter)
        applyData = findViewById(R.id.createTaskList)
        tagSpinner = findViewById(R.id.selectTag)
        descriptionSymCounter = findViewById(R.id.symbolsCounter)

        val el1 = getString(R.string.withoutTag)
        val el2 = getString(R.string.greenTag)
        val el3 = getString(R.string.yellowTag)
        val el4 = getString(R.string.redTag)

        val listOfTagsDescriptions: MutableList<String> = mutableListOf(el1, el2, el3, el4)

        val tagList = arrayListOf(
            selectTagData("#D3D3D3", el1),
            selectTagData("#1FB525", el2),
            selectTagData("#FFC107", el3),
            selectTagData("#FF0000", el4)
        )

        spinTagAdapter = selectTagAdapter(this, tagList)
        tagSpinner.adapter = spinTagAdapter

        recyclerViewAdapter = AddedTaskItemAdapter(this, addTaskList)
        tasksRecycler.layoutManager = LinearLayoutManager(this)
        tasksRecycler.adapter = recyclerViewAdapter

        backToMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        taskDescription.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    val symCount = s.length
                    val symCountText = s.length.toString()
                    val sym = getString(R.string.symbols)
                    descriptionSymCounter.text = "$sym: $symCountText/2000"

                    if (symCount == 2000) {
                        descriptionSymCounter.setTextColor(Color.parseColor("#FFFF0000"))
                    } else if (symCount >= 1000) {
                        descriptionSymCounter.setTextColor(Color.parseColor("#FFD400"))
                    } else {
                        descriptionSymCounter.setTextColor(Color.parseColor("#4CAF50"))
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        addTaskContent.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (source[i] == ',') {
                    return@InputFilter ""
                }
            }
            null
        })

        var gateKeyValue: String
        var gateKeyValueToSave: String
        val tasks: MutableList<String> = mutableListOf()
        val checked: MutableList<String> = mutableListOf()

        fun getDataAndCreate() {
            val database1 = taskListHeadDBHelper.writableDatabase
            val database2 = tasksDBHelper.writableDatabase
            val id = (1000..10000).random().toString()
            val name = taskName.text.toString()
            val description = taskDescription.text.toString()
            val allTasks = recyclerViewAdapter.itemCount
            val tasksCompleted = 0
            val tagPos = tagSpinner.selectedItemPosition
            val tag = listOfTagsDescriptions[tagPos]
            val isCompleted = "No"

            val year = datePicker.year
            val day = datePicker.dayOfMonth
            val month = datePicker.month + 1

            val currentDate = Date()
            val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(currentDate)

            val dateCreation = formattedDate.toString()
            val dateMustCompleted = "$day/$month/$year"

            val nums = (10000..100000).random().toString()
            val gateKey = "gateKey$nums"
            gateKeyValue = gateKey
            gateKeyValueToSave = gateKey

            val date = LocalDate.of(year, month, day)
            val dateNow = LocalDate.now()

            if (name.isNotEmpty() && description.isNotEmpty() && allTasks != 0 && !date.isBefore(dateNow) && dateMustCompleted != dateCreation) {

                val values = ContentValues().apply {
                    put("Id", id)
                    put("TaskName", name)
                    put("TaskDescription", description)
                    put("AllTasks", allTasks)
                    put("CompletedTasks", tasksCompleted)
                    put("DateNow", dateCreation)
                    put("MustCompleted", dateMustCompleted)
                    put("GateKey", gateKeyValue)
                    put("TagType", tag)
                    put("IsCompleted", isCompleted)
                }
                database1.insert("TaskHeadsTable", null, values)
                database1.close()

                val tasksString = tasks.joinToString(",")
                val isChecked = checked.joinToString(",")

                val tasksContent = ContentValues().apply {
                    put("TaskContent", tasksString)
                    put("IsChecked", isChecked)
                    put("GateKeyToOpen", gateKeyValueToSave)
                }
                database2.insert("TasksTable", null, tasksContent)
                database2.close()

                val created = getString(R.string.listCreated)
                Toast.makeText(this, created, Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                val builder = AlertDialog.Builder(this)
                val error = getString(R.string.insufficientData)
                builder.setTitle(error)
                val inflater = layoutInflater
                val dialog = inflater.inflate(R.layout.alert_insufficient_data, null)
                val alertView = dialog.findViewById<TextView>(R.id.alertDataView)
                builder.setView(dialog)

                var alertMessage = ""

                val err1 = getString(R.string.err1)
                val err2 = getString(R.string.err2)
                val err3 = getString(R.string.err3)
                val err4 = getString(R.string.err4)
                val err5 = getString(R.string.err5)

                if (name.isEmpty()) {
                    alertMessage += "$err1\n"
                    taskName.setBackgroundColor(Color.RED)
                } else {
                    taskName.setBackgroundColor(Color.TRANSPARENT)
                }

                if (description.isEmpty()) {
                    alertMessage += "$err2\n"
                    taskDescription.setBackgroundColor(Color.RED)
                } else {
                    taskDescription.setBackgroundColor(Color.TRANSPARENT)
                }

                if (allTasks == 0) {
                    alertMessage += "$err3\n"
                    tasksRecycler.setBackgroundColor(Color.RED)
                }

                if (dateMustCompleted == dateCreation) {
                    alertMessage += "$err4 (!$dateMustCompleted = $dateCreation!)"
                }

                if (date.isBefore(dateNow)) {
                    alertMessage += err5
                    datePicker.setBackgroundColor(Color.RED)
                } else {
                    datePicker.setBackgroundColor(Color.TRANSPARENT)
                }

                val message = getString(R.string.errorMessage)
                val remarks = getString(R.string.remarks)
                alertView.text = "$message\n $remarks\n $alertMessage"

                builder.setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()
            }
        }

        applyData.setOnClickListener {
            getDataAndCreate()
        }

        var count = 0

        fun addEditableTaskItem() {
            val text = addTaskContent.text.toString()

            if (text.isNotEmpty()) {
                addTaskList.add(AddedTaskItemData(text))
                recyclerViewAdapter.notifyDataSetChanged()
                addTaskContent.setBackgroundColor(Color.TRANSPARENT)
                tasksRecycler.setBackgroundColor(Color.TRANSPARENT)
                count++
                val added = getString(R.string.addedTasks2)
                itemCounter.text = "$added: $count"
                tasks.add(text)
                checked.add("false")
            } else {
                val empty = getString(R.string.notEmpty)
                val alertBar = Snackbar.make(findViewById(android.R.id.content), empty, Snackbar.LENGTH_INDEFINITE)
                    .setBackgroundTint(ContextCompat.getColor(this, R.color.danger))
                    .setTextColor(Color.WHITE)
                    .setDuration(1000)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                alertBar.show()
                addTaskContent.setBackgroundColor(Color.RED)
            }
        }

        addTask.setOnClickListener {
            addEditableTaskItem()
        }

        clearTaskList.setOnClickListener {
            addTaskList.clear()
            tasks.clear()
            recyclerViewAdapter.deleteAllItems()
            count = 0
            val added = getString(R.string.addedTasks2)
            val cleared = getString(R.string.listCleared)
            itemCounter.text = "$added: $count"
            Toast.makeText(this, cleared, Toast.LENGTH_SHORT).show()
        }
    }
}
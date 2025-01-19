package com.example.mytasks

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.GONE
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val dbTaskHeadHelper = TaskHeadDB(this)
    private val dbTasksHelper = TasksDB(this)

    private lateinit var menuFab: ExtendedFloatingActionButton
    private lateinit var createFab: FloatingActionButton
    private lateinit var deleteFab: FloatingActionButton
    private lateinit var valuesList: ArrayList<TaskListHeadData>
    private lateinit var searchFilteredList: ArrayList<TaskListHeadData>
    private lateinit var tasksView: RecyclerView
    private lateinit var recyclerViewAdapter: TaskListHeadAdapter
    private lateinit var allView: TextView
    private lateinit var completedView: TextView
    private lateinit var notCompletedView: TextView
    private lateinit var normalCount: TextView
    private lateinit var hardCount: TextView
    private lateinit var veryHardCount: TextView
    private lateinit var noData: ImageView
    private lateinit var noDataText: TextView
    private lateinit var openMenu: ImageButton
    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var text3: TextView
    private lateinit var searchString: SearchView

    private var isFabsVisible: Boolean? = null

    @SuppressLint("NotifyDataSetChanged", "CutPasteId", "MissingPermission", "ScheduleExactAlarm")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        valuesList = ArrayList()
        searchFilteredList = ArrayList()

        menuFab = findViewById(R.id.fabMenu)
        createFab = findViewById(R.id.fabCreate)
        deleteFab = findViewById(R.id.fabDelete)
        tasksView = findViewById(R.id.tasksView)
        allView = findViewById(R.id.allTasksView)
        completedView = findViewById(R.id.completedTasksView)
        notCompletedView = findViewById(R.id.notCompletedTasksView)
        normalCount = findViewById(R.id.tagNormalCount)
        hardCount = findViewById(R.id.tagHardCount)
        veryHardCount = findViewById(R.id.tagVeryHardCount)
        noData = findViewById(R.id.noTasksIcon)
        noDataText = findViewById(R.id.tasksText)
        openMenu = findViewById(R.id.btnMenu)
        text1 = findViewById(R.id.desc1)
        text2 = findViewById(R.id.desc2)
        text3 = findViewById(R.id.desc3)
        searchString = findViewById(R.id.searcher)

        recyclerViewAdapter = TaskListHeadAdapter(this, valuesList)
        tasksView.layoutManager = LinearLayoutManager(this)
        tasksView.adapter = recyclerViewAdapter

        sendNotificationPeriodicProcess()

        val mainHandler = Handler(Looper.getMainLooper())

        isFabsVisible = false

        menuFab.shrink()

        createFab.visibility = GONE
        deleteFab.visibility = GONE
        text1.visibility = GONE
        text2.visibility = GONE
        text3.visibility = GONE

        menuFab.setOnClickListener {
            isFabsVisible = if (!isFabsVisible!!) {

                createFab.show()
                deleteFab.show()
                text1.visibility = View.VISIBLE
                text2.visibility = View.VISIBLE
                text3.visibility = View.VISIBLE

                menuFab.extend()

                true
            } else {

                createFab.hide()
                deleteFab.hide()
                text1.visibility = GONE
                text2.visibility = GONE
                text3.visibility = GONE

                menuFab.shrink()

                false
            }
        }

        createFab.setOnClickListener {
            val intent = Intent(this, CreateTaskListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        searchString.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterThis(newText)
                return true
            }

            @SuppressLint("SetTextI18n")
            private fun filterThis(query: String) {

                if (query.isEmpty()) {
                    recyclerViewAdapter.updateData(valuesList)
                    return
                }

                searchFilteredList.clear()

                valuesList.forEach {
                    if (it.task_list_name.contains(query, true)) {
                        searchFilteredList.add(it)
                    }
                }
                recyclerViewAdapter.updateData(searchFilteredList)
            }
        })

        fun dropDownMenu() {
            val menu = PopupMenu(this@MainActivity, openMenu)
            menu.menuInflater.inflate(R.menu.menu_items, menu.menu)
            menu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_quit -> {
                        val activity = MainActivity()
                        activity.finish()
                        System.exit(0)
                        true
                    }

                    R.id.menu_about -> {
                        val builder = AlertDialog.Builder(this)
                        val inflater = layoutInflater
                        val about = getString(R.string.about)
                        builder.setTitle(about)
                        val dialog = inflater.inflate(R.layout.about, null)
                        builder.setView(dialog)

                        val close = getString(R.string.close)
                        builder.setPositiveButton(close) {dialog, which ->
                            dialog.dismiss()
                        }

                        builder.show()
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
            menu.show()
        }

        openMenu.setOnClickListener {
            dropDownMenu()
        }

        val database1 = dbTaskHeadHelper.readableDatabase
        val projection = arrayOf("Id", "TaskName", "TaskDescription", "DateNow", "MustCompleted", "AllTasks", "CompletedTasks", "TagType", "IsCompleted", "RemindDate", "RemindTime")

        val cursor = database1.query("TaskHeadsTable", projection, null, null, null, null, null)

        var id: Int?
        var name: String?
        var descriptionTask: String?
        var allTasks: Int?
        var completedTasks: Int?
        var dateNow: String?
        var mustCompleted: String?
        var tagColor: String?
        var isCompleted: String?

        val tags: MutableList<String> = mutableListOf()
        val completed: MutableList<String> = mutableListOf()

        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow("Id"))
            name = cursor.getString(cursor.getColumnIndexOrThrow("TaskName"))
            descriptionTask = cursor.getString(cursor.getColumnIndexOrThrow("TaskDescription"))
            allTasks = cursor.getInt(cursor.getColumnIndexOrThrow("AllTasks"))
            completedTasks = cursor.getInt(cursor.getColumnIndexOrThrow("CompletedTasks"))
            dateNow = cursor.getString(cursor.getColumnIndexOrThrow("DateNow"))
            mustCompleted = cursor.getString(cursor.getColumnIndexOrThrow("MustCompleted"))
            tagColor = cursor.getString(cursor.getColumnIndexOrThrow("TagType"))
            isCompleted = cursor.getString(cursor.getColumnIndexOrThrow("IsCompleted"))

            tags.add(tagColor)
            completed.add(isCompleted)

            valuesList.add(TaskListHeadData(id, name, descriptionTask, allTasks, completedTasks, dateNow, mustCompleted, tagColor))
            recyclerViewAdapter.notifyDataSetChanged()
        }

        cursor.close()
        database1.close()

        mainHandler.post(object : Runnable {
            override fun run() {
                val itemCount = recyclerViewAdapter.itemCount.toString()
                allView.text = itemCount

                val itemsCount = recyclerViewAdapter.itemCount

                if (itemsCount == 0) {
                    noData.visibility = View.VISIBLE
                    noDataText.visibility = View.VISIBLE
                } else {
                    noData.visibility = GONE
                    noDataText.visibility = GONE
                }

                recyclerViewAdapter.updateTagListAndIsCompletedList(tags, completed)

                val el2 = getString(R.string.greenTag)
                val el3 = getString(R.string.yellowTag)
                val el4 = getString(R.string.redTag)

                val green = tags.count { it == el2 }.toString()
                val yellow = tags.count { it == el3 }.toString()
                val red = tags.count { it == el4 }.toString()

                val completedCount = completed.count { it == "Yes" }.toString()
                val notCompletedCount = completed.count { it == "No" }.toString()

                normalCount.text = green
                hardCount.text = yellow
                veryHardCount.text = red

                completedView.text = completedCount
                notCompletedView.text = notCompletedCount

                mainHandler.postDelayed(this, 1000)
            }
        })

        fun deleteAll() {
            val Database1 = dbTaskHeadHelper.writableDatabase
            val database2 = dbTasksHelper.writableDatabase
            val builder = AlertDialog.Builder(this)
            val delete = getString(R.string.wantDeleteAll)
            builder.setTitle(delete)
            val inflater = layoutInflater
            val dialog = inflater.inflate(R.layout.alert_delete_dialog, null)
            builder.setView(dialog)

            fun deleteEvent() {
                Database1.delete("TaskHeadsTable", null, null)
                database2.delete("TasksTable", null, null)
                valuesList.clear()
                searchFilteredList.clear()
                recyclerViewAdapter.notifyDataSetChanged()
                Database1.close()
                database2.close()

                tags.clear()
                completed.clear()

                normalCount.text = "0"
                hardCount.text = "0"
                veryHardCount.text = "0"

                completedView.text = "0"
                notCompletedView.text = "0"

                val allDeleted = getString(R.string.allDeleted)
                val alertBar = Snackbar.make(findViewById(android.R.id.content), "$allDeleted\uD83D\uDCC1❌", Snackbar.LENGTH_INDEFINITE)
                    .setBackgroundTint(ContextCompat.getColor(this, R.color.lime))
                    .setTextColor(Color.WHITE)
                    .setDuration(2000)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                alertBar.show()
            }

            val yes = getString(R.string.yes)
            val no = getString(R.string.no)

            builder.setPositiveButton(yes) { dialog, _ ->
                deleteEvent()
            }

            builder.setNegativeButton(no) { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }

        deleteFab.setOnClickListener {
            deleteAll()
        }
    }

    private fun sendNotificationPeriodicProcess() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val request = PeriodicWorkRequest.Builder(NotificationWorkManager::class.java, 6, TimeUnit.HOURS).setConstraints(constraints).addTag("workId").build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("workId", ExistingPeriodicWorkPolicy.KEEP, request)
    }
}
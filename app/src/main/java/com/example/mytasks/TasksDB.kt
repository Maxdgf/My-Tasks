package com.example.mytasks

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TasksDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TasksBox.db"
        const val DATABASE_VERSION = 4

        private const val TABLE_NAME = "TasksTable"
        private const val TASK_CONTENT = "TaskContent"
        private const val IS_CHECKED = "IsChecked"
        private const val GATE_KEY_TO_OPEN = "GateKeyToOpen"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($TASK_CONTENT TEXT, $IS_CHECKED TEXT, $GATE_KEY_TO_OPEN TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
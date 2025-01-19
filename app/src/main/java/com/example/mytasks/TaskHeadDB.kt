package com.example.mytasks

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskHeadDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TaskHeadsBox.db"
        const val DATABASE_VERSION = 4

        private const val TABLE_NAME = "TaskHeadsTable"
        private const val ID = "Id"
        private const val TASK_LIST_NAME = "TaskName"
        private const val TASK_LIST_CONTENT = "TaskDescription"
        private const val DATE_TODAY = "DateNow"
        private const val MUST_COMPLETED = "MustCompleted"
        private const val ALL_TASKS = "AllTasks"
        private const val COMPLETED_TASKS = "CompletedTasks"
        private const val GATE_KEY = "GateKey"
        private const val TAG_TYPE = "TagType"
        private const val IS_COMPLETED = "IsCompleted"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($ID INTEGER, $TASK_LIST_NAME TEXT, $TASK_LIST_CONTENT TEXT, $DATE_TODAY TEXT, $MUST_COMPLETED TEXT, $ALL_TASKS INTEGER, $COMPLETED_TASKS INTEGER, $GATE_KEY TEXT, $TAG_TYPE TEXT, $IS_COMPLETED TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
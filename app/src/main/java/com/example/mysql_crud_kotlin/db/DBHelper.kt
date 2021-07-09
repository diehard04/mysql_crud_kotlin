package com.example.mysql_crud_kotlin.db



import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mysql_crud_kotlin.utils.Utils
import com.phjethva.mysql_db_crud_kotlin.models.ModelTask
import java.util.*

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val totalTaskCount: Int
        get() {
            val countQuery = "SELECT  * FROM $TABLE_NAME"
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)
            val count = cursor.count
            cursor.close()
            return count
        }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DATABASE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, vOld: Int, vNew: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun createTask(msg: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, msg)
        values.put(COLUMN_TIME, Utils.getCurrentTime())
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun readAllTask(): List<ModelTask> {
        val tasks = ArrayList<ModelTask>()
        val selectQuery = "SELECT  * FROM $TABLE_NAME ORDER BY $COLUMN_TIME ASC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val task = ModelTask()
                task.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                task.taskName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                task.taskDateTime = cursor.getString(cursor.getColumnIndex(COLUMN_TIME))
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        db.close()
        return tasks
    }

    /**
     * sorting in descending order
     */
    fun readAllTaskByDescending(): List<ModelTask> {
        val tasks = ArrayList<ModelTask>()
        val selectQuery = "SELECT  * FROM $TABLE_NAME ORDER BY $COLUMN_TIME DESC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val task = ModelTask()
                task.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                task.taskName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                task.taskDateTime = cursor.getString(cursor.getColumnIndex(COLUMN_TIME))
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        db.close()
        return tasks
    }

    fun readTaskByID(id: Long): ModelTask {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_TIME),
            "$COLUMN_ID=?",
            arrayOf(id.toString()), null, null, null, null
        )
        cursor?.moveToFirst()
        val note = ModelTask(
            cursor!!.getInt(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getString(cursor.getColumnIndex(COLUMN_TIME))
        )
        cursor.close()
        return note
    }

    fun updateTask(id: Long, msg: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, msg)
        return db.update(
            TABLE_NAME, values, "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }



    fun deleteTask(id: Long) {
        val db = this.writableDatabase
        db.delete(
            TABLE_NAME, "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
    }

    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "db_task"

        val TABLE_NAME = "task_list"
        val COLUMN_ID = "id"
        val COLUMN_NAME = "name"
        val COLUMN_TIME = "time"

        private val DATABASE_CREATE = ("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_TIME + " TEXT"
                + ")")
    }

}
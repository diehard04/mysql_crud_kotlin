package com.example.mysql_crud_kotlin.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysql_crud_kotlin.R
import com.example.mysql_crud_kotlin.db.DBHelper
import com.example.mysql_crud_kotlin.utils.Utils
import com.phjethva.mysql_crud_kotlin.adapters.AdapterTask
import com.phjethva.mysql_db_crud_kotlin.models.ModelTask
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), View.OnClickListener, AdapterTask.ItemClick {


    internal lateinit var btnAddNewTsk: Button
    private var tasks: List<ModelTask> = ArrayList()
    internal lateinit var adapterTask: AdapterTask
    internal lateinit var recyclerViewTask: RecyclerView
    internal lateinit var sortbtn: Button
    private var dbHelper: DBHelper? = null
    private var Ascending = true
    private var isAscending:Boolean = true

    companion object {

        private val tasks = ArrayList<String>()

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }


        private val INSERT_TASK = 1
        private val UPDATE_TASK = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.setView()
        this.setClickListen()

        dbHelper = DBHelper(this)

        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerViewTask!!.layoutManager = layoutManager

        tasks = dbHelper!!.readAllTask()
        adapterTask = AdapterTask(this, tasks)
        recyclerViewTask!!.adapter = adapterTask

        readDataBase()
    }


    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.btn_add_new_task -> dialogTaskAddNew(INSERT_TASK, 0, "")

            // added sorting button
            R.id.btn_sort -> dialogSorting()
        }
    }

    /**
     * dialog interface for selection of sorting
     */
    private fun dialogSorting() {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        isAscending = false
                        tasks = dbHelper!!.readAllTaskByDescending()
                        adapterTask!!.notifyData(tasks)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        isAscending = true
                        tasks = dbHelper!!.readAllTask()
                        adapterTask!!.notifyData(tasks)
                    }
                }
            }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Please select?")
            .setPositiveButton("Descending with time", dialogClickListener)
            .setNegativeButton("Ascending with time", dialogClickListener).show()

    }

    private fun setView() {
        btnAddNewTsk = findViewById(R.id.btn_add_new_task)
        recyclerViewTask = findViewById(R.id.recycle_view_task)
        recyclerViewTask.setLayoutManager(LinearLayoutManager(this))
        sortbtn = findViewById(R.id.btn_sort)


    }


    private fun setClickListen() {
        btnAddNewTsk!!.setOnClickListener(this)
        sortbtn.setOnClickListener(this)

    }

    private fun readDataBase() {
        if (isAscending) {
            tasks = dbHelper!!.readAllTask()
            adapterTask!!.notifyData(tasks)
        } else{
            tasks = dbHelper!!.readAllTaskByDescending()
            adapterTask!!.notifyData(tasks)
        }
    }

    private fun dialogTaskInfo(task: ModelTask) {
        val dialog = Dialog(this, R.style.DialogFullScreen)
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window!!.setDimAmount(0.5f)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_task_info)

        val tvInfoDate = dialog.findViewById<TextView>(R.id.tv_info_date)
        val tvInfoName = dialog.findViewById<TextView>(R.id.tv_info_name)
        tvInfoDate.text = "Date: " + Utils.formatDateTime(task.taskDateTime!!)
        tvInfoName.text = task.taskName
        val btnInfoClose = dialog.findViewById<Button>(R.id.btn_info_close)
        btnInfoClose.setOnClickListener { dialog.dismiss() }


        dialog.show()
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window!!.setDimAmount(0.5f)
        dialog.window!!.attributes = lp

    }

    private fun dialogTaskAddNew(type: Int, id: Int, msg: String) {
        val dialog = Dialog(this, R.style.DialogFullScreen)
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window!!.setDimAmount(0.5f)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.BOTTOM
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_task_add_new)

        val etAddNewName = dialog.findViewById<EditText>(R.id.et_add_new_name)
        val btnAddNewClose = dialog.findViewById<Button>(R.id.btn_add_new_close)
        val btnAddNewAdd = dialog.findViewById<Button>(R.id.btn_add_new_add)


        if (type == UPDATE_TASK) {
            etAddNewName.setText(msg)
            btnAddNewAdd.text = getString(R.string.update)
        } else {
            etAddNewName.setText("")
            btnAddNewAdd.text = getString(R.string.add)
        }


        btnAddNewAdd.setOnClickListener {
            if (type == UPDATE_TASK) {
                val message = etAddNewName.text.toString().trim { it <= ' ' }
                dbHelper!!.updateTask(id.toLong(), message)
                readDataBase()
            } else if (type == INSERT_TASK) {
                val message = etAddNewName.text.toString().trim { it <= ' ' }
                dbHelper!!.createTask(message)
                readDataBase()
            }
            dialog.dismiss()
        }

        btnAddNewClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window!!.setDimAmount(0.5f)
        dialog.window!!.attributes = lp

    }

    override fun callbackItemClick(task: ModelTask, imageButtonMenu: ImageButton) {
        val popup = PopupMenu(this@MainActivity, imageButtonMenu)
        popup.menuInflater.inflate(R.menu.menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.info -> dialogTaskInfo(task)
                R.id.update -> dialogTaskAddNew(UPDATE_TASK, task.id, task.taskName!!)

                R.id.delete -> {
                    dbHelper!!.deleteTask(task.id.toLong())
                    readDataBase()
                }
            }
            true
        }
        popup.show()
    }


}
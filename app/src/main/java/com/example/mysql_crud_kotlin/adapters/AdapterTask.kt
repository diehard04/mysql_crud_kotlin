package com.phjethva.mysql_crud_kotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysql_crud_kotlin.R
import com.example.mysql_crud_kotlin.utils.Utils
import com.phjethva.mysql_db_crud_kotlin.models.ModelTask


class AdapterTask(var click: ItemClick?, private var tasks: List<ModelTask>?) :

    RecyclerView.Adapter<AdapterTask.TaskViewHolder>() {



    interface ItemClick {
        fun callbackItemClick(task: ModelTask, imageButtonMenu: ImageButton)
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var taskName: TextView
        internal var taskDateTime: TextView
        internal var imageButtonMenu: ImageButton

        init {
            taskName = view.findViewById(R.id.textview_task_name)
            taskDateTime = view.findViewById(R.id.textview_task_date_time)
            imageButtonMenu = view.findViewById(R.id.imagebutton_menu)
        }

        fun bind(task: ModelTask, click: ItemClick?) {
            imageButtonMenu.setOnClickListener(MenuButtonClick(task, imageButtonMenu))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks!![position]
        holder.taskName.text = task.taskName
        holder.taskDateTime.text = Utils.formatDateTime(task.taskDateTime!!)
        holder.bind(task, click)

    }

    fun notifyData(tasks: List<ModelTask>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return tasks!!.size
    }

    internal inner class MenuButtonClick(var task: ModelTask, var imageButtonMenu: ImageButton) : View.OnClickListener {

        override fun onClick(v: View) {
            if (click != null) {
                click!!.callbackItemClick(task, imageButtonMenu)
            }
        }
    }

}
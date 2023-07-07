package com.nickytoolchick.recyclerviewpractice.model

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.nickytoolchick.recyclerviewpractice.R
import com.nickytoolchick.recyclerviewpractice.TaskActionListener
import com.nickytoolchick.recyclerviewpractice.databinding.LayoutTaskBinding
import java.time.format.DateTimeFormatter

class TaskAdapter(tasks: MutableList<Task>, private val actionListener: TaskActionListener) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(), OnClickListener,
    CompoundButton.OnCheckedChangeListener {
    lateinit var binding: LayoutTaskBinding

    class TaskViewHolder(val binding: LayoutTaskBinding) : RecyclerView.ViewHolder(binding.root)

    private var _tasks: MutableList<Task> = tasks
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = LayoutTaskBinding.inflate(inflater, parent, false)

        binding.editTaskButton.setOnClickListener(this)
        binding.deleteTaskButton.setOnClickListener(this)
        binding.shareTaskButton.setOnClickListener(this)
        binding.isTaskDoneCheckBox.setOnCheckedChangeListener(this)

        binding.root.setOnClickListener(this)

        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int = _tasks.count()

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = _tasks[position]
        with(holder.binding) {
            editTaskButton.tag = task
            deleteTaskButton.tag = task
            shareTaskButton.tag = task
            isTaskDoneCheckBox.tag = task

            taskNameTV.text = task.name
            taskDescriptionTV.text = task.description
            taskDeadlineTV.visibility = if (task.hasDeadline) View.VISIBLE else View.GONE
            if (task.deadline != null) {
                val deadlineString = "Deadline ${task.deadline!!.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}"
                taskDeadlineTV.text = deadlineString
            }

            isTaskDoneCheckBox.isChecked = task.isCompleted
        }
    }

    override fun onClick(v: View?) {
        val task = v?.tag as? Task
        task?.let {
            when (v?.id) {
                R.id.editTaskButton -> {
                    actionListener.onEditTask(it)
                }
                R.id.deleteTaskButton -> {
                    actionListener.onDeleteTask(it)
//                    _tasks.remove(it)
                    notifyDataSetChanged()
                }
                R.id.shareTaskButton -> {
                    actionListener.onShareTask(it)
                }
            }
        }
    }



    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        val task = buttonView!!.tag as Task
        actionListener.onCompleteTask(task)
        buttonView.isChecked = task.isCompleted
    }

    fun notifyChanges() {
        notifyDataSetChanged()
    }
}
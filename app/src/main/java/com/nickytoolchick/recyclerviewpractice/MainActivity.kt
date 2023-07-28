package com.nickytoolchick.recyclerviewpractice

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.nickytoolchick.recyclerviewpractice.databinding.ActivityMainBinding
import com.nickytoolchick.recyclerviewpractice.model.Task
import com.nickytoolchick.recyclerviewpractice.model.TaskAdapter
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TaskAdapter
    private val tasks = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionListener = object : TaskActionListener {
            override fun onEditTask(task: Task) {
                createOrEditTask(task)
            }

            override fun onDeleteTask(task: Task) {
                val dialogLayout = layoutInflater.inflate(R.layout.layout_dialog_delete_task, null)
                val dialogTextView = dialogLayout.findViewById<TextView>(R.id.text_delete_task)
                val submitButton = dialogLayout.findViewById<Button>(R.id.submit_button)
                val cancelButton = dialogLayout.findViewById<Button>(R.id.cancel_button)

                val dialog = AlertDialog.Builder(this@MainActivity)
                    .setView(dialogLayout)
                    .create()

                val dialogText = "Do you really want to delete task\n${task.name}?"
                dialogTextView.text = dialogText
                submitButton.setOnClickListener {
                    tasks.remove(task)
                    showToast("Delete task")
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }

            override fun onShareTask(task: Task) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("task", task.toString())
                clipboard.setPrimaryClip(clip)

                showToast("Task data copied to the clipboard")
            }

            override fun onCompleteTask(task: Task) {
                showToast("Toggle task completion")
                Log.d("task", task.isCompleted.toString())
            }
        }

        adapter = TaskAdapter(tasks, actionListener)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        binding.addTaskButton.setOnClickListener {

            createOrEditTask(null)
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    // TODO: data validation
    // TODO: do something to display the isCompleted checkBox state according to the state received from edit dialog immediately
    private fun createOrEditTask(task: Task?) {
        val dialogLayout = layoutInflater.inflate(R.layout.layout_dialog_edit_task, null)
        val dialogTextView = dialogLayout.findViewById<TextView>(R.id.dialog_title)
        val submitButton = dialogLayout.findViewById<Button>(R.id.submit_button)
        val cancelButton = dialogLayout.findViewById<Button>(R.id.cancel_button)
        val nameInput = dialogLayout.findViewById<EditText>(R.id.name_input)
        val descriptionInput = dialogLayout.findViewById<EditText>(R.id.description_input)
        val hasDeadlineCheckBox = dialogLayout.findViewById<CheckBox>(R.id.has_deadline_checkBox)
        val deadlineEditText = dialogLayout.findViewById<EditText>(R.id.deadline_textView)
        val isCompletedCheckBox = dialogLayout.findViewById<CheckBox>(R.id.is_completed_checkBox)

        val isTaskNull = task == null

        if (!isTaskNull) {
            populateViews(task!!, nameInput, descriptionInput, hasDeadlineCheckBox, deadlineEditText, isCompletedCheckBox)
        }

        setDeadlineCheckBoxListener(hasDeadlineCheckBox, deadlineEditText)

        val dialog = createAlertDialog(dialogLayout)
        setDialogTitle(dialogTextView, isTaskNull, task)

        submitButton.setOnClickListener {
            val newTask = createNewTask(nameInput, descriptionInput, hasDeadlineCheckBox, deadlineEditText, isCompletedCheckBox)

            if (isTaskNull) {
                addTask(newTask)
            } else {
                editTask(newTask, task!!)
            }

            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun populateViews(
        task: Task,
        nameInput: EditText,
        descriptionInput: EditText,
        hasDeadlineCheckBox: CheckBox,
        deadlineEditText: EditText,
        isCompletedCheckBox: CheckBox
    ) {
        nameInput.setText(task.name)
        descriptionInput.setText(task.description)
        hasDeadlineCheckBox.isChecked = task.hasDeadline
        deadlineEditText.visibility = if (task.hasDeadline) View.VISIBLE else View.GONE
        if (task.hasDeadline) {
            deadlineEditText.visibility = View.VISIBLE
            deadlineEditText.setText(task.deadline!!.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
        } else {
            deadlineEditText.visibility = View.GONE
        }
        isCompletedCheckBox.isChecked = task.isCompleted
    }

    private fun setDeadlineCheckBoxListener(
        hasDeadlineCheckBox: CheckBox,
        deadlineEditText: EditText
    ) {
        hasDeadlineCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            deadlineEditText.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }

    private fun createAlertDialog(dialogLayout: View): AlertDialog {
        return AlertDialog.Builder(this@MainActivity)
            .setView(dialogLayout)
            .create()
    }

    private fun setDialogTitle(
        dialogTextView: TextView,
        isTaskNull: Boolean,
        task: Task?
    ) {
        val dialogText = if (isTaskNull) "Create new task" else "Edit task ${task!!.name}"
        dialogTextView.text = dialogText
    }

    private fun createNewTask(
        nameInput: EditText,
        descriptionInput: EditText,
        hasDeadlineCheckBox: CheckBox,
        deadlineEditText: EditText,
        isCompletedCheckBox: CheckBox
    ): Task {
        var deadline: LocalDateTime? = null
        if (hasDeadlineCheckBox.isChecked) {
            val splitDate = deadlineEditText.text.toString()
                .split(".")
                .map { text -> text.toInt() }
            deadline = LocalDateTime.of(splitDate[2], Month.of(splitDate[1]), splitDate[0], 0, 0)
        }

        val isCompleted = isCompletedCheckBox.isChecked

        return Task(
            id = Random.nextInt(),
            name = nameInput.text.toString(),
            description = descriptionInput.text.toString(),
            hasDeadline = hasDeadlineCheckBox.isChecked,
            deadline = deadline,
            isCompleted = isCompleted
        )
    }


    private fun addTask(newTask: Task) {
        tasks.add(newTask)
        adapter.notifyDataSetChanged()
        showToast("Add task")
    }

    private fun editTask(newTask: Task, task: Task) {
        val taskIndex = tasks.indexOf(task)
        tasks[taskIndex] = newTask
        adapter.notifyDataSetChanged()
        showToast("Edit task")
    }

}
package com.nickytoolchick.recyclerviewpractice

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.nickytoolchick.recyclerviewpractice.databinding.ActivityMainBinding
import com.nickytoolchick.recyclerviewpractice.model.Task
import com.nickytoolchick.recyclerviewpractice.model.TaskAdapter
import java.time.LocalDateTime
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tasks = mutableListOf(
            Task(
                id = 1,
                name = "Clean the room",
                description = "Make the room clean, take out the rubbish",
                hasDeadline = true,
                deadline = LocalDateTime.now(),
                isCompleted = false
            )
        )

        val actionListener = object : TaskActionListener {
            override fun onEditTask(task: Task) {
                showToast("Edit task")
                // TODO: custom dialog where the task data can be edited
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
                    adapter.notifyChanges()
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
                task.isCompleted = !task.isCompleted
                showToast("Toggle task completion")
            }
        }

        adapter = TaskAdapter(tasks, actionListener)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        binding.addTaskButton.setOnClickListener {

            // TODO: implement task creation through a dialog

            tasks.add(Task(
                id = Random.nextInt(),
                name = "Test task",
                description = "Something has to be done",
                hasDeadline = true,
                deadline = LocalDateTime.now(),
                isCompleted = false
            ))
            adapter.notifyChanges()
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
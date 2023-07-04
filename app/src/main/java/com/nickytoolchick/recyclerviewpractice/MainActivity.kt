package com.nickytoolchick.recyclerviewpractice

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
                tasks.remove(task)
                // TODO: custom dialog asking if the user really wants to delete the task
                showToast("Delete task")
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
            tasks.add(Task(
                id = Random.nextInt(),
                name = "Test task",
                description = "Something has to be done",
                hasDeadline = true,
                deadline = LocalDateTime.now(),
                isCompleted = false
            ))
            adapter.notifyAddedTask()
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
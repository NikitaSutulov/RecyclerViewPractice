package com.nickytoolchick.recyclerviewpractice

import com.nickytoolchick.recyclerviewpractice.model.Task

interface TaskActionListener {
    fun onEditTask(task: Task)
    fun onDeleteTask(task: Task)
    fun onShareTask(task: Task)
    fun onCompleteTask(task: Task)
}
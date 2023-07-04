package com.nickytoolchick.recyclerviewpractice.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Task(
    val id: Int,
    var name: String,
    var description: String,
    var hasDeadline: Boolean,
    var deadline: LocalDateTime?,
    var isCompleted: Boolean
) {
    override fun toString(): String {
        return "$name\n" +
                "$description\n" +
                if (hasDeadline) "Deadline: ${deadline!!.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}" else ""
    }
}
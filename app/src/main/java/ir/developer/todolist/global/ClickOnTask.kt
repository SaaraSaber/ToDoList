package ir.developer.todolist.global

import android.widget.CheckBox

interface ClickOnTask {
    fun clickOnTask(index: Int,checkBox: CheckBox)
}
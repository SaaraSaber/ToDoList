package ir.developer.todolist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.developer.todolist.databinding.LayoutRecyclerViewTaskBinding
import ir.developer.todolist.datamodel.TaskModel
import ir.developer.todolist.global.ClickOnTask

class TaskAdapter(private val onClick: ClickOnTask) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    private lateinit var binding: LayoutRecyclerViewTaskBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun bind(taskModel: TaskModel) {
            binding.apply {
                textTask.text = taskModel.task
            }
        }

        var text = binding.textTask
        val checkBox = binding.checkboxTask
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LayoutRecyclerViewTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder()
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val data = holder.bind(differ.currentList[position])


        holder.checkBox.setOnClickListener {
            onClick.clickOnTask(
                holder.adapterPosition,
                holder.checkBox
            )
        }

    }

    private val differCallback = object : DiffUtil.ItemCallback<TaskModel>() {
        override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
            return oldItem.task == newItem.task
        }

        override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)
}
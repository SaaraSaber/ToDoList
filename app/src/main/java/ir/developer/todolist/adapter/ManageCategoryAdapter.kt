package ir.developer.todolist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.developer.todolist.databinding.LayoutRecyclerViewManageCategoryBinding
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.global.ClickOnCategory

class ManageCategoryAdapter(private val clickOnCategory: ClickOnCategory) :
    RecyclerView.Adapter<ManageCategoryAdapter.ViewHolder>() {
    private lateinit var binding: LayoutRecyclerViewManageCategoryBinding

    inner class ViewHolder() : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: TabModel) {
            binding.apply {
                textCategory.text = category.name
            }
        }

        var btnDelete = binding.btnDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LayoutRecyclerViewManageCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder()
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
        val data = differ.currentList[holder.adapterPosition]


        holder.btnDelete.setOnClickListener {
            clickOnCategory.clickOnTab(
                data.id,
                holder.adapterPosition,
                "delete"
            )
        }

    }

    private val differCallback = object : DiffUtil.ItemCallback<TabModel>() {
        override fun areItemsTheSame(oldItem: TabModel, newItem: TabModel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: TabModel, newItem: TabModel): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)
}
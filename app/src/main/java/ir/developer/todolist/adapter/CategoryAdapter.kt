package ir.developer.todolist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.developer.todolist.R
import ir.developer.todolist.databinding.LayoutRecyclerViewCategoryBinding
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.global.ClickOnCategory

class CategoryAdapter(
    private val list: ArrayList<TabModel>,
    private val context: Context,
    private val onClick: ClickOnCategory
) :

    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private lateinit var binding: LayoutRecyclerViewCategoryBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        var text = binding.textCategory
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LayoutRecyclerViewCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder()
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[holder.adapterPosition]

        holder.text.text = data.name

        if (data.isSelected) {
            holder.text.setTextColor(context.getColor(R.color.underwaterMoonlight))
        } else {
            holder.text.setTextColor(context.getColor(R.color.black))
        }

        holder.itemView.setOnClickListener {
            onClick.clickOnTab(data.id, holder.adapterPosition,data.name)
        }
    }

}
package ir.developer.todolist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.developer.todolist.R
import ir.developer.todolist.databinding.LayoutRecyclerViewTabBinding
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.global.ClickOnTab

class TabAdapter(
    private val list: ArrayList<TabModel>,
    private val context: Context,
    private val onClick: ClickOnTab
) :
    RecyclerView.Adapter<TabAdapter.ViewHolder>() {
    private lateinit var binding: LayoutRecyclerViewTabBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        var text = binding.textTab
        val layout = binding.layoutTab
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LayoutRecyclerViewTabBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder()
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[holder.adapterPosition]

        holder.text.text = data.name
        if (data.isSelected) {
//            holder.text.textColors =context.resources.getColor(R.color.gray)
            holder.layout.background =
                context.getDrawable(R.drawable.simple_background_recycler_selected_tab)
        } else {
            //            holder.text.textColors =context.resources.getColor(R.color.white)
            holder.layout.background =
                context.getDrawable(R.drawable.simple_background_recycler_unselected_tab)
        }

        holder.layout.setOnClickListener {
            onClick.clickOnTab(holder.adapterPosition + 1, data.name)
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
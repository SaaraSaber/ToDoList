package ir.developer.todolist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.developer.todolist.databinding.LayoutRecyclerViewManageCategoryBinding

class ManageCategoryAdapter:RecyclerView.Adapter<ManageCategoryAdapter.ViewHolder>() {
    private lateinit var binding:LayoutRecyclerViewManageCategoryBinding

    inner class ViewHolder():RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}
package ir.developer.todolist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import ir.developer.todolist.adapter.TabAdapter
import ir.developer.todolist.database.AppDataBase
import ir.developer.todolist.databinding.FragmentHomeBinding
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.global.ClickOnTab

class HomeFragment : Fragment(), ClickOnTab {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterTab: TabAdapter
    private lateinit var listTab: ArrayList<TabModel>
    private lateinit var dataBase: AppDataBase
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataToRecyclerViewTab()
    }

    private fun setDataToRecyclerViewTab() {
        setDataTab()
        binding.recyclerTabs.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterTab
        }
    }

    private fun setDataTab() {
        setDataListTab()
        adapterTab = TabAdapter(list = listTab, context = requireActivity(), onClick = this)
    }

    private fun setDataListTab() {
        dataBase = AppDataBase.getDatabase(requireContext())
        val readData = dataBase.tab().readTabs()
        listTab = ArrayList()

        readData.forEach {
            listTab.add(it)
        }
    }

    override fun clickOnTab(index: Int, name: String) {
        listTab.forEach {
            it.isSelected = it.id == index
        }
        adapterTab.notifyDataSetChanged()
    }
}
package ir.developer.todolist.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import ir.developer.todolist.R
import ir.developer.todolist.adapter.TaskAdapter
import ir.developer.todolist.database.AppDataBase
import ir.developer.todolist.databinding.FragmentSearchBinding
import ir.developer.todolist.datamodel.TaskModel
import ir.developer.todolist.global.ClickOnTask
import java.util.Locale

class SearchFragment : Fragment(), ClickOnTask {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var listTask: ArrayList<TaskModel>
    private lateinit var dataBase: AppDataBase
    private lateinit var adapterTask: TaskAdapter
    private lateinit var dialogQuestion: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBase = AppDataBase.getDatabase(requireActivity())

        readDataTask()
        clickOnSearchView()

    }

    private fun clickOnSearchView() {
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return false
            }
        })
    }

    private fun filterList(newText: String?) {
        if (newText != null) {
            val filterList = ArrayList<TaskModel>()
            for (i in listTask) {
                if (i.task.lowercase(Locale.ROOT).contains(newText)) {
                    filterList.add(i)
                }
            }

            if (filterList.isEmpty()) {
                binding.textNoData.visibility = View.VISIBLE
                initRecyclerViewResultTask()
                adapterTask.differ.submitList(filterList)
            } else {
                binding.textNoData.visibility = View.GONE
                initRecyclerViewResultTask()
                adapterTask.differ.submitList(filterList)
                adapterTask.setFilteredList(filterList)
            }
        }
    }


    private fun readDataTask() {
        listTask = ArrayList()
        val task = dataBase.task().readTasks()

        if (task != null) {

            task.forEach {
                listTask.add(it)
            }

            initRecyclerViewResultTask()
            adapterTask.differ.submitList(listTask)
        }
        if (listTask.size != 0) binding.imgEmptyList.visibility = View.GONE
    }

    private fun initRecyclerViewResultTask() {
        adapterTask = TaskAdapter(this)
        binding.recyclerViewResultTask.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterTask
        }
    }

    override fun clickOnTask(index: Int, checkBox: CheckBox) {
        if (listTask.size != 0) {
            dialogQuestion(index, checkBox)
        }
    }

    private fun dialogQuestion(
        index: Int,
        checkBox: CheckBox
    ) {
        dialogQuestion = Dialog(requireContext())
        dialogQuestion.apply {
            setContentView(R.layout.layout_dialog_question)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setGravity(Gravity.CENTER)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val lp = window!!.attributes
            lp.dimAmount = 0.7f

            val btnOk = findViewById<MaterialButton>(R.id.btn_ok)
            val btnCansel = findViewById<MaterialButton>(R.id.btn_cansel)
            btnOk.setOnClickListener {
                dismiss()

                deleteTask(index)
            }
            btnCansel.setOnClickListener {
                checkBox.isChecked = false
                dismiss()
            }
            setOnCancelListener {
                checkBox.isChecked = false
            }
            show()
        }
    }

    private fun deleteTask(index: Int) {
        dataBase.task().deleteTask(
            TaskModel(
                listTask[index].id,
                listTask[index].task,
                listTask[index].category
            )
        )
        listTask.removeAt(index)
        adapterTask.differ.submitList(listTask)
        adapterTask.notifyItemRemoved(index)

        if (listTask.size == 0)
            binding.imgEmptyList.visibility = View.VISIBLE
    }
}
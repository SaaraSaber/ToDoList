package ir.developer.todolist.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ir.developer.todolist.R
import ir.developer.todolist.adapter.CategoryAdapter
import ir.developer.todolist.adapter.TabAdapter
import ir.developer.todolist.adapter.TaskAdapter
import ir.developer.todolist.database.AppDataBase
import ir.developer.todolist.databinding.FragmentHomeBinding
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.datamodel.TaskModel
import ir.developer.todolist.global.ClickOnCategory
import ir.developer.todolist.global.ClickOnTab
import ir.developer.todolist.global.ClickOnTask

@SuppressLint("MissingInflatedId")
class HomeFragment : Fragment(), ClickOnTab, ClickOnTask {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterTab: TabAdapter
    private lateinit var adapterCategory: CategoryAdapter
    private lateinit var listTab: ArrayList<TabModel>
    private lateinit var listTask: ArrayList<TaskModel>
    private lateinit var dataBase: AppDataBase
    private lateinit var dialogAddTask: Dialog
    private lateinit var dialogQuestion: Dialog
    private lateinit var dialogCategory: Dialog
    private val adapterTasks by lazy { TaskAdapter(this, requireActivity()) }

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

        dataBase = AppDataBase.getDatabase(requireActivity())

        setDataToRecyclerViewTab()
        readDataTask()

        binding.btnMore.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment2_to_categoryFragment)
        }

        binding.btnAddTask.setOnClickListener { dialogAddTask() }
    }

    private fun dialogQuestion(index: Int, checkBox: CheckBox) {
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
                dataBase.task().deleteTask(
                    TaskModel(
                        listTask[index].id,
                        listTask[index].task,
                        listTask[index].category,
                        listTask[index].isDoneTask
                    )
                )
                listTask.removeAt(index)
                adapterTasks.differ.submitList(listTask)
                adapterTasks.notifyItemRemoved(index)
            }
            btnCansel.setOnClickListener {
                checkBox.isChecked = false
                dismiss()
            }

            show()
        }
    }

    private fun readDataTask() {
        listTask = ArrayList()
        val task = dataBase.task().readTasks()

        if (task != null) {

            task.forEach {
                listTask.add(it)
            }
            initRecyclerViewTasks()
            adapterTasks.differ.submitList(listTask)
        }
    }

    private fun initRecyclerViewTasks() {
        binding.recyclerTasks.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterTasks
        }
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
        val readData = dataBase.tab().readTabs()
        listTab = ArrayList()

        readData.forEach {
            listTab.add(it)
        }
    }

    private lateinit var editTextTask: EditText
    private lateinit var btnCategory: TextView

    private fun dialogAddTask() {
        dialogAddTask = Dialog(requireContext())
        dialogAddTask.apply {
            setContentView(R.layout.layout_dialog_add_task)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setGravity(Gravity.BOTTOM)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val lp = window!!.attributes
            lp.dimAmount = 0.7f

            val btnSend = findViewById<View>(R.id.btn_send)
            btnCategory = findViewById(R.id.btn_category)
            editTextTask = findViewById(R.id.edittext_enter_task)
            val btnAlarm = findViewById<View>(R.id.btn_alarm)
            btnCategory.text = nameCategory

            btnSend.setOnClickListener {
                if (editTextTask.text.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.w_enter_task),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    addTask()
                    editTextTask.text.clear()
                }
            }
            btnCategory.setOnClickListener { dialogCategory() }

            btnAlarm.setOnClickListener { }

            show()
        }
    }

    private fun dialogCategory() {
        dialogCategory = Dialog(requireContext())
        dialogCategory.apply {
            setContentView(R.layout.layout_dialog_category)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setGravity(Gravity.BOTTOM)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val lp = window!!.attributes
            lp.dimAmount = 0.7f

            val recCategory = findViewById<RecyclerView>(R.id.recycler_category)
//            val btnAddCategory = findViewById<TextView>(R.id.btn_add_category)

            //init recyclerView
            adapterCategory = CategoryAdapter(listTab, requireContext(), object : ClickOnCategory {
                override fun clickOnTab(id: Int, index: Int, name: String) {

                    nameCategory = name
                    btnCategory.text = name

                    listTab.forEachIndexed { index1, it ->
                        if (id == it.id) {
                            listTab[index].isSelected = true
                        } else {
                            listTab[index1].isSelected = false
                        }
                    }
                    dialogCategory.dismiss()
                }
            })
            recCategory.layoutManager =
                LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            recCategory.adapter = adapterCategory

            show()
        }
    }

    private var idTask: Int = 1
    private fun addTask() {
        //read idTask
        dataBase.task().readTasks().forEach {
            idTask = it.id + 1
        }
        //add task in database
        dataBase.task().insertTask(
            TaskModel(
                id = idTask,
                task = editTextTask.text.toString(),
                category = nameCategory,
                isDoneTask = false
            )
        )

        //add task in list
        adapterTasks.differ.submitList(loadData())
        initRecyclerViewTasks()
    }

    private fun loadData(): ArrayList<TaskModel> {
        listTask.add(
            TaskModel(
                id = idTask,
                task = editTextTask.text.toString(),
                category = nameCategory,
                isDoneTask = false
            )
        )
        return listTask
    }

    private var nameCategory = "همه"
    override fun clickOnTab(index: Int, name: String) {
        nameCategory = name
        listTab.forEach {
            it.isSelected = it.id == index
        }
        adapterTab.notifyDataSetChanged()
    }

    override fun clickOnTask(index: Int, checkBox: CheckBox) {
        if (checkBox.isChecked) {
            checkBox.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.underwaterMoonlight)
            )
        } else {
            checkBox.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.black)
            )
        }
        if (listTask.size != 0) {
            dialogQuestion(index, checkBox)

        }
    }

}
package ir.developer.todolist.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.AlarmClock
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var dialogAlarm: Dialog
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

                if (listTask.size == 0)
                    binding.imgEmptyList.visibility = View.VISIBLE
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
        if (listTask.size != 0) binding.imgEmptyList.visibility = View.GONE
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

            btnAlarm.setOnClickListener { dialogAlarm() }

            show()
        }
    }

    private lateinit var hour: String
    private lateinit var min: String

    @SuppressLint("DefaultLocale")
    private fun dialogAlarm() {
        dialogAlarm = Dialog(requireContext())
        dialogAlarm.apply {
            setContentView(R.layout.layout_dialog_select_time_alarm)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setGravity(Gravity.CENTER)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val lp = window!!.attributes
            lp.dimAmount = 0.7f

            val timeHour = findViewById<NumberPicker>(R.id.time_hour)
            val timeMin = findViewById<NumberPicker>(R.id.time_min)
            val btnOk = findViewById<View>(R.id.btn_ok)
            val btnCansel = findViewById<View>(R.id.btn_cancel)

            btnCansel.setOnClickListener { dialogAlarm.dismiss() }

            timeMin.setFormatter { value -> String.format("%02d", value) }
            timeHour.setFormatter { value -> String.format("%02d", value) }

//................timeHour............
            timeHour.maxValue = 23
            timeHour.minValue = 0

            hour = timeHour.value.toString()

            if (hour.length == 1)
                hour = "0$hour"

            timeHour.setOnValueChangedListener { _, _, newVal ->
                hour = newVal.toString()
                if (hour.length == 1)
                    hour = "0$hour"
            }

//...........timeMin................
            timeMin.maxValue = 59
            timeMin.minValue = 0

            min = timeMin.value.toString()

            if (min.length == 1)
                min = "0$min"

            timeMin.setOnValueChangedListener { _, _, newVal ->
                min = newVal.toString()
                if (min.length == 1)
                    min = "0$min"
            }


            btnOk.setOnClickListener { getPermission() }

            show()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGrant ->

            if (isGrant) {
//           Alarm

                val h = hour.subSequence(0, 2)
                val m = min.subSequence(0, 2)

                val intent = Intent(AlarmClock.ACTION_SET_ALARM)
                intent.putExtra(AlarmClock.EXTRA_HOUR, h.toString().toInt())
                intent.putExtra(AlarmClock.EXTRA_MINUTES, m.toString().toInt())
                intent.putExtra(AlarmClock.EXTRA_MESSAGE, "ALARM FOR APP TODOLIST..")

                startActivity(intent)
            }

        }

    private fun getPermission() {

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.SET_ALARM
            )
            == PackageManager.PERMISSION_DENIED
        ) {

            requestPermissionLauncher.launch(android.Manifest.permission.SET_ALARM)

        } else {
//           Alarm

            val h = hour.subSequence(0, 2)
            val m = min.subSequence(0, 2)

            val intent = Intent(AlarmClock.ACTION_SET_ALARM)
            intent.putExtra(AlarmClock.EXTRA_HOUR, h.toString().toInt())
            intent.putExtra(AlarmClock.EXTRA_MINUTES, m.toString().toInt())
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, "ALARM FOR APP TODOLIST")

            startActivity(intent)
        }
    }

    private fun dialogCategory() {
        dialogCategory = Dialog(requireContext())
        dialogCategory.apply {
            setContentView(R.layout.layout_dialog_category)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setGravity(Gravity.CENTER)
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
        binding.imgEmptyList.visibility = View.GONE
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
package ir.developer.todolist.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.AlarmClock
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.finishAfterTransition
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
import ir.developer.todolist.broadcast.AlarmReceiver
import ir.developer.todolist.database.AppDataBase
import ir.developer.todolist.databinding.FragmentHomeBinding
import ir.developer.todolist.datamodel.CompletedTaskModel
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.datamodel.TaskModel
import ir.developer.todolist.global.ClickOnCategory
import ir.developer.todolist.global.ClickOnTab
import ir.developer.todolist.global.ClickOnTask
import ir.developer.todolist.sharedPref.SharedPreferencesGame
import java.util.Calendar

@SuppressLint("MissingInflatedId", "DefaultLocale")
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
    private val adapterTasks by lazy { TaskAdapter(this) }
    private lateinit var sharedPreferencesGame: SharedPreferencesGame

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

        setTheme()
    }

    private fun setTheme() {
        sharedPreferencesGame = SharedPreferencesGame(requireContext())
        val nightMode = sharedPreferencesGame.readStateTheme()
        if (nightMode) {
            binding.btnSwitch.isChecked = true
            binding.btnSwitch.text = "روز"
        } else {
            binding.btnSwitch.isChecked = false
            binding.btnSwitch.text = "شب"
        }

        binding.btnSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferencesGame.saveStateTheme(false)
                binding.btnSwitch.text = "شب"

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferencesGame.saveStateTheme(true)
                binding.btnSwitch.text = "روز"

            }
        }
    }

    private fun dialogQuestion(
        index: Int,
        checkBox: CheckBox,
        newList: List<TaskModel>,
        adapter: TaskAdapter
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

                deleteTask(newList as ArrayList<TaskModel>, index, adapter)
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

    private fun deleteTask(task: ArrayList<TaskModel>, index: Int, adapter: TaskAdapter) {
        dataBase.task().deleteTask(
            TaskModel(
                task[index].id,
                task[index].task,
                task[index].category
            )
        )
        task.removeAt(index)
        adapter.differ.submitList(task)
        adapter.notifyItemRemoved(index)



        if (task.size == 0) {
            binding.imgEmptyList.visibility = View.VISIBLE
            //add data to database completedTask

            dataBase.completedTask()
                .updateTask(
                    CompletedTaskModel(
                        id = 1,
                        allTask = 0,
                        completedTask = 0,
                        notDoneTask = 0
                    )
                )
        } else {
            //add data to database completedTask
            val readAllTask = dataBase.completedTask().readTasks().allTask
            val completedTask = dataBase.completedTask().readTasks().completedTask
            val notDoneTask = dataBase.completedTask().readTasks().notDoneTask
            dataBase.completedTask()
                .updateTask(
                    CompletedTaskModel(
                        id = 1,
                        allTask = readAllTask,
                        completedTask = completedTask + 1,
                        notDoneTask = notDoneTask - 1
                    )
                )
        }
    }

    private fun readDataTask() {
        listTask = ArrayList()
        val task = dataBase.task().readTasks()
        val allTask = dataBase.completedTask().readTasks().allTask
        val completedTask = dataBase.completedTask().readTasks().completedTask

        if (task != null) {

            task.forEach {
                listTask.add(it)
            }
            if (allTask == 0) {
                dataBase.completedTask()
                    .updateTask(
                        CompletedTaskModel(
                            id = 1,
                            allTask = listTask.size,
                            completedTask = completedTask,
                            notDoneTask = listTask.size
                        )
                    )
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

                    changTab()

                    dismiss()
                }
            }
            btnCategory.setOnClickListener { dialogCategory() }

            btnAlarm.setOnClickListener {
                if (editTextTask.text.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.w_enter_task2),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    dialogAlarm()
                }
            }

            show()
        }
    }

    private fun changTab() {
        listTab.forEach {
            if (it.name == nameCategory)
                it.isSelected = true
            else
                it.isSelected = false
        }
        adapterTab.notifyDataSetChanged()
    }

    private lateinit var hour: String
    private lateinit var min: String

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

            btnOk.setOnClickListener {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, timeHour.value)
                calendar.set(Calendar.MINUTE, timeMin.value)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                setAlarm(calendar.timeInMillis)
                dialogAlarm.dismiss()
            }


//            btnOk.setOnClickListener { getPermission() }
            show()
        }
    }

    private fun setAlarm(timeInMillis: Long) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {

            putExtra("EXTRA_MESSAGE", editTextTask.text.toString())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
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
                intent.putExtra(AlarmClock.EXTRA_MESSAGE, "set alarm for ${editTextTask.text}")

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
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, "set alarm for ${editTextTask.text}")

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

            //init recyclerView
            adapterCategory =
                CategoryAdapter(listTab, requireContext(), object : ClickOnCategory {
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
                category = nameCategory
            )
        )

        //add data to database completedTask
        val readAllTask = dataBase.completedTask().readTasks().allTask
        val completedTask = dataBase.completedTask().readTasks().completedTask
        val notDoneTask = dataBase.completedTask().readTasks().notDoneTask
        dataBase.completedTask()
            .updateTask(
                CompletedTaskModel(
                    id = 1,
                    allTask = readAllTask + 1,
                    completedTask = completedTask,
                    notDoneTask = notDoneTask + 1
                )
            )

        //add task in list
        if (nameCategory != "همه")
            addNewTaskInSelectedCategory()
        else
            addNewTaskInCategoryAll()
    }

    private fun addNewTaskInCategoryAll() {
        adapterTasks.differ.submitList(loadData())
        binding.imgEmptyList.visibility = View.GONE
        initRecyclerViewTasks()
    }

    private fun addNewTaskInSelectedCategory() {
        val newList = ArrayList<TaskModel>()

        dataBase.task().readTasks().forEach {
            if (it.category == nameCategory) {
                newList.add(TaskModel(it.id, it.task, it.category))
            }
        }

        adapterNewList = TaskAdapter(object : ClickOnTask {
            override fun clickOnTask(index: Int, checkBox: CheckBox) {
                if (newList.isNotEmpty()) {
                    dialogQuestion(index, checkBox, newList, adapterNewList)
                }
            }
        })
        binding.imgEmptyList.visibility = View.GONE
        binding.recyclerTasks.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterNewList
        }
        adapterNewList.differ.submitList(newList)
    }

    private lateinit var adapterNewList: TaskAdapter

    private fun loadData(): ArrayList<TaskModel> {
        listTask.add(
            TaskModel(
                id = idTask,
                task = editTextTask.text.toString(),
                category = nameCategory
            )
        )
        return listTask
    }

    private lateinit var nameCategory: String
    private lateinit var newList: ArrayList<TaskModel>
    override fun clickOnTab(index: Int, name: String) {
        nameCategory = name
        listTab.forEach {
            it.isSelected = it.id == index
        }
        adapterTab.notifyDataSetChanged()

        if (name == "همه") {

            listTask.clear()
            val task = dataBase.task().readTasks()
            task.forEach {
                listTask.add(it)
            }

            newListTasks(listTask)
            if (listTask.isEmpty()) binding.imgEmptyList.visibility = View.VISIBLE
            else binding.imgEmptyList.visibility = View.GONE

        } else {
            newList = ArrayList()
            newList = listTask.filter { it.category == name } as ArrayList<TaskModel>
            newListTasks(newList)
            if (newList.isEmpty()) binding.imgEmptyList.visibility = View.VISIBLE
            else binding.imgEmptyList.visibility = View.GONE
        }

    }

    private lateinit var newAdapterTask: TaskAdapter
    private fun newListTasks(newList: List<TaskModel>) {
        newAdapterTask = TaskAdapter(object : ClickOnTask {
            override fun clickOnTask(index: Int, checkBox: CheckBox) {
                if (newList.isNotEmpty()) {
                    dialogQuestion(index, checkBox, newList, newAdapterTask)
                }
            }
        })
        binding.recyclerTasks.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = newAdapterTask
        }
        newAdapterTask.differ.submitList(newList)
    }

    override fun clickOnTask(index: Int, checkBox: CheckBox) {

        if (listTask.size != 0) {
            dialogQuestion(index, checkBox, listTask, adapterTasks)
        }

    }

    private fun exitApp() {
        requireActivity().overridePendingTransition(
            R.anim.exit_anim,
            0
        ) // Disable default activity transition
        finishAfterTransition(requireActivity()) // Finish activity with exit animation
    }

    private var doubleBackToExitPressedOnce = false
    override fun onResume() {
        super.onResume()

        nameCategory = "همه"

        if (view == null) {
            return
        }
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                // handle back button's click listener

                if (doubleBackToExitPressedOnce) {
//                    exitProcess(0)
                    exitApp()
                    return@setOnKeyListener true
                }

                doubleBackToExitPressedOnce = true

                dialogExitApp()

                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    doubleBackToExitPressedOnce = false
                }, 2000)

                true

            } else false
        }

    }

    private lateinit var dialogExitApp: Dialog
    private fun dialogExitApp() {
        dialogExitApp = Dialog(requireContext())

        dialogExitApp.apply {
            setContentView(R.layout.layout_dialog_exit_app)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setGravity(Gravity.CENTER)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val lp = window!!.attributes
            lp.dimAmount = 0.7f

            val btnExit = findViewById<View>(R.id.btn_exit)
            val btnCansel = findViewById<View>(R.id.btn_cansel)

            btnExit.setOnClickListener { exitApp() }
            btnCansel.setOnClickListener { dismiss() }

            show()
        }

    }

}
package ir.developer.todolist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import ir.developer.todolist.R
import ir.developer.todolist.database.AppDataBase
import ir.developer.todolist.databinding.FragmentChartBinding

class ChartFragment : Fragment() {
    private lateinit var binding: FragmentChartBinding
    private lateinit var dataBase: AppDataBase
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setChart()
    }

    private fun setChart() {
        getDataChartFromDatabase()

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(completedTask.toFloat(), "کارهای انجام شده"))
        entries.add(PieEntry(notDoneTask.toFloat(), "کارهای انجام نشده"))

        val dataSet = PieDataSet(entries, "راهنما: ")
        dataSet.setColors(
            intArrayOf(R.color.underwaterMoonlight, R.color.red),
            requireContext()
        )

        val data = PieData(dataSet)
        data.setValueTextSize(10f)
        data.setDrawValues(false)
        binding.pieChart.data = data

        binding.pieChart.description.isEnabled = false
//        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.isDrawHoleEnabled = true
//        binding.pieChart.setEntryLabelColor(R.color.white)
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad)
        binding.pieChart.invalidate() // Refresh chart
    }

    private var completedTask: Int = 0
    private var notDoneTask: Int = 0

    private fun getDataChartFromDatabase() {
        dataBase = AppDataBase.getDatabase(requireContext())
//        val allTask = dataBase.completedTask().readTasks().allTask
        completedTask = dataBase.completedTask().readTasks().completedTask
        notDoneTask = dataBase.completedTask().readTasks().notDoneTask

        binding.textNumberCompleted.text = completedTask.toString()
        binding.textNumberNotDone.text = notDoneTask.toString()

    }
}
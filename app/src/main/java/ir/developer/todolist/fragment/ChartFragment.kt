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
import com.github.mikephil.charting.formatter.PercentFormatter
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
        entries.add(PieEntry(completedTask.toFloat(), "%"))
        entries.add(PieEntry(notDoneTask.toFloat(), "%"))

        val dataSet = PieDataSet(entries, "")
        dataSet.setColors(
            intArrayOf(R.color.underwaterMoonlight, R.color.red),
            requireContext()
        )

        // on below line we are setting pie data set
        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueTextSize(15f)
        data.setValueFormatter(PercentFormatter())
//        data.setValueTypeface(Typeface.DEFAULT_BOLD)

        binding.pieChart.data = data

        binding.pieChart.description.isEnabled = false
        binding.pieChart.setUsePercentValues(true)

        //Having a circle in the pieChart
        binding.pieChart.isDrawHoleEnabled = true

        // on below line we are setting center text
        binding.pieChart.centerText = "وضعیت پیشرفت"

        // on  below line we are setting hole radius
        binding.pieChart.holeRadius = 30f
        binding.pieChart.transparentCircleRadius = 35f

        // enable rotation of the pieChart by touch
        binding.pieChart.isRotationEnabled = false
        binding.pieChart.isHighlightPerTapEnabled = false

        // on below line we are disabling our legend for pie chart
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.legend.textSize = 13f
        binding.pieChart.legend.textColor = requireContext().getColor(R.color.black)

        // on below line we are setting animation for our pie chart
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
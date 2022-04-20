package com.ultratechies.ghala.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.ultratechies.ghala.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: HomeFragmentBinding

    private val MAX_X_VALUE = 13
    private val MAX_Y_VALUE = 100
    private val MIN_Y_VALUE = 6
    private val GROUPS = 2
    private val GROUP_1_LABEL = "Inventory"
    private val GROUP_2_LABEL = "Orders"
    private val BAR_SPACE = 0.05f
    private val BAR_WIDTH = 0.2f
    private var chart: BarChart? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)

        chart = binding.barChart

        //get current year from calendar
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        binding.barChartTitleTV.text = "$year Statistics"

        val data: BarData = createChartData()!!
        configureChartAppearance()
        prepareChartData(data)

        return binding.root
    }


    private fun configureChartAppearance() {
        chart!!.setPinchZoom(false)
        chart!!.setDrawBarShadow(false)
        chart!!.setDrawGridBackground(false)

        chart!!.description.isEnabled = false
        val xAxis = chart!!.xAxis
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        val leftAxis = chart!!.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.spaceTop = 35f
        leftAxis.axisMinimum = 0f
        chart!!.axisRight.isEnabled = false
        chart!!.xAxis.axisMinimum = 1f
        chart!!.xAxis.axisMaximum = MAX_X_VALUE.toFloat()
    }

    private fun createChartData(): BarData? {
        val values1: ArrayList<BarEntry> = ArrayList()
        val values2: ArrayList<BarEntry> = ArrayList()
        for (i in 0 until MAX_X_VALUE) {
            values1.add(BarEntry(i.toFloat(), (Math.random() * (MAX_Y_VALUE - MIN_Y_VALUE) + MIN_Y_VALUE).toFloat()))
            values2.add(BarEntry(i.toFloat(), (Math.random() * (MAX_Y_VALUE - MIN_Y_VALUE) + MIN_Y_VALUE).toFloat()))
        }
        val set1 = BarDataSet(values1, GROUP_1_LABEL)
        val set2 = BarDataSet(values2, GROUP_2_LABEL)

        set1.color = ColorTemplate.rgb("#1dc7ea")
        set2.color = ColorTemplate.rgb("#fb404b")

        val dataSets: ArrayList<IBarDataSet> = ArrayList()

        dataSets.add(set1)
        dataSets.add(set2)

        return BarData(dataSets)
    }

    private fun prepareChartData(data: BarData) {
        chart!!.data = data
        chart!!.barData.barWidth = BAR_WIDTH
        val groupSpace = 1f - (BAR_SPACE + BAR_WIDTH) * GROUPS
        chart!!.groupBars(0f, groupSpace, BAR_SPACE)
        chart!!.invalidate()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }
}
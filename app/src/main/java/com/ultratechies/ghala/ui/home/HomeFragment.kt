package com.ultratechies.ghala.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.AppDatasource
import com.ultratechies.ghala.data.models.responses.home.OrderValueResponse
import com.ultratechies.ghala.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: HomeFragmentBinding

    private val MAX_X_VALUE = 13
    private val GROUPS = 2
    private val GROUP_1_LABEL = "Orders"
    private val GROUP_2_LABEL = ""
    private val BAR_SPACE = 0.1f
    private val BAR_WIDTH = 0.8f
    private var chart: BarChart? = null

    private val finalValues: ArrayList<OrderValueResponse> = ArrayList()

    //get current year from calendar
    private val calendar: Calendar = Calendar.getInstance()
    private val year = calendar.get(Calendar.YEAR)

    @Inject
    lateinit var appDatasource: AppDatasource

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        chart = binding.barChart
        binding.barChartTitleTV.text = "$year Statistics"

        getStats()
        getStatsListener()

        return binding.root
    }

    private fun getStats() {
        viewModel.getStats(1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appDatasource.getUserFromPreferencesStore().collectLatest { user ->
                    binding.userName.text = user.firstName + " " + user.lastName
                }
            }
        }
    }


    private fun configureChartAppearance() {
        chart!!.setPinchZoom(false)
        chart!!.setDrawBarShadow(false)
        chart!!.setDrawGridBackground(false)

        chart!!.description.isEnabled = false
        val xAxis = chart!!.xAxis
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(false)
        val leftAxis = chart!!.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.spaceTop = 35f
        leftAxis.axisMinimum = 0f
        chart!!.axisRight.isEnabled = false
        chart!!.xAxis.axisMinimum = 1f
        chart!!.xAxis.axisMaximum = MAX_X_VALUE.toFloat()
    }

    private fun createChartData(orderData: ArrayList<BarEntry>): BarData {

        val inventoryData = ArrayList<BarEntry>()
        val set1 = BarDataSet(orderData, GROUP_1_LABEL)
        val set2 = BarDataSet(inventoryData, GROUP_2_LABEL) //add other data to compare with: when backend is ready

        set1.color = ColorTemplate.rgb(getString(R.color.red))
        set2.color = ColorTemplate.rgb(getString(R.color.teal))

        val dataSets: ArrayList<IBarDataSet> = ArrayList()

        dataSets.add(set1)
        dataSets.add(set2)

        return BarData(dataSets)
    }

    private fun getStatsListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stats.collect { it ->
                    if (it != null) {
                        val values1: ArrayList<BarEntry> = ArrayList()
                        finalValues.clear()
                        for (i in 0 until MAX_X_VALUE) {
                            if (i < it.orderValue.size) {
                                finalValues.add(it.orderValue[i])
                            } else {
                                if(!checkIfExists(finalValues, OrderValueResponse(month = i.toDouble(), monthName = getMonthName(i), sum = 0, year = year.toDouble(), yearName = year.toString()))) {
                                    finalValues.add(OrderValueResponse(month = i.toDouble(), monthName = getMonthName(i), sum = 0, year = year.toDouble(), yearName = year.toString()))
                                }
                            }
                        }

                        finalValues.sortBy { it.month }
                        for (i in 0 until finalValues.size) {
                            values1.add(
                                BarEntry(
                                    i.toFloat(),
                                    finalValues[i].sum.toFloat()
                                )
                            )
                        }

                        displayData(values1)
                    }
                    }
                }
            }
        }

    private fun getMonthName(i: Int): String {
        return when (i) {
            1 -> "JAN"
            2 -> "FEB"
            3 -> "MAR"
            4 -> "APR"
            5 -> "MAY"
            6 -> "JUN"
            7 -> "JUL"
            8 -> "AUG"
            9 -> "SEP"
            10 -> "OCT"
            11 -> "NOV"
            12 -> "DEC"
            else -> ""
        }
    }


    //returns true if the OrderValueResponse item already exists in the finalValues list
    private fun checkIfExists(finalValues: ArrayList<OrderValueResponse>, item: OrderValueResponse): Boolean {
        for (i in 0 until finalValues.size) {
            if (finalValues[i].month == item.month) {
                return true
            }
        }
        return false
    }

    private fun displayData(orderData: ArrayList<BarEntry>) {
        val data: BarData = createChartData(orderData)
        configureChartAppearance()
        prepareChartData(data)
    }

    private fun prepareChartData(data: BarData) {
        chart!!.data = data
        chart!!.barData.barWidth = BAR_WIDTH
        val groupSpace = 1f - (BAR_SPACE + BAR_WIDTH) * GROUPS
        chart!!.groupBars(0f, groupSpace, BAR_SPACE)
        chart!!.invalidate()
    }
}
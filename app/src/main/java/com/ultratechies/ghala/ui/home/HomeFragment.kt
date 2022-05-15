package com.ultratechies.ghala.ui.home

import android.app.ProgressDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.snackbar.Snackbar
import com.github.mikephil.charting.utils.MPPointF
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.AppDatasource
import com.ultratechies.ghala.data.models.responses.home.HomeStatsResponse
import com.ultratechies.ghala.data.models.responses.home.OrderValueResponse
import com.ultratechies.ghala.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
open class HomeFragment : Fragment() {

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
    private var pieChart: PieChart? = null
    protected var tfRegular: Typeface? = null
    protected var tfLight: Typeface? = null

    private val finalValues: ArrayList<OrderValueResponse> = ArrayList()

    //get current year from calendar
    private val calendar: Calendar = Calendar.getInstance()
    private val year = calendar.get(Calendar.YEAR)

    protected val statsTitles = arrayOf(
        "Orders", "Inventory"
    )

    private val pDialog: ProgressDialog by lazy { ProgressDialog(requireActivity()) }

    @Inject
    lateinit var appDatasource: AppDatasource

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        chart = binding.barChart
        pieChart = binding.pieChart
        binding.barChartTitleTV.text = "$year Sales"

        getStats()
        getStatsListener()

        return binding.root
    }

    private fun getStats() {
        viewModel.getStats(1) //TODO: get user id from shared preferences and pass it here
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchErrorListener()

        // set data
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appDatasource.getUserFromPreferencesStore().collectLatest { user ->
                    binding.userName.text = user?.firstName + " " + user?.lastName
                    if (user?.assignedWarehouse == null) {
                        displayAssignWarehouseDialog()
                    }
                }
            }
        }

    }


    private fun displayAssignWarehouseDialog() {
       val alert =  MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Unavailable warehouse")
            .setMessage("Reach out admin to assign you a warehouse")
            .setPositiveButton("Check Again", null)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                requireActivity().finish()
            }
            .setCancelable(false)
            .show()

        alert.getButton( DialogInterface.BUTTON_POSITIVE ).setOnClickListener {
            refreshUserDetails(alert)
        }
    }

    private fun refreshUserDetails(p0: DialogInterface) {
        pDialog.setMessage("Checking...")
        pDialog.show()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getUserById.collect {
                    pDialog.dismiss()
                    if (it.assignedWarehouse == null) {
                        Snackbar.make(
                            binding.root,
                            "Please contact admin and try again",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        // dismiss dialog
                        p0.dismiss()
                    }
                }
            }
        }
        viewModel.getUserById()
    }

    private fun fetchErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collectLatest {
                    pDialog.dismiss()
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT)
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

        //pie chart
        pieChart!!.setUsePercentValues(true)
        pieChart!!.description.isEnabled = false
        pieChart!!.setExtraOffsets(5F, 10F, 5F, 5F)

        pieChart!!.dragDecelerationFrictionCoef = 0.95f

        pieChart!!.setCenterTextTypeface(tfLight)
        pieChart!!.centerText = generateCenterSpannableText()

        pieChart!!.isDrawHoleEnabled = true
        pieChart!!.setHoleColor(Color.WHITE)

        pieChart!!.setTransparentCircleColor(Color.WHITE)
        pieChart!!.setTransparentCircleAlpha(110)

        pieChart!!.holeRadius = 58f
        pieChart!!.transparentCircleRadius = 61f

        pieChart!!.setDrawCenterText(true)

        pieChart!!.rotationAngle = 0.toFloat()
        // enable rotation of the chart by touch
        pieChart!!.isRotationEnabled = true;
        pieChart!!.isHighlightPerTapEnabled = true


        pieChart!!.animateY(1400, Easing.EaseInOutQuad)
        // pieChart.spin(2000, 0, 360);

        pieChart!!.spin(2000, 0F, 360F, Easing.EaseInOutQuad)
        val l = pieChart!!.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling
        pieChart!!.setEntryLabelColor(Color.WHITE)
        pieChart!!.setEntryLabelTypeface(tfRegular)
        pieChart!!.setEntryLabelTextSize(12f)
    }

    private fun generateCenterSpannableText(): SpannableString? {
        return SpannableString("Inventory\nvs\nOrders")
    }

    //bar chart
    private fun createChartData(orderData: ArrayList<BarEntry>): BarData {

        val inventoryData = ArrayList<BarEntry>()
        val set1 = BarDataSet(orderData, GROUP_1_LABEL)
        val set2 = BarDataSet(inventoryData, GROUP_2_LABEL) //add other data to compare with: when backend is ready

        set1.color = ColorTemplate.rgb(R.color.red.toString())
        set2.color = ColorTemplate.rgb(R.color.teal.toString())

        val dataSets: ArrayList<IBarDataSet> = ArrayList()

        dataSets.add(set1)
        dataSets.add(set2)

        return BarData(dataSets)
    }

    private fun setPieChartData(homeStatsResponse: HomeStatsResponse) {
        val entries: ArrayList<PieEntry> = ArrayList()

        //loop through homeStatsResponse.orderValue and sum them orderValue.sum field
        var ordersSum = 0f
        for (i in homeStatsResponse.orderValue.indices) {
            ordersSum += homeStatsResponse.orderValue[i].sum
        }

        val ordersvsInventory = (ordersSum / (ordersSum + homeStatsResponse.inventoryValue)) * 100
        val inventoryvsOrders = (homeStatsResponse.inventoryValue / (ordersSum + homeStatsResponse.inventoryValue)) * 100

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        entries.add(
            PieEntry(
                ordersvsInventory.roundToInt().toFloat(),
                statsTitles[0 % statsTitles.size]
            )
        )
        entries.add(
            PieEntry(
                inventoryvsOrders.roundToInt().toFloat(),
                statsTitles[1 % statsTitles.size]
            )
        )
        val dataSet = PieDataSet(entries,"")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f

        // add colors
        val colors: ArrayList<Int> = ArrayList()
        colors.add(ColorTemplate.rgb(getString(R.color.red)))
        colors.add(ColorTemplate.rgb(getString(R.color.blue)))
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        data.setValueTypeface(tfLight)
        pieChart!!.setData(data)

        // undo all highlights
        pieChart!!.highlightValues(null)
        pieChart!!.invalidate()
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

                        displayData(values1, it)
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

    private fun displayData(orderData: ArrayList<BarEntry>, homeStatsResponse: HomeStatsResponse) {
        val data: BarData = createChartData(orderData)
        configureChartAppearance()
        prepareChartData(data)
        setPieChartData(homeStatsResponse)
    }

    private fun prepareChartData(data: BarData) {
        chart!!.data = data
        chart!!.barData.barWidth = BAR_WIDTH
        val groupSpace = 1f - (BAR_SPACE + BAR_WIDTH) * GROUPS
        chart!!.groupBars(0f, groupSpace, BAR_SPACE)
        chart!!.invalidate()
    }
}
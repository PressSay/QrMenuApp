package com.example.qfmenu.overview

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.databinding.FragmentOverviewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverviewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val slidePaneLayout = requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)

        val backMenu = navBar.menu.findItem(R.id.backToHome)
        val homeMenu = navBar.menu.findItem(R.id.homeMenu)
        val optionOne = navBar.menu.findItem(R.id.optionOne)
        val optionTwo = navBar.menu.findItem(R.id.optionTwo)

        homeMenu.isVisible = width < SCREEN_LARGE
        backMenu.isVisible = true
        optionOne.isVisible = false
        optionTwo.isVisible = false

        homeMenu.setIcon(R.drawable.ic_home)
        backMenu.setIcon(R.drawable.ic_arrow_back)

        navBar.setOnItemSelectedListener {
            if (it.itemId == R.id.backToHome) {
                findNavController().popBackStack()
            }
            if (it.itemId == R.id.homeMenu) {
                slidePaneLayout.closePane()
                navBar.visibility = View.GONE
            }
            true
        }


        val spinnerOverView = binding.spinnerOverView
        val editTextChooseDate = binding.editTextChooseDateOverview

        val buttonsOverView = binding.buttonsOverview
        val btnOverViewList = (buttonsOverView as ViewGroup).getChildAt(0) as AppCompatButton
        val btnInvestment = (buttonsOverView as ViewGroup).getChildAt(1) as AppCompatButton
        val btnChooseDate = (buttonsOverView as ViewGroup).getChildAt(2) as AppCompatButton

        val descriptionOverView = binding.descriptionOverView as ViewGroup
        val amountBill = descriptionOverView.getChildAt(0) as TextView
        val revenue = descriptionOverView.getChildAt(1) as TextView
        val avgPerBill = descriptionOverView.getChildAt(2) as TextView
        val investmentOverView = descriptionOverView.getChildAt(3) as TextView
        val profitOverView = descriptionOverView.getChildAt(4) as TextView

        val chartLayoutOverView = binding.chartOverView

        val sevenDayAgo = Calendar.getInstance()
        sevenDayAgo.add(Calendar.DAY_OF_YEAR, -7)
        val threeDayAgo = Calendar.getInstance()
        threeDayAgo.add(Calendar.DAY_OF_YEAR, -3)
        val oneDayAgo = Calendar.getInstance()
        oneDayAgo.add(Calendar.DAY_OF_YEAR, -1)

        val arrayDayOfWeek = arrayOf<String>(
            sevenDayAgo.time.toString().trim(),
            threeDayAgo.time.toString().trim(),
            oneDayAgo.time.toString().trim()
        )

        val ad = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayDayOfWeek
        )

        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOverView.adapter = ad


        btnOverViewList.setOnClickListener {
            findNavController().navigate(R.id.action_overviewFragment_to_overviewListFragment)
        }
        btnInvestment.setOnClickListener {
            findNavController().navigate(R.id.action_overviewFragment_to_editCreateInvestmentFragment)
        }
        btnChooseDate.setOnClickListener {

        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OverviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OverviewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
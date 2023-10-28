package com.example.qfmenu.ui.overview

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.databinding.FragmentOverviewBinding
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar
import java.util.regex.Pattern


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
    private val saveStateViewModel: SaveStateViewModel by activityViewModels()
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
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
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
        val threeDayAgo = Calendar.getInstance()
        val oneDayAgo = Calendar.getInstance()
        val navGlobal =
            NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel) {}
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, false, optTwo = false)
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, 0, 0)
        navGlobal.impNav()

        sevenDayAgo.add(Calendar.DAY_OF_YEAR, -7)
        threeDayAgo.add(Calendar.DAY_OF_YEAR, -3)
        oneDayAgo.add(Calendar.DAY_OF_YEAR, -1)

        btnOverViewList.setOnClickListener {
            findNavController().navigate(R.id.action_overviewFragment_to_billFragment)
        }
        btnInvestment.setOnClickListener {
            findNavController().navigate(R.id.action_overviewFragment_to_configInvestmentFragment)
        }
        btnChooseDate.setOnClickListener {
            if (editTextChooseDate.text.isBlank() || !Pattern.matches(
                    "(^(((0[1-9]|1[0-9]|2[0-8])/(0[1-9]|1[012]))|((29|30|31)/(0[13578]|1[02]))|((29|30)/(0[4,6,9]|11)))/(19|[2-9][0-9])\\d\\d\$)|(^29/02/(19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)\$)",
                    editTextChooseDate.text.toString()
                )
            ) {
                AlertDialog.Builder(context)
                    .setTitle("Add Bill")
                    .setMessage("Your selection is blank!!")
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _, _ -> }.show()
            } else {
                saveStateViewModel.stateCalendar = editTextChooseDate.text.toString()
                AlertDialog.Builder(context)
                    .setTitle("Add Bill")
                    .setMessage("Your selection has been saved " + saveStateViewModel.stateCalendar)
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _, _ -> }.show()
            }

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
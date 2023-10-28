package com.example.qfmenu.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.OrderDb
import com.example.qfmenu.databinding.FragmentPrepareBillBinding
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.CustomerViewModel
import com.example.qfmenu.viewmodels.CustomerViewModelFactory
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PrepareBillFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrepareBillFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentPrepareBillBinding? = null
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
        _binding = FragmentPrepareBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()
    private val customerViewModel: CustomerViewModel by viewModels {
        CustomerViewModelFactory(
            (activity?.application as QrMenuApplication).database.customerDao(),
            (activity?.application as QrMenuApplication).database.customerDishCrossRefDao(),
            (activity?.application as QrMenuApplication).database.reviewDao(),
            (activity?.application as QrMenuApplication).database.orderDao(),
            saveStateViewModel.stateDishes
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slidingPaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val navGlobal = NavGlobal(navBar, findNavController(), slidingPaneLayout, saveStateViewModel) {
            if (it == R.id.optionTwo) {
                GlobalScope.launch {
                    if (saveStateViewModel.stateIsOffOnOrder) {
                        val orderDao =
                            (activity?.application as QrMenuApplication).database.orderDao()
                        val orderDb =
                            async { orderDao.getOrderCustomerOwner(saveStateViewModel.stateCustomerDb.customerId) }.await()
                        val newOrderDb = OrderDb(
                            orderId = orderDb.orderId,
                            customerOwnerId = orderDb.customerOwnerId,
                            tableId = orderDb.tableId,
                            status = "Bill Paid",
                            payments = "Cash",
                            promotion = 0
                        )
                        orderDao.update(newOrderDb)
                        saveStateViewModel.stateCustomerOrderQueues.removeAt(saveStateViewModel.posCusCurrentQueue)
                    } else {
                        val customerDb = saveStateViewModel.stateCustomerDb
                        customerViewModel.insertCustomer(
                            customerDb, "Cash", "Bill Paid", 0, -1,
                        )
                    }
                    findNavController().navigate(R.id.action_prepareBillFragment_to_exportBillFragment)
                }
            }
        }
        navGlobal.setIconNav(0, R.drawable.ic_home, R.drawable.ic_arrow_back, R.drawable.ic_approve_order)
        navGlobal.setVisibleNav(saveStateViewModel.stateIsOffOnOrder, width < SCREEN_LARGE, false,
            optTwo = true
        )
        navGlobal.impNav()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PrepareBillFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrepareBillFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
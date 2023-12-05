package com.example.qfmenu.ui.order

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.doAfterTextChanged
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
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.repository.CustomerRepository
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.CustomerViewModel
import com.example.qfmenu.viewmodels.CustomerViewModelFactory
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val customerDao = (activity?.application as QrMenuApplication).database.customerDao()
        val customerDishCrossRefDao =
            (activity?.application as QrMenuApplication).database.customerDishCrossRefDao()
        val orderDao = (activity?.application as QrMenuApplication).database.orderDao()
        val dishDao = (activity?.application as QrMenuApplication).database.dishDao()
        val sharePref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharePref.getString("token", "") ?: ""
        val networkRetrofit = NetworkRetrofit(token)
        val customerRepository = CustomerRepository(
            networkRetrofit,
            customerDao,
            customerDishCrossRefDao,
            orderDao
        )

        val tableNameTx = binding.tableName
        val taxTx = binding.taxPreBill
        val totalTx = binding.totalPreBill
        val guestMoneyTx = binding.customerMoney
        val changeMoneyTx = binding.changeMoneyPreBill
        val codeTx = binding.billCodePreCode
        val phoneTx = binding.phonePreBill
        val orderTx = binding.orderIdPreBill


        val customerDb = saveStateViewModel.stateCustomerDb
        CoroutineScope(Dispatchers.Main).launch {
            var total = 0
            if (saveStateViewModel.stateIsStartOrder) {
                val customerDishAmountArr =
                    customerDao.getCustomerDishCrossRefs(customerDb.customerId)
                val orderDb = orderDao.getOrderCustomerOwner(customerDb.customerId)
                val customerDishArr =
                    customerDao.getCustomerWithDishes(customerDb.customerId).dishesDb
                customerDishAmountArr.forEach {
                    val dishDb = customerDishArr.find { dishDb -> dishDb.dishId == it.dishId }
                    if (dishDb != null) {
                        total += (dishDb.cost * it.amount)
                    }
                }
                tableNameTx.text =
                    requireContext().getString(R.string.table_name, orderDb.tableId.toString())
            } else {
                saveStateViewModel.stateDishes.forEach {
                    total += (it.amount.toInt() * it.dishDb.cost)
                }
                tableNameTx.text =
                    requireContext().getString(R.string.table_name, "0")
            }
            totalTx.text = requireContext().getString(R.string.total, total.toString())

            guestMoneyTx.inputType = 2
            guestMoneyTx.doAfterTextChanged { editable ->
                if (editable != null) {
                    val curTxt = editable.toString()
                    val guestMoney = if (curTxt.isEmpty()) 0 else curTxt.toInt()
                    val changeMoney = guestMoney - total
                    changeMoneyTx.text =
                        requireContext().getString(R.string.change_money, changeMoney.toString())
                }
            }
        }

        taxTx.text = requireContext().getString(R.string.tax, "10%")
        codeTx.text = customerDb.code
        phoneTx.text = requireContext().getString(R.string.phone_customer, customerDb.phone)
        orderTx.text = requireContext().getString(R.string.order_id, customerDb.customerId.toString())


        val slidingPaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
        val navGlobal = NavGlobal(
            navBar,
            findNavController(),
            slidingPaneLayout,
            saveStateViewModel,
            searchView
        ) {
            if (it == R.id.optionTwo) {

                if (saveStateViewModel.stateIsOffOnOrder) {
                    val orderDao =
                        (activity?.application as QrMenuApplication).database.orderDao()
                    CoroutineScope(Dispatchers.IO).launch {
                        val orderDb =
                            async { orderDao.getOrderCustomerOwner(saveStateViewModel.stateCustomerDb.customerId) }.await()
                        val newOrderDb = OrderDb(
                            orderId = orderDb.orderId,
                            customerOwnerId = orderDb.customerOwnerId,
                            tableId = orderDb.tableId,
                            status = "paid",
                            payments = "crash",
                            promotion = 0
                        )
                        orderDao.update(newOrderDb)
                        customerRepository.updateCustomerNet(customerDb = saveStateViewModel.stateCustomerDb)
                        saveStateViewModel.stateCustomerOrderQueues.removeAt(saveStateViewModel.posCusCurrentQueue)
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        val customerDb = saveStateViewModel.stateCustomerDb
                        customerViewModel.insertCustomer(
                            customerDb, "crash", "paid", 0, 0, customerRepository
                        )
                    }
                }
                findNavController().navigate(R.id.action_prepareBillFragment_to_exportBillFragment)

            }
        }
        navGlobal.setIconNav(
            0,
            R.drawable.ic_home,
            R.drawable.ic_arrow_back,
            R.drawable.ic_approve_order
        )
        navGlobal.setVisibleNav(
            saveStateViewModel.stateIsOffOnOrder, width < SCREEN_LARGE, false,
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
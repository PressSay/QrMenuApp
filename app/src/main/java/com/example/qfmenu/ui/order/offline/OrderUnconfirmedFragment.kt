package com.example.qfmenu.ui.order.offline

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.databinding.FragmentOrderUnconfirmedBinding
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.repository.CustomerRepository
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.util.OrderUnconfirmedAdapter
import com.example.qfmenu.viewmodels.CustomerViewModel
import com.example.qfmenu.viewmodels.CustomerViewModelFactory
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderUnconfirmedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderUnconfirmedFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentOrderUnconfirmedBinding? = null
    private val binding get() = _binding!!

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()

    private var detailTableStatus: Boolean = false

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
        _binding = FragmentOrderUnconfirmedBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val customerDao = (activity?.application as QrMenuApplication).database.customerDao()
        val customerDishCrossRefsDao = (activity?.application as QrMenuApplication).database.customerDishCrossRefDao()
        val reviewDao = (activity?.application as QrMenuApplication).database.reviewDao()
        val orderDao = (activity?.application as QrMenuApplication).database.orderDao()
        val customerViewModel: CustomerViewModel by viewModels {
            CustomerViewModelFactory(
                customerDao,
                customerDishCrossRefsDao,
                reviewDao,
                orderDao,
                saveStateViewModel.stateDishes
            )
        }
        detailTableStatus = saveStateViewModel.stateIsTableDetail

        val sharePref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token =
            sharePref.getString("token", "1|1TudhRqXy7ebTzelqlme0rD6uRAxlZVHDdROWKmW9f480231")!!
        val networkRetrofit = NetworkRetrofit(token)
        val customerRepository = CustomerRepository(networkRetrofit, customerDao, customerDishCrossRefsDao, orderDao)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val recyclerView = binding.orderListUnconfirmedRecyclerView
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val orderUnconfirmedAdapter = OrderUnconfirmedAdapter(
            true,
            requireContext(),
            saveStateViewModel,
            customerViewModel,
            customerDishCrossRefsDao,
            customerRepository
        )
        var isSearch = false
        val icSearch = requireActivity().findViewById<AppCompatImageButton>(R.id.icSearch)
        val textSearch = requireActivity().findViewById<TextInputEditText>(R.id.textSearch)


        saveStateViewModel.stateCustomerOrderQueues = mutableListOf()
        if (saveStateViewModel.isOpenSlide)
            navBar.visibility = View.VISIBLE

//        val orderListAdapter = OrderUnconfirmedAdapter(true, requireContext(), saveStateViewModel, saveStateViewModel.stateCustomerWithSelectDishes, this.viewLifecycleOwner)
//        Not Use Ram
        getOrderLive(customerViewModel) {
            orderUnconfirmedAdapter.submitList(it)
            if (it.isEmpty())
                saveStateViewModel.stateCustomerOrderQueuesPos = mutableListOf()
        }

        icSearch.setOnClickListener {
            val text = textSearch.text.toString()
            if (text.isNotEmpty()) {
                getOrderLive(customerViewModel) { arrCus ->
                    val arrCusFilter = arrCus.filter {
                        it.name.contains(text, true)
                    }
                    orderUnconfirmedAdapter.submitList(arrCusFilter)
                }
            } else {
                getOrderLive(customerViewModel) { arrCus ->
                    orderUnconfirmedAdapter.submitList(arrCus)
                }
            }
        }

        recyclerView.adapter = orderUnconfirmedAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
        val navGlobal = NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel, searchView) {
            if (it == R.id.optionOne) {
                isSearch = !isSearch
                if (isSearch) {
                    this.getOrderLive(customerViewModel) { arrCus ->
                        orderUnconfirmedAdapter.submitList(arrCus)
                    }
                }
                searchView.visibility =
                    if (isSearch) View.VISIBLE else View.GONE
            }
            if (it == R.id.optionTwo) {
                if (saveStateViewModel.stateCustomerOrderQueues.isNotEmpty()) {
                    findNavController().navigate(R.id.action_orderUnconfirmedFragment_to_orderQueueFragment)
                }
            }
        }
        navGlobal.setIconNav(0, R.drawable.ic_home, R.drawable.ic_search, R.drawable.ic_approve_order)
        navGlobal.setVisibleNav(false, width < SCREEN_LARGE, true, optTwo = true)
        navGlobal.impNav()
    }

    private fun getOrderLive(customerViewModel: CustomerViewModel, handler: (List<CustomerDb>) -> Unit) {
        if (detailTableStatus) {
              saveStateViewModel.stateIsTableDetail = false
            val tableId = saveStateViewModel.stateTableDb?.tableId ?: -1
            customerViewModel.getCustomerListByTableId(tableId).observe(this.viewLifecycleOwner) {
                handler(it)
            }
        } else {
            customerViewModel.customerList.observe(this.viewLifecycleOwner) {
                handler(it)
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
         * @return A new instance of fragment OrderUnconfirmedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderUnconfirmedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
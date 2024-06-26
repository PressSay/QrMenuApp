package com.example.qfmenu.ui.overview.bill

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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
import com.example.qfmenu.databinding.FragmentBillBinding
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.repository.CustomerRepository
import com.example.qfmenu.util.BillAdapter
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.CustomerViewModel
import com.example.qfmenu.viewmodels.CustomerViewModelFactory
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BillFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BillFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentBillBinding? = null
    private val binding get() = _binding!!

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()

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
        _binding = FragmentBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getBillLive(customerViewModel: CustomerViewModel,handler: (List<CustomerDb>) -> Unit) {
        val stateCalendar = saveStateViewModel.stateCalendar
        customerViewModel.getCustomersByCalendar(stateCalendar).observe(this.viewLifecycleOwner) {
            handler(it)
        }
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
        val sharePref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token =
            sharePref.getString("token", "1|1TudhRqXy7ebTzelqlme0rD6uRAxlZVHDdROWKmW9f480231")!!
        val networkRetrofit = NetworkRetrofit(token)
        val customerRepository = CustomerRepository(networkRetrofit, customerDao, customerDishCrossRefsDao, orderDao)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val recyclerView = binding.recyclerViewBillList
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val billAdapter = BillAdapter(requireContext(), saveStateViewModel, customerViewModel, customerRepository)


        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        getBillLive(customerViewModel) {
            billAdapter.submitList(it)
        }

        recyclerView.adapter = billAdapter

        var isSearch = false
        val icSearch = requireActivity().findViewById<AppCompatImageButton>(R.id.icSearch)
        val textSearch = requireActivity().findViewById<TextView>(R.id.textSearch)

        icSearch.setOnClickListener {
            getBillLive(customerViewModel) {
                val filltered = it.filter { it1 -> it1.customerId.toString().contains(textSearch.text) }
                billAdapter.submitList(filltered)
            }
        }

        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
        val navGlobal = NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel, searchView) {
            if (it == R.id.optionTwo) {
                isSearch = !isSearch
                if (isSearch) {
                    getBillLive(customerViewModel) { it1 ->
                        billAdapter.submitList(it1)
                    }
                }
                searchView.visibility = if (isSearch) View.VISIBLE else View.GONE
            }
        }
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, false, optTwo = true)
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, 0, R.drawable.ic_search)
        navGlobal.impNav()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BillFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BillFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
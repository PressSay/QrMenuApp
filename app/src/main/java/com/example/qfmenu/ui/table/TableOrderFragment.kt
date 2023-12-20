package com.example.qfmenu.ui.table

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
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.dao.TableDao
import com.example.qfmenu.database.entity.TableDb
import com.example.qfmenu.databinding.FragmentTableOrderBinding
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.repository.CustomerRepository
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.util.WaitingTableAdapter
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
 * Use the [TableOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TableOrderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentTableOrderBinding? = null
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
        _binding = FragmentTableOrderBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    private fun popToStartDestination(navController: NavController) {
        val startDestination = navController.graph.startDestination
        val navOptions = NavOptions.Builder()
            .setPopUpTo(startDestination, true)
            .build()
        navController.navigate(startDestination, null, navOptions)
    }

    private fun getTableLive(tableDao: TableDao, handler: (List<TableDb>) -> Unit) {
        tableDao.getTablesLiveData().observe(this.viewLifecycleOwner) {
            handler(it)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val customerDao = (activity?.application as QrMenuApplication).database.customerDao()
        val customerDishCrossRefDao =
            (activity?.application as QrMenuApplication).database.customerDishCrossRefDao()
        val reviewDao = (activity?.application as QrMenuApplication).database.reviewDao()
        val orderDao = (activity?.application as QrMenuApplication).database.orderDao()

        val customerViewModel: CustomerViewModel by viewModels {
            CustomerViewModelFactory(
                customerDao,
                customerDishCrossRefDao,
                reviewDao,
                orderDao,
                saveStateViewModel.stateDishes
            )
        }

        val isStartOrder = false
        val slidingPaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val spanCount = if (width > SCREEN_LARGE) 4 else 2
        val recyclerView = binding.tableRecyclerView
        val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)
        val tableDao = (activity?.application as QrMenuApplication).database.tableDao()
        val sharePref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharePref.getString("token", "") ?: ""
        val networkRetrofit = NetworkRetrofit(token)
        val customerRepository = CustomerRepository(
            networkRetrofit,
            customerDao,
            customerDishCrossRefDao,
            orderDao
        )
        val waitingTableAdapter = WaitingTableAdapter(
            customerViewModel,
            saveStateViewModel,
            requireContext(),
            customerRepository
        )
        var isSearch = false
        val icSearch = requireActivity().findViewById<AppCompatImageButton>(R.id.icSearch)
        val textSearch = requireActivity().findViewById<TextInputEditText>(R.id.textSearch)



        if (saveStateViewModel.isOpenSlide)
            navBar.visibility = View.VISIBLE
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = waitingTableAdapter

        getTableLive(tableDao) { tables ->
            waitingTableAdapter.submitList(tables)
        }
        icSearch.setOnClickListener {
            getTableLive(tableDao) { tables ->
                val filtered = tables.filter {
                    it.tableId == textSearch.text.toString().toLong()
                }
                if (filtered.isNotEmpty()) {
                    waitingTableAdapter.submitList(filtered)
                } else {
                    waitingTableAdapter.submitList(tables)
                }
            }
        }
        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
        val navGlobal =
            NavGlobal(navBar, findNavController(), slidingPaneLayout, saveStateViewModel, searchView) {
                if (it == R.id.homeMenu) {
                    saveStateViewModel.setStateDishesDb(listOf())
                    saveStateViewModel.stateDishesByCategories.clear()
                }
                if (it == R.id.optionTwo) {
                    isSearch = !isSearch
                    if (isSearch) {
                        getTableLive(tableDao) { tables ->
                            waitingTableAdapter.submitList(tables)
                        }
                    }
                    requireActivity().findViewById<View>(R.id.searchView).visibility =
                        if (isSearch) View.VISIBLE else View.GONE
                }
            }

        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, 0, R.drawable.ic_search)
        navGlobal.setVisibleNav(isStartOrder, width < SCREEN_LARGE, false, optTwo = true)
        navGlobal.impNav()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WaittingTableFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TableOrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
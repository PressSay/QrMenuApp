package com.example.qfmenu.ui.menu.dish.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.databinding.FragmentConfirmDishBinding
import com.example.qfmenu.util.ConfirmDishAdapter
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ConfirmDishFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfirmDishFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentConfirmDishBinding? = null
    private val binding get() = _binding!!

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()

    private val TAX = 5;

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
        _binding = FragmentConfirmDishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val slidingPaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val dishConfirmDiscount = binding.dishConfirmDiscount
        val dishConfirmTax = binding.dishConfirmTax
        val dishConfirmTotal = binding.dishConfirmTotal
        val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)
        val recyclerView = binding.recyclerViewEditConfirmDish

        dishConfirmTax.text = requireContext().getString(R.string.tax, "$TAX%")
        var total = 0;
        saveStateViewModel.stateDishes.forEach {
            total += (it.amount.toInt() * it.dishDb.cost)
        }
        val totalCurrency = NumberFormat.getNumberInstance(Locale.US).format(total)
        dishConfirmTotal.text = requireContext().getString(R.string.total, "$totalCurrency\$")
        dishConfirmDiscount.text = requireContext().getString(R.string.discount, "0%");

        val confirmDishAdapter =
            ConfirmDishAdapter(
                requireContext(),
                total,
                dishConfirmTotal,
                saveStateViewModel,
                (activity?.application as QrMenuApplication).database.customerDishCrossRefDao(),
                saveStateViewModel.stateDishes.toMutableList()
            )

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = confirmDishAdapter

        val navGlobal = NavGlobal(navBar, findNavController(), slidingPaneLayout, saveStateViewModel) {
            if (it == R.id.backToHome) {
                if (!saveStateViewModel.stateIsOffOnOrder) {
                    saveStateViewModel.setStateDishesDb(confirmDishAdapter.getDataset())
                } else {
                    saveStateViewModel.setStateDishesDb(listOf())
                }
            }
            if (it == R.id.optionOne) {
                navBar.visibility = View.GONE
                findNavController().navigate(R.id.action_confirmDishFragment_to_qrOldFragment)
            }
            if (it == R.id.optionTwo) {
                if (!saveStateViewModel.stateIsOffOnOrder) {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, 3)
                    val expired = calendar.get(Calendar.DATE).toString() + "/" + calendar.get(
                        Calendar.MONTH
                    ) + "/" + calendar.get(Calendar.YEAR)
                    val code = (0..1000).random()
                    val customerDb = CustomerDb(
                        accountCreatorId = 1,
                        dateExpireCode = expired,
                        name = "now",
                        code = code.toString(),
                        phone = "0123456789",
                        address = "empty"
                    )
                    saveStateViewModel.setStateCustomer(customerDb)

                    val customerDishDbs = mutableListOf<CustomerDishDb>()

                    confirmDishAdapter.getDataset().forEach { dishAmountDb ->
                        val customerDishDb = CustomerDishDb(
                            customerDb.customerId,
                            dishAmountDb.dishDb.dishId,
                            dishAmountDb.amount,
                            0
                        )
                        customerDishDbs.add(customerDishDb)
                    }

                    saveStateViewModel.setStateDishesDb(confirmDishAdapter.getDataset())
                    saveStateViewModel.setStateCustomer(customerDb)
                    saveStateViewModel.setStateCustomerDishCrossRefs(customerDishDbs)

                    if (saveStateViewModel.stateIsStartOrder) {
                        saveStateViewModel.stateIsTableUnClock = false
                        findNavController().navigate(R.id.action_confirmDishFragment_to_tableOrderFragment)
                    } else if (!saveStateViewModel.stateIsStartOrder) {
                        findNavController().navigate(R.id.action_confirmDishFragment_to_prepareBillFragment)
                    }
                } else {
                    saveStateViewModel.stateIsOffOnOrder = true
                    saveStateViewModel.setStateDishesDb(confirmDishAdapter.getDataset())
                    confirmDishAdapter.setDataset(saveStateViewModel.stateDishes.toMutableList())
                    saveStateViewModel.stateDishesByCategories = mutableListOf()
                    findNavController().navigate(R.id.action_confirmDishFragment_to_dishMenuFragment)
                }
            }
        }
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, R.drawable.ic_qr, R.drawable.ic_active_order)
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, !saveStateViewModel.stateIsOffOnOrder, true)
        navGlobal.impNav()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ConfirmDishFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfirmDishFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
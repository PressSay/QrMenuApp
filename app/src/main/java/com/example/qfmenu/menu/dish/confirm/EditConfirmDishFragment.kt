package com.example.qfmenu.menu.dish.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.viewmodels.CustomerViewModel
import com.example.qfmenu.viewmodels.CustomerViewModelFactory
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishCrossRef
import com.example.qfmenu.databinding.FragmentEditConfirmDishBinding
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditConfirmDishFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditConfirmDishFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentEditConfirmDishBinding? = null
    private val binding get() = _binding!!

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()
    private val customerViewModel: CustomerViewModel by viewModels {
        CustomerViewModelFactory(
            (activity?.application as QrMenuApplication).database.customerDao(),
            (activity?.application as QrMenuApplication).database.customerDishCrossRefDao(),
            (activity?.application as QrMenuApplication).database.reviewDao(),
            (activity?.application as QrMenuApplication).database.reviewCustomerCrossRefDao(),
            (activity?.application as QrMenuApplication).database.orderDao(),
            saveStateViewModel.stateDishes
        )
    }

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
        _binding = FragmentEditConfirmDishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val slidingPaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val spanCount = if (width < SCREEN_LARGE) 1 else 2

        val backMenu = navBar.menu.findItem(R.id.backToHome)
        val homeMenu = navBar.menu.findItem(R.id.homeMenu)
        val optionOne = navBar.menu.findItem(R.id.optionOne)
        val optionTwo = navBar.menu.findItem(R.id.optionTwo)

        val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)
        val recyclerView = binding.recyclerViewEditConfirmDish
        val editConfirmDishAdapter =
            EditConfirmDishAdapter(
                saveStateViewModel,
                (activity?.application as QrMenuApplication).database.customerDishCrossRefDao(),
                saveStateViewModel.stateDishes.toMutableList()
            )


        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = editConfirmDishAdapter

        optionOne.isVisible = !saveStateViewModel.stateIsOffOnOrder
        optionTwo.isVisible = true
        backMenu.isVisible = true
        homeMenu.isVisible = width < SCREEN_LARGE

        homeMenu.setIcon(R.drawable.ic_home)
        backMenu.setIcon(R.drawable.ic_arrow_back)
        optionOne.setIcon(R.drawable.ic_qr)
        optionTwo.setIcon(R.drawable.ic_active_order)

        if (width > SCREEN_LARGE) {
            navBar.menu.findItem(R.id.homeMenu).isVisible = false
        }



        navBar.setOnItemSelectedListener {
            if (it.itemId == R.id.homeMenu) {
                if (width < SCREEN_LARGE) {
                    slidingPaneLayout.closePane()
                    navBar.visibility = View.GONE
                }
            }
            if (it.itemId == R.id.backToHome) {

                if (!saveStateViewModel.stateIsOffOnOrder) {
                    saveStateViewModel.setStateDishesDb(editConfirmDishAdapter.getDataset())
                } else {
                    saveStateViewModel.setStateDishesDb(listOf())
                }
                findNavController().popBackStack()
            }

            if (it.itemId == R.id.optionTwo) {
                if (!saveStateViewModel.stateIsOffOnOrder) {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, 3)
                    val expired = calendar.get(Calendar.DATE).toString() + "/" + calendar.get(
                        Calendar.MONTH
                    ) + "/" + calendar.get(Calendar.YEAR)
                    val code = (0..1000).random()
                    val customerDb = CustomerDb(
                        accountCreatorId = 1,
                        expired = expired,
                        name = "now",
                        code = code.toString(),
                        phone = "0123456789",
                        address = "empty"
                    )
                    saveStateViewModel.setStateCustomer(customerDb)

                    val customerDishCrossRefs = mutableListOf<CustomerDishCrossRef>()

                    editConfirmDishAdapter.getDataset().forEach { dishAmountDb ->
                        val customerDishCrossRef = CustomerDishCrossRef(
                            customerDb.customerId,
                            dishAmountDb.dishDb.dishId,
                            dishAmountDb.amount,
                            0
                        )
                        customerDishCrossRefs.add(customerDishCrossRef)
                    }

                    saveStateViewModel.setStateDishesDb(editConfirmDishAdapter.getDataset())
                    saveStateViewModel.setStateCustomer(customerDb)
                    saveStateViewModel.setStateCustomerDishCrossRefs(customerDishCrossRefs)

                    if (saveStateViewModel.stateIsStartOrder) {
                        saveStateViewModel.stateIsTableUnClock = false
                        findNavController().navigate(R.id.action_editConfirmDishFragment_to_waittingTableFragment)
                    } else if (!saveStateViewModel.stateIsStartOrder) {
                        findNavController().navigate(R.id.action_editConfirmDishFragment_to_prepareBillFragment)
                    }
                } else {
                    saveStateViewModel.stateIsOffOnOrder = true
                    saveStateViewModel.setStateDishesDb(editConfirmDishAdapter.getDataset())
                    editConfirmDishAdapter.setDataset(saveStateViewModel.stateDishes.toMutableList())
                    saveStateViewModel.stateDishesByCategories = mutableListOf()
                    findNavController().navigate(R.id.action_editConfirmDishFragment_to_dishMenuFragment)
                }
            }
            true
        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditConfirmDishFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditConfirmDishFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
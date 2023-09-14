package com.example.qfmenu.menu.dish

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.CustomerDishCrossRef
import com.example.qfmenu.databinding.FragmentDishMenuBinding
import com.example.qfmenu.viewmodels.DishAmountDb
import com.example.qfmenu.viewmodels.DishViewModel
import com.example.qfmenu.viewmodels.DishViewModelFactory
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


/**
 * A simple [Fragment] subclass.
 * Use the [DishMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DishMenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()
    private val dishViewModel: DishViewModel by viewModels {
        DishViewModelFactory(
            (activity?.application as QrMenuApplication).database.dishDao(),
            (activity?.application as QrMenuApplication).database.categoryDao(),
        )
    }

    private var _binding: FragmentDishMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentDishMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myDataset = DatasourceDish().loadDishMenu()
        val recyclerView = binding.recyclerViewDishMenu
        val btnCategory = binding.itemMenuCategoryBtn
        val root = binding.layoutDishMenu
        val slidingPaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val btnBuy = binding.itemMenuOrderBtn
        val titleMenu = binding.titleMenuOrder

        val menuDao = (activity?.application as QrMenuApplication).database.menuDao()
        val menuUsed = menuDao.getMenuUsedLiveData()
        val menuUsedLiveData = menuDao.getMenuUsedLiveData()

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)

        val backMenu = navBar.menu.findItem(R.id.backToHome)
        val homeMenu = navBar.menu.findItem(R.id.homeMenu)
        val optionOne = navBar.menu.findItem(R.id.optionOne)
        val optionTwo = navBar.menu.findItem(R.id.optionTwo)

        homeMenu.isVisible = width < SCREEN_LARGE
        backMenu.isVisible = saveStateViewModel.stateIsOfflineOrder
        optionOne.isVisible = true
        optionTwo.isVisible = true

        homeMenu.setIcon(R.drawable.ic_home)
        backMenu.setIcon(R.drawable.ic_arrow_back)
        optionOne.setIcon(R.drawable.ic_search)
        optionTwo.setIcon(R.drawable.ic_menu)

        if (saveStateViewModel.stateIsStartOrder) {
            titleMenu.text = "Start Order"
        } else {
            titleMenu.text = "Buy Take Away"
        }

        btnBuy.isEnabled = false

        val dishMenuAdapter =
            DishMenuAdapter(requireContext(), btnBuy, saveStateViewModel)
        recyclerView.adapter = dishMenuAdapter

        val categoryPos = saveStateViewModel.stateCategoryPositionMenu

        btnBuy.setOnClickListener {
            if (saveStateViewModel.stateDishesByCategories.size == 0) {
                saveStateViewModel.stateDishesByCategories.add(
                    mutableListOf()
                )
            }

            saveStateViewModel.stateDishesByCategories[categoryPos] =
                dishMenuAdapter.listSelected

            val dishesDb = mutableListOf<DishAmountDb>()
            saveStateViewModel.stateDishesByCategories.forEach {
                dishesDb.addAll(it)
            }

            if (!saveStateViewModel.stateIsOfflineOrder) {
                findNavController().navigate(R.id.action_dishMenuFragment_to_editConfirmDishFragment)
            } else {
                GlobalScope.async {
                    val customerDishCrossRefDao =
                        (activity?.application as QrMenuApplication).database.customerDishCrossRefDao()

                    saveStateViewModel.stateDishes.forEach { dishAmountDb ->
                        val customerDishCrossRef = CustomerDishCrossRef(
                            saveStateViewModel.stateCustomerDb.customerId,
                            dishAmountDb.dishDb.dishId,
                            dishAmountDb.amount,
                            0
                        )
                        async(Dispatchers.IO) {
                            customerDishCrossRefDao.getListByCustomerId(
                                saveStateViewModel.stateCustomerDb.customerId
                            )
                        }.await().forEach {
                            if (it.dishId == dishAmountDb.dishDb.dishId) {
                                customerDishCrossRefDao.delete(customerDishCrossRef)
                            }
                        }
                    }

                    dishesDb.forEach { dishAmountDb ->
                        val customerDishCrossRef = CustomerDishCrossRef(
                            saveStateViewModel.stateCustomerDb.customerId,
                            dishAmountDb.dishDb.dishId,
                            dishAmountDb.amount,
                            0
                        )
                        customerDishCrossRefDao.insert(customerDishCrossRef)
                    }
                    findNavController().popBackStack()
                }
            }

            saveStateViewModel.setStateDishesDb(dishesDb)


        }

        menuUsed.observe(this.viewLifecycleOwner) { menuDb ->
            if (menuDb != null) {
                menuDao.getMenuWithCategoriesLiveData(menuId = menuDb.menuId)
                    .observe(this.viewLifecycleOwner) { menuWithCategories ->
                        if (menuWithCategories != null) {
                            val categories = menuWithCategories.categoriesDb
                            if (categories.isNotEmpty()) {
                                val categoryDb =
                                    categories[saveStateViewModel.stateCategoryPositionMenu]
                                dishViewModel.getDishesAmountDbLiveData(categoryDb.categoryId)
                                    .observe(this.viewLifecycleOwner) {
                                        it.let {
                                            dishMenuAdapter.submitList(it)
                                        }
                                    }
                            }
                        }

                    }
            }
        }

        navBar.setOnItemSelectedListener {
            if (it.itemId == R.id.homeMenu) {
                slidingPaneLayout.closePane()
                navBar.visibility = View.GONE
            }
            if (it.itemId == R.id.optionOne) {

            }
            if (it.itemId == R.id.backToHome) {
                findNavController().popBackStack()
            }
            if (it.itemId == R.id.optionTwo) {
                if (categoryPos < saveStateViewModel.stateDishesByCategories.size) {
                    saveStateViewModel.stateDishesByCategories[categoryPos] =
                        dishMenuAdapter.listSelected
                } else {
                    saveStateViewModel.stateDishesByCategories.add(
                        dishMenuAdapter.listSelected
                    )
                }
                findNavController().navigate(R.id.action_dishMenuFragment_to_categoryFragment)
            }
            true
        }



        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DishMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DishMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
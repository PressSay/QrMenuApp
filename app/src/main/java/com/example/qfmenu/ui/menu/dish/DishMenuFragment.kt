package com.example.qfmenu.ui.menu.dish

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.dao.MenuDao
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.databinding.FragmentDishMenuBinding
import com.example.qfmenu.util.DishMenuAdapter
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.DishAmountDb
import com.example.qfmenu.viewmodels.DishViewModel
import com.example.qfmenu.viewmodels.DishViewModelFactory
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


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

    private fun getMenu(
        menuDao: MenuDao,
        menuDb: LiveData<MenuDb>,
        handlerFun: (List<DishAmountDb>) -> Unit
    ) {
        menuDb.observe(this.viewLifecycleOwner) { menuDb ->
            if (menuDb != null) {
                menuDao.getMenuWithCategoriesLiveData(menuId = menuDb.menuId)
                    .observe(this.viewLifecycleOwner) { menuWithCategories ->
                        if (menuWithCategories != null) {
                            val categories = menuWithCategories.categoriesDb
                            if (categories.isNotEmpty()) {
                                val categoryDb =
                                    categories[saveStateViewModel.stateCategoryPosition]
                                dishViewModel.getDishesAmountDbLiveData(categoryDb.categoryId)
                                    .observe(this.viewLifecycleOwner) { dishAmountDbs ->
                                        handlerFun(dishAmountDbs)
                                    }
                            }
                        }
                    }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        val navHostDetail =
            requireActivity().findViewById<FragmentContainerView>(R.id.nav_host_detail)
        val categoryPos = saveStateViewModel.stateCategoryPosition
        val dishMenuAdapter =
            DishMenuAdapter(requireContext(), btnBuy, saveStateViewModel)
        var isSearch = false
        val icSearch = requireActivity().findViewById<AppCompatImageButton>(R.id.icSearch)
        val textSearch = requireActivity().findViewById<TextView>(R.id.textSearch)

        if (width < SCREEN_LARGE && navHostDetail.visibility != View.GONE && saveStateViewModel.isOpenSlide) {
            navBar.visibility = View.VISIBLE
        }
        if (saveStateViewModel.stateIsStartOrder) {
            titleMenu.text = requireContext().getString(R.string.start_order)
        } else {
            titleMenu.text = requireContext().getString(R.string.buy_take_away)
        }

        btnBuy.isEnabled = false
        btnBuy.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.green_secondary
            )
        )
        recyclerView.adapter = dishMenuAdapter
        btnBuy.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
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

                saveStateViewModel.setStateDishesDb(dishesDb)

                if (!saveStateViewModel.stateIsOffOnOrder) {
                    findNavController().navigate(R.id.action_dishMenuFragment_to_confirmDishFragment)
                } else {

                    val customerDishCrossRefDao =
                        (activity?.application as QrMenuApplication).database.customerDishCrossRefDao()

                    val customerDishCrossRefList = async(Dispatchers.IO) {
                        customerDishCrossRefDao.getListByCustomerId(
                            saveStateViewModel.stateCustomerDb.customerId
                        )
                    }.await()

                    saveStateViewModel.stateDishes.forEach { dishAmountDb ->
                        val customerDishDb = CustomerDishDb(
                            saveStateViewModel.stateCustomerDb.customerId,
                            dishAmountDb.dishDb.dishId,
                            dishAmountDb.amount,
                            0
                        )
                        customerDishCrossRefList.forEach {
                            if (it.dishId == dishAmountDb.dishDb.dishId) {
                                customerDishCrossRefDao.delete(customerDishDb)
                            }
                        }
                    }

                    dishesDb.forEach { dishAmountDb ->
                        val customerDishDb = CustomerDishDb(
                            saveStateViewModel.stateCustomerDb.customerId,
                            dishAmountDb.dishDb.dishId,
                            dishAmountDb.amount,
                            0
                        )
                        customerDishCrossRefDao.insert(customerDishDb)
                    }

                    findNavController().popBackStack()
                }
            }
        }

        icSearch.setOnClickListener {
            getMenu(menuDao, menuUsed) { dishAmountDbs ->
                val filtered =
                    dishAmountDbs.filter {
                        it.dishDb.name.contains(
                            textSearch.text.toString(),
                            ignoreCase = true
                        )
                    }
                if (filtered.isNotEmpty()) {
                    dishMenuAdapter.submitList(filtered)
                } else {
                    dishMenuAdapter.submitList(dishAmountDbs)
                }
            }
        }
        getMenu(menuDao, menuUsed) {
            dishMenuAdapter.submitList(it)
        }

        val navGlobal =
            NavGlobal(navBar, findNavController(), slidingPaneLayout, saveStateViewModel) {
                if (it == R.id.optionOne) {
                    isSearch = !isSearch
                    if (isSearch) {
                        if (categoryPos < saveStateViewModel.stateDishesByCategories.size) {
                            Log.d("CategoryPos", "GreaterThan")
                            saveStateViewModel.stateDishesByCategories[categoryPos] =
                                dishMenuAdapter.listSelected
                        } else {
                            Log.d("CategoryPos", "LessThan")
                            saveStateViewModel.stateDishesByCategories.add(
                                dishMenuAdapter.listSelected
                            )
                        }
                        getMenu(menuDao, menuUsed) {
                            dishMenuAdapter.submitList(it)
                        }
                    }
                }
                if (it == R.id.optionTwo) {
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
                requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility =
                    View.GONE
            }
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, R.drawable.ic_search, R.drawable.ic_menu)
        navGlobal.setVisibleNav(saveStateViewModel.stateIsOffOnOrder, width < SCREEN_LARGE, true,
            optTwo = true
        )
        navGlobal.impNav()

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
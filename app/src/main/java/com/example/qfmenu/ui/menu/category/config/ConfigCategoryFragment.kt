package com.example.qfmenu.ui.menu.category.config

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.databinding.FragmentConfigCategoryBinding
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.repository.MenuRepository
import com.example.qfmenu.util.ConfigCategoryAdapter
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.CategoryViewModel
import com.example.qfmenu.viewmodels.CategoryViewModelFactory
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [ConfigCategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ConfigCategoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentConfigCategoryBinding? = null
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
        _binding = FragmentConfigCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryDao = (activity?.application as QrMenuApplication).database.categoryDao()
        val dishDao = (activity?.application as QrMenuApplication).database.dishDao()
        val menuDao = (activity?.application as QrMenuApplication).database.menuDao()
        val categoryViewModel: CategoryViewModel by viewModels {
            CategoryViewModelFactory(
                dishDao,
                categoryDao,
                menuDao,
            )
        }
        val sharePref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharePref.getString("token", "") ?: ""
        val networkRetrofit = NetworkRetrofit(token)
        val menuRepository = MenuRepository(networkRetrofit, menuDao, categoryDao, dishDao)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val recyclerView = binding.recyclerViewEditCreateCategory
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val saveMenuEditCategory = binding.saveMenuEditCategory as ViewGroup
        val linearTextField1 = saveMenuEditCategory.getChildAt(0) as ViewGroup
        val menuEditTextView = linearTextField1.getChildAt(2) as TextInputEditText
        val saveMenuBtn = linearTextField1.getChildAt(3) as ImageButton
        val linearTextField2 = saveMenuEditCategory.getChildAt(1) as ViewGroup
        val categoryEditTextView = linearTextField2.getChildAt(2) as TextInputEditText
        var menuDb = saveStateViewModel.stateMenuDb
        val configCategoryAdapter = ConfigCategoryAdapter(
            categoryViewModel,
            requireContext(),
            saveStateViewModel,
            menuRepository
        )
        var isSearch = false
        val icSearch = requireActivity().findViewById<AppCompatImageButton>(R.id.icSearch)
        val textSearch = requireActivity().findViewById<TextView>(R.id.textSearch)
        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
        val navGlobal = NavGlobal(
            navBar,
            findNavController(),
            slidePaneLayout,
            saveStateViewModel,
            searchView
        ) {
            if (it == R.id.optionOne) {
                categoryViewModel.getCategoriesLiveData(saveStateViewModel.stateMenuDb.menuId)
                    .observe(this.viewLifecycleOwner) { menuWithCategories ->
                        configCategoryAdapter.submitList(menuWithCategories.categoriesDb)
                    }
                isSearch = !isSearch
                searchView.visibility =
                    if (isSearch) View.VISIBLE else View.GONE
            }
            if (it == R.id.optionTwo) {
                if (categoryEditTextView.text.toString().isNotEmpty()) {
                    val categoryDb = CategoryDb(
                        name = categoryEditTextView.text.toString(),
                        menuId = menuDb.menuId
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            menuRepository.createCategory(categoryDb)
                            categoryViewModel.insertCategory(categoryDb)
                        } catch (e: Exception) {
                            categoryViewModel.insertCategory(categoryDb)
                        }
                    }
                }
            }
        }
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, optOne = true, optTwo = true)
        navGlobal.setIconNav(
            R.drawable.ic_arrow_back,
            R.drawable.ic_home,
            R.drawable.ic_search,
            R.drawable.ic_plus
        )
        navGlobal.impNav()

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        icSearch.setOnClickListener {
            categoryViewModel.getCategoriesLiveData(saveStateViewModel.stateMenuDb.menuId)
                .observe(this.viewLifecycleOwner) { menuWithCategories ->
                    val filtered = menuWithCategories.categoriesDb.filter {
                        it.name.contains(
                            textSearch.text.toString(),
                            ignoreCase = true
                        )
                    }
                    configCategoryAdapter.submitList(filtered)
                }
        }

        categoryViewModel.getCategoriesLiveData(saveStateViewModel.stateMenuDb.menuId)
            .observe(this.viewLifecycleOwner) {
                configCategoryAdapter.submitList(it.categoriesDb)
            }
        recyclerView.adapter = configCategoryAdapter
        menuEditTextView.setText(menuDb.name)
        saveMenuBtn.setOnClickListener {
            if (menuEditTextView.text.toString() != menuDb.name) {
                menuDb = MenuDb(
                    menuDb.menuId,
                    menuEditTextView.text.toString(),
                    menuDb.isUsed
                )
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        menuRepository.updateMenuNet(menuDb)
                        menuDao.update(menuDb)
                    } catch (e: Exception) {
                        menuDao.update(menuDb)
                    }
                }
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
         * @return A new instance of fragment ConfigCategoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfigCategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
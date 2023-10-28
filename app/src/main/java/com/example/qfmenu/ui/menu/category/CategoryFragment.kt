package com.example.qfmenu.ui.menu.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.viewmodels.MenuViewModel
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.databinding.FragmentCategoryBinding
import com.example.qfmenu.util.CategoryAdapter
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.DelicateCoroutinesApi

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val menuViewModel: MenuViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

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
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun popToDishMenu(navController: NavController) {
        val startDestination = R.id.dishMenuFragment
        val navOptions = NavOptions.Builder()
            .setPopUpTo(startDestination, true)
            .build()
        navController.navigate(startDestination, null, navOptions)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = binding.recyclerViewCategory
//        val myDataset = DataSourceCategory().loadCategoryMenu()

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)


        val navGlobal = NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel) {
            if (it == R.id.optionTwo) {
               findNavController().popBackStack()
            }
        }
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, R.drawable.ic_search, R.drawable.ic_check_fill)
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, optOne = true, optTwo = true)
        navGlobal.impNav()


        recycler.layoutManager = GridLayoutManager(requireContext(), spanCount)
        val categoryAdapter = CategoryAdapter(
            requireContext(), saveStateViewModel
        )

        val menuDao = (activity?.application as QrMenuApplication).database.menuDao()
        menuDao.getMenuUsedLiveData().observe(this.viewLifecycleOwner) { menuDb ->
            if (menuDb != null) {
                menuDao.getMenuWithCategoriesLiveData(menuId = menuDb.menuId)
                    .observe(this.viewLifecycleOwner) { menuWithCategories ->
                        if (menuWithCategories != null) {
                            val categories = menuWithCategories.categoriesDb
                            if (categories.isNotEmpty()) {
                                categoryAdapter.submitList(categories)
                                recycler.adapter = categoryAdapter
                            }
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
         * @return A new instance of fragment CategoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
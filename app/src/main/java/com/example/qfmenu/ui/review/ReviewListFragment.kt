package com.example.qfmenu.ui.review

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
import com.example.qfmenu.databinding.FragmentReviewListBinding
import com.example.qfmenu.util.ReviewListAdapter
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReviewListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReviewListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentReviewListBinding? = null
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
        _binding = FragmentReviewListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val btnReviewStore = binding.btnRStoreReviewList
        val recyclerView = binding.recyclerViewReviewList
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)

        val backMenu = navBar.menu.findItem(R.id.backToHome)
        val homeMenu = navBar.menu.findItem(R.id.homeMenu)
        val optionOne = navBar.menu.findItem(R.id.optionOne)
        val optionTwo = navBar.menu.findItem(R.id.optionTwo)

        homeMenu.isVisible = width < SCREEN_LARGE
        backMenu.isVisible = true
        optionOne.isVisible = true
        optionTwo.isVisible = true

        homeMenu.setIcon(R.drawable.ic_home)
        backMenu.setIcon(R.drawable.ic_arrow_back)
        optionOne.setIcon(R.drawable.ic_search)
        optionTwo.setIcon(R.drawable.ic_menu)

        navBar.setOnItemSelectedListener {
            if (it.itemId == R.id.backToHome) {
                findNavController().popBackStack()
            }
            if (it.itemId == R.id.homeMenu) {
                slidePaneLayout.closePane()
                navBar.visibility = View.GONE
            }
            if (it.itemId == R.id.optionOne) {

            }
            if (it.itemId == R.id.optionTwo) {
                findNavController().navigate(R.id.action_reviewListFragment_to_categoryFragment)
            }
            true
        }

        btnReviewStore.setOnClickListener {
            findNavController().navigate(R.id.action_reviewListFragment_to_reviewStoreListFragment)
        }

        val reviewListAdapter = ReviewListAdapter(
            requireContext(),
            saveStateViewModel
        )

//        val customerDishCrossRefDao =
//            (activity?.application as QrMenuApplication).database.customerDishCrossRefDao()
        val menuDao = (activity?.application as QrMenuApplication).database.menuDao()
        val categoryDao = (activity?.application as QrMenuApplication).database.categoryDao()


        recyclerView.adapter = reviewListAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)

//        menuDao.getMenuUsedLiveData().observe(this.viewLifecycleOwner) {
//            if (it != null) {
//                menuDao.getMenuWithCategoriesLiveData(it.menuId)
//                    .observe(this.viewLifecycleOwner) { menuWithCategories ->
//                        if (menuWithCategories != null) {
//                            if (menuWithCategories.categoriesDb.isNotEmpty()) {
//                                val posCategory = saveStateViewModel.stateCategoryPositionMenu
//                                customerDishCrossRefDao.getDishReviewByCategoryId(menuWithCategories.categoriesDb[posCategory].categoryId)
//                                    .observe(this.viewLifecycleOwner) { dishesDb ->
//                                        dishesDb.let {
//                                            reviewListAdapter.submitList(dishesDb)
//                                        }
//                                    }
//                            }
//                        }
//                    }
//            }
//        }

        menuDao.getMenuUsedLiveData().observe(this.viewLifecycleOwner) { menuDb ->
            if (menuDb != null) {
                menuDao.getMenuWithCategoriesLiveData(menuId = menuDb.menuId)
                    .observe(this.viewLifecycleOwner) { menuWithCategories ->
                        if (menuWithCategories != null) {
                            val categories = menuWithCategories.categoriesDb
                            if (categories.isNotEmpty()) {
                                val categoryDb =
                                    categories[saveStateViewModel.stateCategoryPosition]
                                categoryDao.getCategoryWithDishesLiveData(categoryDb.categoryId)
                                    .observe(this.viewLifecycleOwner) {
                                        it.let {
                                            if (it.dishesDb.isNotEmpty())
                                                reviewListAdapter.submitList(it.dishesDb)
                                        }
                                    }
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
         * @return A new instance of fragment ReviewListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReviewListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
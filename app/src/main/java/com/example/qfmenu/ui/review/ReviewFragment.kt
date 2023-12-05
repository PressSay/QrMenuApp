package com.example.qfmenu.ui.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.dao.CategoryDao
import com.example.qfmenu.database.dao.MenuDao
import com.example.qfmenu.database.entity.CategoryWidthDishes
import com.example.qfmenu.databinding.FragmentReviewBinding
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.util.ReviewAdapter
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReviewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentReviewBinding? = null
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
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun formatNumRev(number: Int): String {
        if (number > 1000000000) {
            return "${number / 1000000000}B"
        } else if (number > 1000000) {
            return "${number / 1000000}M"
        } else if (number > 1000) {
            return "${number / 1000}K"
        } else {
            return number.toString()
        }
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
        var isSearch = false
        val icSearch = requireActivity().findViewById<AppCompatImageButton>(R.id.icSearch)
        val textSearch = requireActivity().findViewById<TextInputEditText>(R.id.textSearch)
        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)

        btnReviewStore.setOnClickListener {
            findNavController().navigate(R.id.action_reviewFragment_to_reviewStoreFragment)
        }

        val reviewDao = (activity?.application as QrMenuApplication).database.reviewDao()
        val reviewAdapter = ReviewAdapter(
            requireContext(),
            saveStateViewModel,
            reviewDao
        )
        val menuDao = (activity?.application as QrMenuApplication).database.menuDao()
        val categoryDao = (activity?.application as QrMenuApplication).database.categoryDao()

        reviewDao.countCustRev().observe(this.viewLifecycleOwner) { it ->
            if (it != null) {
                val formatted = formatNumRev(it)
                "$formatted reviews".also { btnReviewStore.text = it }
            }
        }

        recyclerView.adapter = reviewAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)


        getReviewLive(menuDao, categoryDao) {
            val reviewList = it.dishesDb
            if (reviewList.isNotEmpty()) {
                reviewAdapter.submitList(reviewList)
            }
        }

        icSearch.setOnClickListener {
            getReviewLive(menuDao, categoryDao) { it ->
                val filtered = it.dishesDb.filter { it.name.contains(textSearch.text.toString()) }
                if (filtered.isNotEmpty()) {
                    reviewAdapter.submitList(filtered)
                }
            }
        }

        val navGlobal =
            NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel, searchView) {
                if (it == R.id.optionOne) {
                    isSearch = !isSearch
                    if (isSearch) {
                        getReviewLive(menuDao, categoryDao) { it ->
                            val reviewList = it.dishesDb
                            if (reviewList.isNotEmpty()) {
                                reviewAdapter.submitList(reviewList)
                            }
                        }
                    }
                    searchView.visibility = if (isSearch) View.VISIBLE else View.GONE
                }
                if (it == R.id.optionTwo) {
                    findNavController().navigate(R.id.action_reviewFragment_to_categoryFragment)
                }
            }
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, true, optTwo = true)
        navGlobal.setIconNav(
            R.drawable.ic_arrow_back,
            R.drawable.ic_home,
            R.drawable.ic_search,
            R.drawable.ic_menu
        )
        navGlobal.impNav()

    }

    private fun getReviewLive(menuDao: MenuDao, categoryDao: CategoryDao, handler: (CategoryWidthDishes) -> Unit) {
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
                                        handler(it)
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
         * @return A new instance of fragment ReviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReviewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
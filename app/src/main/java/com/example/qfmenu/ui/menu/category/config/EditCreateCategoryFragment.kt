package com.example.qfmenu.ui.menu.category.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.viewmodels.CategoryViewModel
import com.example.qfmenu.viewmodels.CategoryViewModelFactory
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.databinding.FragmentEditCreateCategoryBinding
import com.example.qfmenu.util.EditCategoryListAdapter
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [EditCreateCategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EditCreateCategoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentEditCreateCategoryBinding? = null
    private val binding get() = _binding!!

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModelFactory(
            (activity?.application as QrMenuApplication).database.dishDao(),
            (activity?.application as QrMenuApplication).database.categoryDao(),
            (activity?.application as QrMenuApplication).database.menuDao(),
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
        _binding = FragmentEditCreateCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val recyclerView = binding.recyclerViewEditCreateCategory
        val spanCount = if (width < SCREEN_LARGE) 1 else 2

        val backMenu = navBar.menu.findItem(R.id.backToHome)
        val homeMenu = navBar.menu.findItem(R.id.homeMenu)
        val optionOne = navBar.menu.findItem(R.id.optionOne)
        val optionTwo = navBar.menu.findItem(R.id.optionTwo)

        optionOne.isVisible = true
        optionTwo.isVisible = true
        backMenu.isVisible = true
        homeMenu.isVisible = width < SCREEN_LARGE

        homeMenu.setIcon(R.drawable.ic_home)
        backMenu.setIcon(R.drawable.ic_arrow_back)
        optionOne.setIcon(R.drawable.ic_search)
        optionTwo.setIcon(R.drawable.ic_plus)

        val saveMenuEditCategory = binding.saveMenuEditCategory as ViewGroup
        val linearTextField1 = saveMenuEditCategory.getChildAt(0) as ViewGroup
        val menuEditTextView = linearTextField1.getChildAt(2) as TextInputEditText
        val saveMenuBtn = linearTextField1.getChildAt(3) as ImageButton
        val linearTextField2 = saveMenuEditCategory.getChildAt(1) as ViewGroup
        val categoryEditTextView = linearTextField2.getChildAt(2) as TextInputEditText

        val menuDao = (activity?.application as QrMenuApplication).database.menuDao()
        var menuDb = saveStateViewModel.stateMenuDb

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)

        val editCategoryListAdapter = EditCategoryListAdapter(categoryViewModel, requireContext(), saveStateViewModel)




        categoryViewModel.getCategoriesLiveData(saveStateViewModel.stateMenuDb.menuId).observe(this.viewLifecycleOwner) {
            editCategoryListAdapter.submitList(it.categoriesDb)
        }


        recyclerView.adapter = editCategoryListAdapter

        menuEditTextView.setText(menuDb.menuName)

        saveMenuBtn.setOnClickListener {
            if (menuEditTextView.text.toString() != menuDb.menuName) {
                GlobalScope.launch {
                    menuDb = MenuDb(
                        menuDb.menuId,
                        menuEditTextView.text.toString(),
                        menuDb.isUsed
                    )
                    menuDao.update(
                        menuDb
                    )
                }
            }

        }

        navBar.setOnItemSelectedListener {
            if (it.itemId == R.id.homeMenu) {
                slidePaneLayout.closePane()
                navBar.visibility = View.GONE
            }
            if (it.itemId == R.id.backToHome) {
                findNavController().popBackStack()
            }
            if (it.itemId == R.id.optionOne) {

            }
            if (it.itemId == R.id.optionTwo) {
                GlobalScope.launch {
                    categoryViewModel.insertCategory(
                        CategoryDb(
                            categoryName = categoryEditTextView.text.toString(),
                            menuCreatorId = menuDb.menuId
                        )
                    )
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
         * @return A new instance of fragment EditCreateCategoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditCreateCategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
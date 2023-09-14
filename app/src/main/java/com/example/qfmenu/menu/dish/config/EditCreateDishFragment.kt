package com.example.qfmenu.menu.dish.config

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
import com.example.qfmenu.menu.dish.DatasourceDish
import com.example.qfmenu.viewmodels.DishViewModel
import com.example.qfmenu.viewmodels.DishViewModelFactory
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.databinding.FragmentEditCreateDishBinding
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [EditCreateDishFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EditCreateDishFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentEditCreateDishBinding? = null
    private val binding get() = _binding!!

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()

    private val dishViewModel: DishViewModel by viewModels {
        DishViewModelFactory(
            (activity?.application as QrMenuApplication).database.dishDao(),
            (activity?.application as QrMenuApplication).database.categoryDao(),

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
        _binding = FragmentEditCreateDishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerViewEditCreateDish
        val myDataset = DatasourceDish().loadDishMenu()
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = (resources.displayMetrics.widthPixels / resources.displayMetrics.density)
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
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
        optionTwo.setIcon(R.drawable.ic_check_fill)

        val linearTextField0 = binding.linearEditTextEditCreateDish as ViewGroup
        val linearTextField1 = linearTextField0.getChildAt(1) as ViewGroup
        val categoryEditText = linearTextField1.getChildAt(2) as TextInputEditText
        val categorySaveBtn = linearTextField1.getChildAt(3) as ImageButton
        val linearTextField2 = linearTextField0.getChildAt(2) as ViewGroup
        val titleDish = linearTextField2.getChildAt(2) as TextInputEditText
        val costDish = linearTextField2.getChildAt(3) as TextInputEditText
        val descriptionDish = linearTextField0.getChildAt(3) as TextInputEditText
        var categoryDb = saveStateViewModel.stateCategoryDb
        val categoryDao = (activity?.application as QrMenuApplication).database.categoryDao()
        val listAdapter = EditDishListAdapter(dishViewModel, requireContext(), saveStateViewModel)

        dishViewModel.getDishesLiveData(saveStateViewModel.stateCategoryDb.categoryId).observe(this.viewLifecycleOwner){
            it.let {
                listAdapter.submitList(it)
            }
        }


        categoryEditText.setText(saveStateViewModel.stateCategoryDb.categoryName)

        categorySaveBtn.setOnClickListener {
            if (categoryEditText.text.toString() != categoryDb.categoryName) {
                GlobalScope.launch {
                    categoryDb = CategoryDb(
                        categoryDb.categoryId,
                        categoryEditText.text.toString(),
                        categoryDb.menuCreatorId
                    )
                    categoryDao.update(categoryDb)
                }
            }
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        recyclerView.adapter = listAdapter

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
                val dishDb = DishDb(
                    dishName = titleDish.text.toString(),
                    categoryCreatorId = categoryDb.categoryId,
                    description = descriptionDish.text.toString(),
                    cost = costDish.text.toString().toInt()
                )
                dishViewModel.insertDish(dishDb)
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
         * @return A new instance of fragment EditCreateDishFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditCreateDishFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
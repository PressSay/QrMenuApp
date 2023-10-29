package com.example.qfmenu.ui.menu.dish.config.detail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.viewmodels.DishViewModel
import com.example.qfmenu.viewmodels.DishViewModelFactory
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.databinding.FragmentDetailDishBinding
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailDishFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailDishFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentDetailDishBinding? = null
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
        _binding = FragmentDetailDishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linear1 = binding.linear1EditCreateDetailDish as ViewGroup
        val linearDishView = linear1.getChildAt(0) as ViewGroup
        val includeDishItem = linearDishView.getChildAt(0) as ViewGroup
        val linearDishItem = includeDishItem.getChildAt(0) as ViewGroup
        val imgDish = (linearDishItem.getChildAt(0) as ViewGroup).getChildAt(0) as ImageView
        val descriptionDish =
            ((linearDishItem.getChildAt(1) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(1) as TextView
        val titleDish =
            ((((linearDishItem.getChildAt(1) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(
                0
            ) as ViewGroup).getChildAt(0) as TextView
        val costDish =
            ((linearDishItem.getChildAt(0) as ViewGroup).getChildAt(1) as ViewGroup).getChildAt(0) as TextView

        val linearDishEditView = (linear1.getChildAt(2) as ViewGroup)
        val linearDishTitleEdit = linearDishEditView.getChildAt(1) as ViewGroup

        val dishEditTitle = linearDishTitleEdit.getChildAt(2) as TextInputEditText
        val dishDescriptionEdit = linearDishEditView.getChildAt(2) as TextInputEditText
        val dishEditCost = linearDishTitleEdit.getChildAt(3) as TextInputEditText

        val dishDb = saveStateViewModel.stateDishDb
        val dishCostToString = buildString {
            append(dishDb.cost.toString())
            append(" $")
        }

        if (dishDb.image != "Empty") {
            imgDish.setImageURI(Uri.EMPTY)
        }

        titleDish.text = dishDb.name
        descriptionDish.text = dishDb.description
        costDish.text = dishCostToString

        dishEditTitle.setText(dishDb.name)
        dishDescriptionEdit.setText(dishDb.description)
        dishEditCost.setText(dishDb.cost.toString())

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = (resources.displayMetrics.widthPixels / resources.displayMetrics.density)
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)


        val navGlobal = NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel) {
            if (it == R.id.optionTwo) {
                val newDishDb =
                    DishDb(
                        dishDb.dishId,
                        name = dishEditTitle.text.toString(),
                        categoryId = saveStateViewModel.stateCategoryDb.categoryId,
                        description = dishDescriptionEdit.text.toString(),
                        cost = dishEditCost.text.toString().toInt()
                    )
                if (newDishDb.name != dishDb.name || newDishDb.description != dishDb.description || newDishDb.cost != dishDb.cost) {
                    dishViewModel.updateDish(
                        newDishDb
                    )
                    titleDish.text = newDishDb.name
                    descriptionDish.text = newDishDb.description
                    costDish.text = buildString {
                        append(newDishDb.cost)
                        append(" $")
                    }

                }
            }
        }
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, 0, R.drawable.ic_save)
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, false, optTwo = true)
        navGlobal.impNav()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailDishFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailDishFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
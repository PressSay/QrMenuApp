package com.example.qfmenu.ui.review.dish.list

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.database.entity.ReviewDb
import com.example.qfmenu.database.entity.ReviewDishCrossRef
import com.example.qfmenu.databinding.FragmentReviewDishBinding
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.SaveStateViewModel

import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReviewDishFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReviewDishFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentReviewDishBinding? = null
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
        _binding = FragmentReviewDishBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setProDishDbReview(layout: ViewGroup, dishDb: DishDb) {
        val linear1 = layout.getChildAt(0) as ViewGroup
        val linear1_1 = linear1.getChildAt(0) as ViewGroup
        val linear1_1_1 = linear1_1.getChildAt(1) as ViewGroup
        val linear1_2 = linear1.getChildAt(1) as ViewGroup
        val linear1_2_1 = linear1_2.getChildAt(0) as ViewGroup
        val linear1_2_1_1 = linear1_2_1.getChildAt(0) as ViewGroup
        val linear1_2_1_1_1 = linear1_2_1_1.getChildAt(0) as ViewGroup
        val titleDish = linear1_2_1_1_1.getChildAt(0) as TextView
        val img = linear1_1.getChildAt(0) as ImageView
        val costDish = linear1_1_1.getChildAt(0) as TextView
        val descriptionDish = linear1_2_1.getChildAt(1) as TextView

        titleDish.text = dishDb.dishName
        costDish.text = dishDb.cost.toString()
        descriptionDish.text = dishDb.description
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val editInputDishReview = binding.editInputDishReview
        val layoutIsThumpUp = binding.layoutIsThumbUp as ViewGroup
        var stateThumpUp = -1
        val btnThumbUp = layoutIsThumpUp.getChildAt(1) as AppCompatImageButton
        val btnThumbDown = layoutIsThumpUp.getChildAt(0) as AppCompatImageButton
        val layoutDishDb = binding.layoutDishDbReviewEdit.root

        setProDishDbReview(layoutDishDb, saveStateViewModel.stateDishDb)
        btnThumbUp.setOnClickListener {
            stateThumpUp = 1
            btnThumbUp.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green_tertiary
                )
            )
            btnThumbDown.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green_surface_variant
                )
            )
        }
        btnThumbDown.setOnClickListener {
            stateThumpUp = 0
            btnThumbDown.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green_error
                )
            )
            btnThumbUp.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),

                    R.color.green_surface_variant
                )
            )
        }
        val navGlobal = NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel) {
            if (it == R.id.optionTwo) {
                Log.d("DishReview", "True")
                if (editInputDishReview.text?.isNotBlank() == true) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val reviewDao =
                            (activity?.application as QrMenuApplication).database.reviewDao()
                        val reviewDb = ReviewDb(
                            isThumbUp = stateThumpUp,
                            description = editInputDishReview.text.toString()
                        )
                        val reviewId = reviewDao.insert(reviewDb)
                        reviewDao.insertReviewDishCrossRef(
                            ReviewDishCrossRef(
                                dishId = saveStateViewModel.stateDishDb.dishId,
                                reviewId = reviewId
                            )
                        )
                        findNavController().popBackStack()
                    }
                } else {
                    AlertDialog.Builder(context)
                        .setTitle("Input Invalid")
                        .setMessage("Try Again")
                        .setPositiveButton(android.R.string.ok
                        ) { _, _ -> }.show()
                }
            }
        }
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, 0, R.drawable.ic_star)
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
         * @return A new instance of fragment ReviewDishFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReviewDishFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
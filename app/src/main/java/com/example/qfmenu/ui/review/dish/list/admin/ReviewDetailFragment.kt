package com.example.qfmenu.ui.review.dish.list.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.databinding.FragmentReviewDetailBinding
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.util.StoDisReviewAdapter
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReviewDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReviewDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentReviewDetailBinding? = null
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
        _binding = FragmentReviewDetailBinding.inflate(inflater, container, false)
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

        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
        searchView.visibility = View.GONE

        titleDish.text = dishDb.name
        costDish.text = dishDb.cost.toString()
        descriptionDish.text = dishDb.description


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerViewReviewListDetail = binding.recyclerViewReviewListDetail
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)

        val layoutDishDbReview = binding.layoutDishDbReview.root
        setProDishDbReview(layoutDishDbReview, saveStateViewModel.stateDishDb)
        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
        val navGlobal = NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel, searchView) {
            if (it == R.id.optionOne) {
            }
            if (it == R.id.optionTwo) {
                findNavController().navigate(R.id.action_reviewDetailFragment_to_reviewDishFragment)
            }
        }
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, R.drawable.ic_thumb_down, R.drawable.ic_plus)
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, true, optTwo = true)
        navGlobal.impNav()

        val reviewDao = (activity?.application as QrMenuApplication).database.reviewDao()

        val stoDisReviewAdapter = StoDisReviewAdapter(
            false,
            reviewDao,
            saveStateViewModel,
            requireContext()
        )

        recyclerViewReviewListDetail.layoutManager = GridLayoutManager(requireContext(), spanCount)

        recyclerViewReviewListDetail.adapter = stoDisReviewAdapter

        reviewDao.getReviewsByDishId(saveStateViewModel.stateDishDb.dishId).observe(this.viewLifecycleOwner) {
            it.apply {
                stoDisReviewAdapter.submitList(it)
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
         * @return A new instance of fragment ReviewDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReviewDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
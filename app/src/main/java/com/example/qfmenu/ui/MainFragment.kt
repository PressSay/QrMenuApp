package com.example.qfmenu.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.databinding.FragmentMainBinding
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun popToStartDestination(navController: NavController) {
        val startDestination = navController.graph.startDestination
        val navOptions = NavOptions.Builder()
            .setPopUpTo(startDestination, true)
            .build()
        navController.navigate(startDestination, null, navOptions)
    }

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val swipeRefreshLayout = binding.refreshLayout
        val myNavHostFragment1 : NavHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_detail) as NavHostFragment
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width : Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val slidingPaneLayout = requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)


        // Refresh function for the layout
        swipeRefreshLayout.setOnRefreshListener{



            swipeRefreshLayout.isRefreshing = false
        }

        if (width < SCREEN_LARGE) {
            navBar.visibility = View.GONE
        } else {
            navBar.menu.findItem(R.id.homeMenu).isVisible = false
        }

        binding.btnStartOrder.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            saveStateViewModel.stateIsStartOrder = true
            saveStateViewModel.stateIsOffOnOrder = false
            saveStateViewModel.setStateDishesDb(listOf())
            saveStateViewModel.stateCategoryPosition = 0
            saveStateViewModel.stateDishesByCategories = mutableListOf()
            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                slidingPaneLayout.openPane()
            }
            requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility = View.GONE
        }

        binding.btnBuyTakeAway.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            saveStateViewModel.stateIsStartOrder = false
            saveStateViewModel.stateIsOffOnOrder = false
            saveStateViewModel.setStateDishesDb(listOf())
            saveStateViewModel.stateCategoryPosition = 0
            saveStateViewModel.stateDishesByCategories = mutableListOf()
            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                slidingPaneLayout.openPane()
            }
            requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility = View.GONE
        }

        binding.btnConfigShop.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.configShopFragment)

            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                Log.d("openSide", "true")
                slidingPaneLayout.openPane()
            }
            requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility = View.GONE
        }

        binding.btnOfflineList.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.orderUnconfirmedFragment)
            saveStateViewModel.stateIsOffOnOrder = true
            saveStateViewModel.setStateDishesDb(listOf())
            saveStateViewModel.stateDishesByCategories = mutableListOf()
            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                slidingPaneLayout.openPane()
            }
            requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility = View.GONE
        }

        binding.btnOnlineList.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.onlineOrderFragment)
            saveStateViewModel.setStateDishesDb(listOf())
            saveStateViewModel.stateDishesByCategories = mutableListOf()
            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                slidingPaneLayout.openPane()
            }
            requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility = View.GONE
        }

        binding.btnTableUnlock.setOnClickListener {
            saveStateViewModel.stateIsTableUnClock = true
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.tableOrderFragment)
            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                slidingPaneLayout.openPane()
            }
            requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility = View.GONE
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                MainFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
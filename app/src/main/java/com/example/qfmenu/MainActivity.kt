package com.example.qfmenu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.databinding.ActivityMainBinding
import com.example.qfmenu.viewmodels.SaveStateViewModel

val SCREEN_LARGE get() = 850

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private fun popToStartDestination(navController: NavController) {
        val startDestination = navController.graph.startDestination
        val navOptions = NavOptions.Builder()
            .setPopUpTo(startDestination, true)
            .build()
        navController.navigate(startDestination, null, navOptions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val saveStateViewModel: SaveStateViewModel by viewModels()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val slidingPaneLayout = binding.slidingPaneLayout
        val myNavHostFragment1: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_detail) as NavHostFragment
        val inflater1 = myNavHostFragment1.navController.navInflater
        val graphOrder = inflater1.inflate(R.navigation.nav_graph)
        val navBar = binding.navBar

        binding.searchView.visibility = View.GONE

        myNavHostFragment1.navController.graph = graphOrder

        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED

        val width : Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density

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
            saveStateViewModel.stateCategoryPositionMenu = 0
            saveStateViewModel.stateDishesByCategories = mutableListOf()
            if (width < SCREEN_LARGE) {
                binding.slidingPaneLayout.openPane()
                navBar.visibility = View.VISIBLE
            }
        }

        binding.btnBuyTakeAway.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            saveStateViewModel.stateIsStartOrder = false
            saveStateViewModel.stateIsOffOnOrder = false
            saveStateViewModel.setStateDishesDb(listOf())
            saveStateViewModel.stateCategoryPositionMenu = 0
            saveStateViewModel.stateDishesByCategories = mutableListOf()
            if (width < SCREEN_LARGE) {
                slidingPaneLayout.openPane()
                navBar.visibility = View.VISIBLE
            }
        }

        binding.btnConfigShop.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.configShopFragment)

            if (width < SCREEN_LARGE) {
                slidingPaneLayout.openPane()
                navBar.visibility = View.VISIBLE
            }
        }

        binding.btnOfflineList.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.orderListUnconfirmedFragment)
            saveStateViewModel.stateIsOffOnOrder = true
            saveStateViewModel.setStateDishesDb(listOf())
            saveStateViewModel.stateDishesByCategories = mutableListOf()
            if (width < SCREEN_LARGE) {
                slidingPaneLayout.openPane()
                navBar.visibility = View.VISIBLE
            }
        }

        binding.btnOnlineList.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.editOnlineOrderFragment)
            saveStateViewModel.setStateDishesDb(listOf())
            saveStateViewModel.stateDishesByCategories = mutableListOf()
            if (width < SCREEN_LARGE) {
                slidingPaneLayout.openPane()
                navBar.visibility = View.VISIBLE
            }
        }

        binding.btnTableUnlock.setOnClickListener {
            saveStateViewModel.stateIsTableUnClock = true
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.waitingTableFragment)

            if (width < SCREEN_LARGE) {
                slidingPaneLayout.openPane()
                navBar.visibility = View.VISIBLE
            }
        }

    }
}
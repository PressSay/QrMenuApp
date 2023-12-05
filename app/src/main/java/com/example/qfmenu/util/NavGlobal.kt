package com.example.qfmenu.util

import android.view.View
import android.widget.LinearLayout
import androidx.navigation.NavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavGlobal(
    val navBar: BottomNavigationView,
    private val navController: NavController,
    private val slidePaneLayout: SlidingPaneLayout,
    private val saveStateViewModel: SaveStateViewModel,
    val searchView: LinearLayout,
    val option: (itemId: Int) -> Unit
) {
    private val backMenu = navBar.menu.findItem(R.id.backToHome)
    private val homeMenu = navBar.menu.findItem(R.id.homeMenu)
    private val optionOne = navBar.menu.findItem(R.id.optionOne)
    private val optionTwo = navBar.menu.findItem(R.id.optionTwo)

    fun impNav() {
        navBar.setOnItemSelectedListener {
            if (it.itemId == R.id.backToHome) {
                searchView.visibility = View.GONE
                navController.popBackStack()
            }
            if (it.itemId == R.id.homeMenu) {
                slidePaneLayout.closePane()
                navBar.visibility = View.GONE
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                searchView.visibility = View.GONE
            }
            option (it.itemId)
            true
        }
    }

    fun setIconNav(back: Int, home: Int, optOne: Int, optTwo: Int) {
        backMenu.setIcon(back)
        homeMenu.setIcon(home)
        optionOne.setIcon(optOne)
        optionTwo.setIcon(optTwo)
    }

    fun setVisibleNav(back: Boolean, home: Boolean, optOne: Boolean, optTwo: Boolean) {
        backMenu.isVisible = back
        homeMenu.isVisible = home
        optionOne.isVisible = optOne
        optionTwo.isVisible = optTwo
    }
}
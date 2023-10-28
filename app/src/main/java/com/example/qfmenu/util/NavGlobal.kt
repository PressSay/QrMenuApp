package com.example.qfmenu.util

import android.app.AlertDialog
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.AccountDb
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NavGlobal(
    val navBar: BottomNavigationView,
    private val navController: NavController,
    private val slidePaneLayout: SlidingPaneLayout,
    private val saveStateViewModel: SaveStateViewModel,
    val option: (itemId: Int) -> Unit,
) {
    private val backMenu = navBar.menu.findItem(R.id.backToHome)
    private val homeMenu = navBar.menu.findItem(R.id.homeMenu)
    private val optionOne = navBar.menu.findItem(R.id.optionOne)
    private val optionTwo = navBar.menu.findItem(R.id.optionTwo)

    fun impNav() {
        navBar.setOnItemSelectedListener {
            if (it.itemId == R.id.backToHome) {
                navController.popBackStack()
            }
            if (it.itemId == R.id.homeMenu) {
                slidePaneLayout.closePane()
                navBar.visibility = View.GONE
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
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
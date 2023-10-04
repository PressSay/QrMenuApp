package com.example.qfmenu

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.databinding.ActivityMainBinding
import com.example.qfmenu.viewmodels.SaveStateViewModel


val SCREEN_LARGE get() = 850

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val saveStateViewModel: SaveStateViewModel by viewModels()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val slidingPaneLayout = binding.slidingPaneLayout
        val myNavHostFragment1: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_detail) as NavHostFragment
        val myNavHostFragment2: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_login) as NavHostFragment

        val inflater1 = myNavHostFragment1.navController.navInflater
        val inflater2 = myNavHostFragment2.navController.navInflater

        val graphOrder = inflater1.inflate(R.navigation.nav_graph)
        val graphLogin = inflater2.inflate(R.navigation.login_graph)

        val navBar = binding.navBar

        binding.searchView.visibility = View.GONE

        myNavHostFragment1.navController.graph = graphOrder
        myNavHostFragment2.navController.graph = graphLogin

        saveStateViewModel.myNavHostFragment1 = myNavHostFragment1
        saveStateViewModel.myNavHostFragment2 = myNavHostFragment2

        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED

        val navHostLogin = binding.navHostLogin
        binding.navHostDetail.visibility = View.GONE

        val lp = SlidingPaneLayout.LayoutParams(
            SlidingPaneLayout.LayoutParams.MATCH_PARENT,
            SlidingPaneLayout.LayoutParams.MATCH_PARENT
        )

        navHostLogin.layoutParams = lp

    }
}
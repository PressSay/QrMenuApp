package com.example.qfmenu


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.databinding.ActivityMainBinding
import com.example.qfmenu.viewmodels.SaveStateViewModel


val SCREEN_LARGE get() = 850

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val actionBar: ActionBar? = supportActionBar

        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this.baseContext, R.color.green_surface_variant)
        val colorDrawable = ColorDrawable(Color.parseColor("#FDFDF5"))
        actionBar?.setBackgroundDrawable(colorDrawable)
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


        binding.searchView.visibility = View.GONE
        myNavHostFragment1.navController.graph = graphOrder
        myNavHostFragment2.navController.graph = graphLogin
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
                if (saveStateViewModel.isOpenSlide) {
                    if (width < SCREEN_LARGE) {
                        binding.searchView.visibility = View.GONE
                        saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                        binding.slidingPaneLayout.closePane()
                        binding.navBar.visibility = View.GONE
                        binding.searchView.visibility = View.GONE
                    }
                }
            }
        })
    }


}
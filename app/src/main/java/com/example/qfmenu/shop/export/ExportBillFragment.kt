package com.example.menumanager.shop.export

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.databinding.FragmentExportBillBinding

import com.google.android.material.bottomnavigation.BottomNavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExportBillFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExportBillFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentExportBillBinding? = null
    private val binding get() = _binding!!

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
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentExportBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slidingPaneLayout = requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density

        val homeMenu = navBar.menu.findItem(R.id.homeMenu)
        val backMenu = navBar.menu.findItem(R.id.backToHome)
        val optionOne = navBar.menu.findItem(R.id.optionOne)
        val optionTwo = navBar.menu.findItem(R.id.optionTwo)

        homeMenu.isVisible = width < SCREEN_LARGE
        backMenu.isVisible = true
        optionOne.isVisible = true
        optionTwo.isVisible = true


        homeMenu.setIcon(R.drawable.ic_home)
        backMenu.setIcon(R.drawable.ic_arrow_back)
        optionOne.setIcon(R.drawable.ic_save)
        optionTwo.setIcon(R.drawable.ic_print)

        navBar.setOnItemSelectedListener {
            if (it.itemId == R.id.homeMenu) {
                slidingPaneLayout.closePane()
                navBar.visibility = View.GONE
            }
            if (it.itemId == R.id.backToHome) {
                val startDestination = R.id.orderQueueFragment
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(startDestination, true)
                    .build()
                findNavController().navigate(startDestination, null, navOptions)
            }
            if (it.itemId == R.id.optionOne) {}
            if (it.itemId == R.id.optionTwo) {}
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
         * @return A new instance of fragment ExportBillFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExportBillFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
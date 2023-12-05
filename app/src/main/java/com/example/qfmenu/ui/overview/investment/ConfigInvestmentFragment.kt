package com.example.qfmenu.ui.overview.investment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.InvestmentDb
import com.example.qfmenu.databinding.FragmentConfigInvestmentBinding
import com.example.qfmenu.util.ConfigInvestmentAdapter
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ConfigInvestmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfigInvestmentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentConfigInvestmentBinding? = null
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
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentConfigInvestmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val recyclerView = binding.recyclerViewInvestment
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)

        val layoutFieldInvestment = binding.layoutFieldInvestment as ViewGroup
        val layout1FieldInvestment = layoutFieldInvestment.getChildAt(0) as ViewGroup
        val layout2FieldInvestment = layoutFieldInvestment.getChildAt(1) as ViewGroup

        val nameInvestment = layout1FieldInvestment.getChildAt(2)!! as TextInputEditText
        val costInvestment = layout2FieldInvestment.getChildAt(2)!! as TextInputEditText
        val investmentDao = (activity?.application as QrMenuApplication).database.investmentDao()

        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
        val navGlobal = NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel, searchView) {
            if (it == R.id.optionOne) {
            }
            if (it == R.id.optionTwo) {
                val nameInvestmentIsNullOrBlank = nameInvestment.text.isNullOrBlank()
                val costInvestmentIsnullOrBlank = costInvestment.text.isNullOrBlank()

                CoroutineScope(Dispatchers.IO).launch {
                    if (!(nameInvestmentIsNullOrBlank || costInvestmentIsnullOrBlank)) {

                        val investmentDb = InvestmentDb(
                            nameInvestment.text.toString(),
                            costInvestment.text.toString().toInt()
                        )
                        investmentDao.insert(investmentDb)
                    }
                }
            }
        }
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, R.drawable.ic_search, R.drawable.ic_plus)
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, true, optTwo = true)
        navGlobal.impNav()

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        val configInvestmentAdapter = ConfigInvestmentAdapter(
            requireContext(),
            investmentDao
        )
        recyclerView.adapter = configInvestmentAdapter

        investmentDao.getInvestments().observe(this.viewLifecycleOwner) {
            it.let {
                configInvestmentAdapter.submitList(it)
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
         * @return A new instance of fragment ConfigInvestmentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfigInvestmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
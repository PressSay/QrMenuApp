package com.example.qfmenu.ui.overview.investment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.InvestmentDb
import com.example.qfmenu.databinding.FragmentEditCreateInvestmentBinding
import com.example.qfmenu.util.EditCreateInvestmentAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditCreateInvestmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditCreateInvestmentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentEditCreateInvestmentBinding? = null
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
        _binding = FragmentEditCreateInvestmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val recyclerView = binding.recyclerViewInvestment
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)

        val backMenu = navBar.menu.findItem(R.id.backToHome)
        val homeMenu = navBar.menu.findItem(R.id.homeMenu)
        val optionOne = navBar.menu.findItem(R.id.optionOne)
        val optionTwo = navBar.menu.findItem(R.id.optionTwo)

        homeMenu.isVisible = width < SCREEN_LARGE
        backMenu.isVisible = true
        optionOne.isVisible = true
        optionTwo.isVisible = true

        homeMenu.setIcon(R.drawable.ic_home)
        backMenu.setIcon(R.drawable.ic_arrow_back)
        optionOne.setIcon(R.drawable.ic_search)
        optionTwo.setIcon(R.drawable.ic_plus)

        val layoutFieldInvestment = binding.layoutFieldInvestment as ViewGroup
        val layout1FieldInvestment = layoutFieldInvestment.getChildAt(0) as ViewGroup
        val layout2FieldInvestment = layoutFieldInvestment.getChildAt(1) as ViewGroup

        val nameInvestment = layout1FieldInvestment.getChildAt(2)!! as TextInputEditText
        val costInvestment = layout2FieldInvestment.getChildAt(2)!! as TextInputEditText
        val investmentDao = (activity?.application as QrMenuApplication).database.investmentDao()

        navBar.setOnItemSelectedListener {
            if (it.itemId == R.id.backToHome) {
                findNavController().popBackStack()
            }
            if (it.itemId == R.id.homeMenu) {
                slidePaneLayout.closePane()
                navBar.visibility = View.GONE
            }
            if (it.itemId == R.id.optionOne) {
            }
            if (it.itemId == R.id.optionTwo) {
                GlobalScope.async {
                    if (!(nameInvestment.text.isNullOrBlank() || costInvestment.text.isNullOrBlank())) {

                        val investmentDb = InvestmentDb(
                            nameInvestment.text.toString(),
                            costInvestment.text.toString().toInt()
                        )
                        investmentDao.insert(investmentDb)
                    }
                }
            }
            true
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        val editCreateInvestmentAdapter = EditCreateInvestmentAdapter(
            requireContext(),
            investmentDao
        )
        recyclerView.adapter = editCreateInvestmentAdapter

        investmentDao.getInvestments().observe(this.viewLifecycleOwner) {
            it.let {
                editCreateInvestmentAdapter.submitList(it)
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
         * @return A new instance of fragment EditCreateInvestmentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditCreateInvestmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
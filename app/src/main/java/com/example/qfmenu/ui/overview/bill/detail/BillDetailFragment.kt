package com.example.qfmenu.ui.overview.bill.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.databinding.FragmentBillDetailBinding
import com.example.qfmenu.util.BillDetailAdapter
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.DelicateCoroutinesApi
import java.text.NumberFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BillDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BillDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentBillDetailBinding? = null
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
        _binding = FragmentBillDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float =
            resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)

        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val buttonsBillListDetail = binding.buttonsBillListDetail as ViewGroup
        val descriptionBillListDetail = binding.descriptionBillListDetail as ViewGroup

        val btnBillListDetail = buttonsBillListDetail.getChildAt(0) as AppCompatImageButton
        val imgBtnBillListDetail = buttonsBillListDetail.getChildAt(1) as ImageView

        val discount = descriptionBillListDetail.getChildAt(0) as TextView
        val tax = descriptionBillListDetail.getChildAt(1) as TextView
        val total = descriptionBillListDetail.getChildAt(2) as TextView

        btnBillListDetail.setOnClickListener {
            findNavController().navigate(R.id.action_billDetailFragment_to_billCodeFragment)
        }


        val recyclerView = binding.recyclerViewBillListDetail
        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)

        val discountTextView = binding.billListDetailDiscount
        val taxTextView = binding.billListDetailTax
        val totalTextView = binding.billListDetailTotal
        var totalInt = 0

        discountTextView.text = requireContext().getString(R.string.discount, "0%")
        taxTextView.text = requireContext().getString(R.string.tax, "5%")

        val dishAmountDbList =
            saveStateViewModel.stateCustomerOrderQueue?.dishesAmountDb ?: listOf()
        dishAmountDbList.forEach {
            totalInt += (it.amount.toInt() * it.dishDb.cost)
        }
        val totalCurrency = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(totalInt)
        totalTextView.text = requireContext().getString(R.string.total, totalCurrency)

        val billAdapter = BillDetailAdapter(
            requireContext(),
            dishAmountDbList
        )

        recyclerView.adapter = billAdapter
        var isSearch = false
        val icSearch = requireActivity().findViewById<ImageView>(R.id.icSearch)
        val textSearch = requireActivity().findViewById<TextView>(R.id.textSearch)
        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)

        icSearch.setOnClickListener {
            val filtered = dishAmountDbList.filter {
                it.dishDb.name.contains(textSearch.text.toString(), true)
            }
            billAdapter.submitList(filtered)
        }

        val navGlobal = NavGlobal(
            navBar,
            findNavController(),
            slidePaneLayout,
            saveStateViewModel,
            searchView
        ) {
            if (it == R.id.optionTwo) {
                isSearch = !isSearch
                if (isSearch) {
                    billAdapter.submitList(dishAmountDbList)
                    searchView.visibility = View.VISIBLE
                } else {
                    searchView.visibility = View.GONE
                }
            }
        }
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, 0, R.drawable.ic_search)
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, false, optTwo = true)
        navGlobal.impNav()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BillDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BillDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
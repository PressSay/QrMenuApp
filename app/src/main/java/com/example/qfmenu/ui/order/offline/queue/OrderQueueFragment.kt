package com.example.qfmenu.ui.order.offline.queue

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.databinding.FragmentOrderQueueBinding
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.util.OrderQueueAdapter
import com.example.qfmenu.util.QueueBillOrderAdapter
import com.example.qfmenu.viewmodels.CustomerOrderQueue
import com.example.qfmenu.viewmodels.DishAmountDb
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.NumberFormat
import java.util.Locale




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderQueueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderQueueFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()

    private var _binding: FragmentOrderQueueBinding? = null
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
        _binding = FragmentOrderQueueBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun calTotal(
        customerId: Long,
        totalTextView: TextView,
        idOrder: TextView,
        dishesAmountDb: List<DishAmountDb>
    ) {
        var total = 0
        idOrder.text = requireContext().getString(R.string.order_id, customerId.toString())
        dishesAmountDb.forEach {
            total += (it.amount.toInt() * it.dishDb.cost)
        }
        val formattedAmount = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(total)
        totalTextView.text = requireContext().getString(R.string.total, "$formattedAmount")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slidingPaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val recyclerViewQueue = binding.horizontalListOrderQueue
        val recyclerViewDish = binding.recyclerViewItemConfirmOrderView
        val discount = binding.orderQueueDiscount
        val tax = binding.orderQueueTax
        val totalTextView = binding.orderQueueTotal
        val idOrder = binding.orderQueueIdOrder

        discount.text = requireContext().getString(R.string.discount, "0%")
        tax.text = requireContext().getString(R.string.tax, "5%")

        val orderQueueList: MutableList<CustomerOrderQueue> =
            saveStateViewModel.stateCustomerOrderQueues

        if (orderQueueList.size <= 0) {
            findNavController().popBackStack()
        } else {

            orderQueueList[0].isSelected = true
            val adapterOrderQueueBill = QueueBillOrderAdapter(
                requireContext(),
                orderQueueList[0].dishesAmountDb
            )
            var position = 0

            calTotal(
                orderQueueList[0].customerDb.customerId,
                totalTextView,
                idOrder,
                orderQueueList[0].dishesAmountDb
            )

            val adapterOrderQueue = OrderQueueAdapter(
                { ints ->
                    position = ints[0]
                    adapterOrderQueueBill.setDataset(
                        orderQueueList[ints[0]].dishesAmountDb
                    )
                    calTotal(
                        orderQueueList[ints[0]].customerDb.customerId,
                        totalTextView,
                        idOrder,
                        orderQueueList[ints[0]].dishesAmountDb
                    )
                    adapterOrderQueueBill.notifyDataSetChanged()
                },
                requireContext(),
                orderQueueList
            )

            recyclerViewQueue.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerViewDish.layoutManager = GridLayoutManager(requireContext(), spanCount)
            recyclerViewQueue.adapter = adapterOrderQueue
            recyclerViewDish.adapter = adapterOrderQueueBill

            val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
            val navGlobal =
                NavGlobal(navBar, findNavController(), slidingPaneLayout, saveStateViewModel, searchView) {
                    if (it == R.id.backToHome) {
                        saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                        saveStateViewModel.stateCustomerOrderQueues = mutableListOf()
                    }
                    if (it == R.id.optionOne) {
                        val pNext = (position + 1) % adapterOrderQueue.getDataset.size
                        adapterOrderQueue.getDataset[pNext].isSelected = true
                        adapterOrderQueue.getDataset[position].isSelected = false
                        adapterOrderQueue.notifyItemChanged(pNext)
                        adapterOrderQueue.notifyItemChanged(position)
                        adapterOrderQueueBill.setDataset(
                            orderQueueList[pNext].dishesAmountDb
                        )
                        calTotal(
                            orderQueueList[pNext].customerDb.customerId,
                            totalTextView,
                            idOrder,
                            orderQueueList[pNext].dishesAmountDb
                        )
                        adapterOrderQueueBill.notifyDataSetChanged()
                        position = pNext
                    }
                    if (it == R.id.optionTwo) {
                        saveStateViewModel.setStateCustomer(adapterOrderQueue.getDataset[position].customerDb)
                        saveStateViewModel.posCusCurrentQueue = position
                        findNavController().navigate(R.id.action_orderQueueFragment_to_prepareBillFragment)
                    }
                }
            navGlobal.setVisibleNav(true, width < SCREEN_LARGE, true, optTwo = true)
            navGlobal.setIconNav(
                R.drawable.ic_arrow_back,
                R.drawable.ic_home,
                R.drawable.ic_skip_next,
                R.drawable.ic_approve_order
            )
            navGlobal.impNav()

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderQueueFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderQueueFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
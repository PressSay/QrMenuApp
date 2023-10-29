package com.example.qfmenu.ui.member

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.AccountDb
import com.example.qfmenu.databinding.FragmentMemberBinding
import com.example.qfmenu.util.MemberAdapter
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
 * Use the [MemberFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MemberFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMemberBinding? = null
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
        _binding = FragmentMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerViewMemberManager
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val spanCount = if (width < SCREEN_LARGE) 2 else 4
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val layoutInputMemberManager = binding.itemsEditMemberManager as ViewGroup
        val layoutInputName = layoutInputMemberManager.getChildAt(0) as ViewGroup
        val layoutInputPass = layoutInputMemberManager.getChildAt(1) as ViewGroup
        val editInputName = layoutInputName.getChildAt(2) as TextInputEditText
        val editInputPass = layoutInputPass.getChildAt(2) as TextInputEditText
        val accountDao = (activity?.application as QrMenuApplication).database.accountDao()
        val memberAdapter =
            MemberAdapter(
                requireContext(),
                saveStateViewModel
            )
        val navGlobal = NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel) {
            if (it == R.id.optionTwo) {
                if (!(editInputName.text.isNullOrBlank() && editInputPass.text.isNullOrBlank())) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val accountDb = AccountDb(
                            nameRole = "staff",
                            name = "default",
                            phoneNumber = "default",
                            level = 0,
                            exp = 0,
                            email = editInputName.text.toString(),
                            password = editInputPass.text.toString(),
                            address = "default",
                            avatar = "default",
                        )
                        accountDao.insert(accountDb)
                    }
                } else {
                    AlertDialog.Builder(context)
                        .setTitle("Input Invalid")
                        .setMessage("Try Again")
                        .setPositiveButton(android.R.string.ok,
                            DialogInterface.OnClickListener { _, _ ->
                            }).show()
                }
            }
        }
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, R.drawable.ic_search, R.drawable.ic_plus)
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, optOne = true, optTwo = true)
        navGlobal.impNav();

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        recyclerView.adapter = memberAdapter
        accountDao.getAccountsWithNameRole("staff").observe(this.viewLifecycleOwner) {
            it?.let {
                memberAdapter.submitList(it)
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
         * @return A new instance of fragment MemberFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MemberFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
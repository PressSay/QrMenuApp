package com.example.qfmenu.ui.member.profile

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.AccountDb
import com.example.qfmenu.databinding.FragmentMemberProfileBinding
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MemberProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MemberProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMemberProfileBinding? = null
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
        _binding = FragmentMemberProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val nameAccount = binding.nameAccount
        val phoneAccount = binding.phoneAccount
        val addressAccount = binding.addressAccount
        val emailAccount = binding.emailAccount
        val accountDb = saveStateViewModel.stateAccountDb!!
        val navGlobal = NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel) {
            if (it == R.id.optionTwo) {
                if (!(nameAccount.text.isNullOrBlank() && phoneAccount.text.isNullOrBlank() && addressAccount.text.isNullOrBlank() && emailAccount.text.isNullOrBlank())) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val newAccountDb = AccountDb(
                            id = accountDb.id,
                            roleCreatorId = accountDb.roleCreatorId,
                            name = nameAccount.text.toString(),
                            phoneNumber = phoneAccount.text.toString(),
                            level = 0,
                            exp = 0,
                            email = emailAccount.text.toString(),
                            password = accountDb.password,
                            avatar = "empty",
                            address = addressAccount.text.toString()
                        )
                        val accountDao =
                            (activity?.application as QrMenuApplication).database.accountDao()
                        accountDao.update(newAccountDb)
                        findNavController().popBackStack()
                    }
                } else {
                    AlertDialog.Builder(context)
                        .setTitle("Input Invalid")
                        .setMessage("Try Again")
                        .setPositiveButton(android.R.string.ok
                        ) { _, _ ->
                        }.show()
                }
            }
        }
        navGlobal.setIconNav(R.id.backToHome, R.id.homeMenu, R.id.optionOne, R.id.optionTwo)
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, optOne = false, optTwo = true)
        navGlobal.impNav();
        nameAccount.setText(accountDb.name)
        phoneAccount.setText(accountDb.phoneNumber)
        addressAccount.setText(accountDb.address)
        emailAccount.setText(accountDb.email)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MemberProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MemberProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
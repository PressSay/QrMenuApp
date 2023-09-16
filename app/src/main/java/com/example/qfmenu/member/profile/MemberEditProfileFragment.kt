package com.example.qfmenu.member.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.AccountDb
import com.example.qfmenu.databinding.FragmentMemberEditProfileBinding
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MemberEditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MemberEditProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMemberEditProfileBinding? = null
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
        _binding = FragmentMemberEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density

        val backMenu = navBar.menu.findItem(R.id.backToHome)
        val homeMenu = navBar.menu.findItem(R.id.homeMenu)
        val optionOne = navBar.menu.findItem(R.id.optionOne)
        val optionTwo = navBar.menu.findItem(R.id.optionTwo)

        homeMenu.isVisible = width < SCREEN_LARGE
        backMenu.isVisible = true
        optionOne.isVisible = false
        optionTwo.isVisible = true

        homeMenu.setIcon(R.drawable.ic_home)
        backMenu.setIcon(R.drawable.ic_arrow_back)
        optionTwo.setIcon(R.drawable.ic_check_fill)

        val accountDao = (activity?.application as QrMenuApplication).database.accountDao()
        val nameAccount = binding.nameAccount
        val phoneAccount = binding.phoneAccount
        val addressAccount = binding.addressAccount
        val emailAccount = binding.emailAccount
        val passAccount = binding.passwordAccount
        val roleAccount = binding.roleAccount

        val layoutUploadAccount = binding.layoutUploadAccount as ViewGroup

        val accountDb = saveStateViewModel.stateAccountDb!!

        nameAccount.setText(accountDb.name)
        phoneAccount.setText(accountDb.phoneNumber)
        addressAccount.setText(accountDb.address)
        emailAccount.setText(accountDb.email)
        passAccount.setText(accountDb.password)
        roleAccount.setText(accountDb.roleCreatorId)


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
                if (!(nameAccount.text.isNullOrBlank() && phoneAccount.text.isNullOrBlank() && addressAccount.text.isNullOrBlank() && emailAccount.text.isNullOrBlank() && passAccount.text.isNullOrBlank() && roleAccount.text.isNullOrBlank())) {
                    GlobalScope.async {
                        val newAccountDb = AccountDb(
                            accountId = accountDb.accountId,
                            roleCreatorId = roleAccount.text.toString(),
                            name = nameAccount.text.toString(),
                            phoneNumber = phoneAccount.text.toString(),
                            level = 0,
                            email = emailAccount.text.toString(),
                            password = passAccount.text.toString(),
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
                        .setPositiveButton(android.R.string.ok,
                            DialogInterface.OnClickListener { _, _ ->
                            }).show()
                }
            }
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
         * @return A new instance of fragment MemberEditProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MemberEditProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
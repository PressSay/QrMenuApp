package com.example.qfmenu.ui.member.profile

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.AccountDb
import com.example.qfmenu.databinding.FragmentMemberProfileBinding
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.repository.StaffRepository
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
        val sharePref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token =
            sharePref.getString("token", "1|1TudhRqXy7ebTzelqlme0rD6uRAxlZVHDdROWKmW9f480231")!!
        val networkRetrofit = NetworkRetrofit(token)
        val accountDao = (activity?.application as QrMenuApplication).database.accountDao()
        val accountRepository = StaffRepository(networkRetrofit, accountDao)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val nameAccount = binding.nameAccount
        val phoneAccount = binding.phoneAccount
        val addressAccount = binding.addressAccount
        val emailAccount = binding.emailAccount
        val accountDb = saveStateViewModel.stateAccountDb!!
        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
        val uploadBtn = binding.memberUpload
        val delBtn = binding.memberDel
        val imgMem = binding.imageMember
        val levelEditText = binding.levelAccount
        val expEditText = binding.expAccount



        var curUri: Uri? = null

        val registry = requireActivity()
            .activityResultRegistry.register(
                "key",
                this,
                ActivityResultContracts.GetContent()
            ) { uri ->
                // Handle the returned Uri
                uri?.let {
                    imgMem.setImageURI(it)
                    imgMem.scaleType = ImageView.ScaleType.FIT_XY
                    curUri = it
                }
            }
        uploadBtn.setOnClickListener {
            registry.launch("image/*")
        }

        delBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    accountRepository.deleteStaff(accountDb)
                    accountDao.delete(accountDb)
                } catch (e: Exception) {
                    accountDao.delete(accountDb)
                }
                findNavController().popBackStack()
            }
        }

        val navGlobal = NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel, searchView) {
            if (it == R.id.optionTwo) {
                if (!(nameAccount.text.isNullOrBlank() && phoneAccount.text.isNullOrBlank()
                            && addressAccount.text.isNullOrBlank() && emailAccount.text.isNullOrBlank())
                    && levelEditText.text.toString().toInt() >= 0 && expEditText.text.toString().toInt() >= 0) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val newAccountDb = AccountDb(
                            id = accountDb.id,
                            nameRole = accountDb.nameRole,
                            name = nameAccount.text.toString(),
                            phoneNumber = phoneAccount.text.toString(),
                            level = levelEditText.text.toString().toInt(),
                            exp = expEditText.text.toString().toInt(),
                            email = emailAccount.text.toString(),
                            password = accountDb.password,
                            avatar = "empty",
                            address = addressAccount.text.toString()
                        )
                        try {
                            accountRepository.updateStaff(newAccountDb)
                            accountDao.update(newAccountDb)
                        } catch (e: Exception) {
                            accountDao.update(newAccountDb)
                        }
                    }
                } else {
                    AlertDialog.Builder(context)
                        .setTitle("Input Invalid")
                        .setMessage("Try Again")
                        .setPositiveButton(android.R.string.ok
                        ) { _, _ -> }.show()
                }
            }
        }
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, 0, R.drawable.ic_save)
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, optOne = false, optTwo = true)
        navGlobal.impNav();
        nameAccount.setText(accountDb.name)
        phoneAccount.setText(accountDb.phoneNumber)
        addressAccount.setText(accountDb.address)
        emailAccount.setText(accountDb.email)
        levelEditText.setText(accountDb.level.toString())
        expEditText.setText(accountDb.exp.toString())
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
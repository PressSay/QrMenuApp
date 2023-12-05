package com.example.qfmenu.ui.user.profile

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.databinding.FragmentProfileBinding
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentProfileBinding? = null
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
        navBar.visibility = View.VISIBLE
        val navGlobal =
            NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel, searchView) {
            }

        val name = binding.profileName
        val phone = binding.phoneProfile
        val email = binding.emailProfile
        val address = binding.addressProfile
        val exp = binding.exp
        val role = binding.role
        val imgProfile = binding.imgProfile
        val sharePref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharePref.getString("token", "") ?: ""
        val networkRetrofit = NetworkRetrofit(token)

        if (token.isNotEmpty()) {
            Log.d("token", token)
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = networkRetrofit.user().user()
                    Log.d("response", response.toString())
                    if (response.isSuccessful) {
                        val userNetwork = response.body()
                        if (userNetwork != null) {
                            val phoneText = requireContext().getString(R.string.phone_profile, userNetwork.phoneNumber)
                            val emailText = requireContext().getString(R.string.email_profile, userNetwork.email)
                            val addressText = requireContext().getString(R.string.address_profile, userNetwork.address)
                            val expText = requireContext().getString(R.string.exp_profile, userNetwork.exp.toString())
                            val roleText = requireContext().getString(R.string.role_profile, userNetwork.nameRole)
                            val imgPath = userNetwork.image.source

                            name.text = userNetwork.name
                            phone.text = phoneText
                            email.text = emailText
                            address.text = addressText
                            exp.text = expText
                            role.text = roleText

                            try {
                                Picasso.get().load("${NetworkRetrofit.BASE_URL}/${imgPath}")
                                    .fit().centerCrop().into(imgProfile)
                            } catch (networkError: IOException) {
                                Log.d("NoInternet", true.toString())
                            }
                        }
                    }
                } catch (_: IOException) {
                    AlertDialog.Builder(context)
                        .setTitle("Login")
                        .setMessage("No Internet")
                        .setPositiveButton(
                            android.R.string.ok
                        ) { _, _ ->
                            popToStartDestination(findNavController())
                            if (width < SCREEN_LARGE)
                                slidePaneLayout.closePane()
                        }.show()
                }
            }
        }


        navGlobal.setVisibleNav(false, width < SCREEN_LARGE, false, optTwo = false)
        navGlobal.setIconNav(
            R.drawable.ic_arrow_back,
            R.drawable.ic_home,
            R.drawable.ic_search,
            R.drawable.ic_menu
        )
        navGlobal.impNav()
        searchView.visibility = View.GONE
    }

    private fun popToStartDestination(navController: NavController) {
        val startDestination = navController.graph.startDestination
        val navOptions = NavOptions.Builder()
            .setPopUpTo(startDestination, true)
            .build()
        navController.navigate(startDestination, null, navOptions)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
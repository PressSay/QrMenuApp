package com.example.qfmenu.ui.user.login


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.R
import com.example.qfmenu.databinding.FragmentLoginBinding
import com.example.qfmenu.network.NetworkRetrofit
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentLoginBinding? = null
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun navigateToMainFragment(
        navBar: BottomNavigationView,
        navHostDetail: FragmentContainerView,
        navHostLogin: FragmentContainerView,
        isHaveToken: Boolean
    ) {
        if (isHaveToken) {
            requireActivity().runOnUiThread {
                navHostDetail.visibility = View.VISIBLE
            }
            val pixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                425f, resources.displayMetrics
            )
            val lpNavHostLogin = SlidingPaneLayout.LayoutParams(
                pixels.toInt(),
                SlidingPaneLayout.LayoutParams.MATCH_PARENT,
            )
            lpNavHostLogin.weight = 1 / 3F
            navHostLogin.layoutParams = lpNavHostLogin
            navBar.visibility = View.VISIBLE
            val myNavHostFragment2 =
                requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_login) as NavHostFragment
            myNavHostFragment2.navController.navigate(R.id.action_loginFragment_to_mainFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnLogin = binding.btnSignIn
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val email = binding.emailAccount
        val password = binding.passwordAccount
        val navHostLogin =
            requireActivity().findViewById<FragmentContainerView>(R.id.nav_host_login)
        val navHostDetail =
            requireActivity().findViewById<FragmentContainerView>(R.id.nav_host_detail)
        val sharePref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editPref = sharePref.edit()

        navHostDetail.visibility = View.GONE
        navBar.visibility = View.GONE
        val lp = SlidingPaneLayout.LayoutParams(
            SlidingPaneLayout.LayoutParams.MATCH_PARENT,
            SlidingPaneLayout.LayoutParams.MATCH_PARENT
        )
        navHostLogin.layoutParams = lp

        val token = sharePref.getString("token", "") ?: ""
        val networkRetrofit = NetworkRetrofit(token)
        val offlineMode = binding.offlineMode

        offlineMode.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            AlertDialog.Builder(context)
                                .setTitle("Offline Mode")
                                .setMessage("This Mean You Can't Sync!!")
                                .setPositiveButton(
                                    android.R.string.ok
                                ) { _, _ ->
                                    navigateToMainFragment(
                                        navBar,
                                        navHostDetail,
                                        navHostLogin,
                                        true
                                    )
                                }.show()
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {}
                    }
                }

            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
        }

        if (token.isNotEmpty()) {
            Log.d("token", token)
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = networkRetrofit.user().user()
                    Log.d("response", response.toString())
                    if (response.isSuccessful) {
                        val userNetwork = response.body()
                        Log.d("User-Network", userNetwork.toString())
                        if (userNetwork != null) {
                            val isHaveToken = token.isNotBlank()
                            requireActivity().runOnUiThread {
                                navigateToMainFragment(
                                    navBar,
                                    navHostDetail,
                                    navHostLogin,
                                    isHaveToken
                                )
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
                        }.show()
                }
            }
        }


        btnLogin.setOnClickListener {
            if (email.text?.isNotBlank() == true && password.text?.isNotBlank() == true) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val response = networkRetrofit.user().login(
                            email.text.toString(),
                            password.text.toString()
                        )
                        if (response.isSuccessful) {
                            val tokenKey = response.body()?.token
                            if (tokenKey != null) {
                                try {
                                    editPref.putString("token", tokenKey)
                                    editPref.apply()
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                                navigateToMainFragment(
                                    navBar,
                                    navHostDetail,
                                    navHostLogin,
                                    true
                                )
                            } else {
                                AlertDialog.Builder(context)
                                    .setTitle("Login")
                                    .setMessage("Wrong Password Or Email!!")
                                    .setPositiveButton(
                                        android.R.string.ok
                                    ) { _, _ ->
                                    }.show()
                            }
                        }
                    } catch (_: IOException) {
                        AlertDialog.Builder(context)
                            .setTitle("Login")
                            .setMessage("No Internet")
                            .setPositiveButton(
                                android.R.string.ok
                            ) { _, _ ->
                            }.show()
                    }
                }
            } else {
                AlertDialog.Builder(context)
                    .setTitle("Login")
                    .setMessage("Input Invalid!")
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _, _ ->
                    }.show()
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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
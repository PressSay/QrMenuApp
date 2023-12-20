package com.example.qfmenu.ui.menu.config

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.databinding.FragmentConfigMenuBinding
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.repository.MenuRepository
import com.example.qfmenu.util.ConfigMenuAdapter
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.MenuViewModel
import com.example.qfmenu.viewmodels.MenuViewModelFactory
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
 * Use the [ConfigMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfigMenuFragment : Fragment() {
    private var _binding: FragmentConfigMenuBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        _binding = FragmentConfigMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuDao = (activity?.application as QrMenuApplication).database.menuDao()
        val categoryDao = (activity?.application as QrMenuApplication).database.categoryDao()
        val dishDao = (activity?.application as QrMenuApplication).database.dishDao()
        val menuViewModel: MenuViewModel by viewModels {
            MenuViewModelFactory(
                dishDao,
                categoryDao,
                menuDao
            )
        }
        val menuSubmitParent = binding.menuSubmit as ViewGroup
        val menuSubmitString =
            (menuSubmitParent.getChildAt(0) as ViewGroup).getChildAt(2) as TextInputEditText
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val recyclerView = binding.recyclerViewEditCreateMenu
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        var isSearch = false
        val icSearch = requireActivity().findViewById<AppCompatImageButton>(R.id.icSearch)
        val textSearch = requireActivity().findViewById<TextView>(R.id.textSearch)
        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
        val sharePref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharePref.getString("token", "") ?: ""
        val networkRetrofit = NetworkRetrofit(token)
        val menuRepository = MenuRepository(networkRetrofit, menuDao, categoryDao, dishDao)
        val menus = menuViewModel.menus
        val configMenuAdapter = ConfigMenuAdapter(
            menuViewModel,
            requireContext(),
            saveStateViewModel,
            menuRepository
        )



        val navGlobal = NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel, searchView) { it ->
            if (it == R.id.optionOne) {
                menus.observe(this.viewLifecycleOwner) {
                    configMenuAdapter.submitList(it)
                }
                isSearch = !isSearch
                searchView.visibility =
                    if (isSearch) View.VISIBLE else View.GONE
            }
            if (it == R.id.optionTwo) {
                if (menuSubmitString.text.toString().isNotEmpty()) {
                    val menuDb = if (configMenuAdapter.currentList.size == 0) {
                        MenuDb(name = menuSubmitString.text.toString(), isUsed = true)
                    } else {
                        MenuDb(name = menuSubmitString.text.toString(), isUsed = false)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            menuRepository.createMenu(menuDb)
                            menuViewModel.insertMenu(menuDb)
                        } catch (e: Exception) {
                            menuViewModel.insertMenu(menuDb)
                        }
                    }
                }
            }
        }
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, true, optTwo = true)
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, R.drawable.ic_search, R.drawable.ic_save)
        navGlobal.impNav()

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        recyclerView.adapter = configMenuAdapter
        icSearch.setOnClickListener {
            menus.observe(this.viewLifecycleOwner) { menuDbs ->
                val filtered = menuDbs.filter {  it.name.contains(textSearch.text.toString(), ignoreCase = true) }
                if (filtered.isNotEmpty()) {
                    configMenuAdapter.submitList(filtered)
                } else {
                    configMenuAdapter.submitList(menuDbs)
                }
            }
        }
        menus.observe(this.viewLifecycleOwner) {
            configMenuAdapter.submitList(it)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ConfigMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfigMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package com.example.qfmenu.menu.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.viewmodels.MenuViewModel
import com.example.qfmenu.viewmodels.MenuViewModelFactory
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.databinding.FragmentEditCreateMenuBinding
import com.example.qfmenu.menu.MenuEditListAdapter
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditCreateMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditCreateMenuFragment : Fragment() {
    private var _binding: FragmentEditCreateMenuBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()

    private val menuViewModel: MenuViewModel by viewModels {
        MenuViewModelFactory(
            (activity?.application as QrMenuApplication).database.dishDao(),
            (activity?.application as QrMenuApplication).database.categoryDao(),
            (activity?.application as QrMenuApplication).database.menuDao()
        )
    }

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
        _binding = FragmentEditCreateMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuSubmitParent = binding.menuSubmit as ViewGroup
        val menuSubmitString =
            (menuSubmitParent.getChildAt(0) as ViewGroup).getChildAt(2) as TextInputEditText

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val recyclerView = binding.recyclerViewEditCreateMenu
        val spanCount = if (width < SCREEN_LARGE) 1 else 2

        val backMenu = navBar.menu.findItem(R.id.backToHome)
        val homeMenu = navBar.menu.findItem(R.id.homeMenu)
        val optionOne = navBar.menu.findItem(R.id.optionOne)
        val optionTwo = navBar.menu.findItem(R.id.optionTwo)

        optionOne.isVisible = true
        optionTwo.isVisible = true
        backMenu.isVisible = true
        homeMenu.isVisible = width < SCREEN_LARGE

        homeMenu.setIcon(R.drawable.ic_home)
        backMenu.setIcon(R.drawable.ic_arrow_back)
        optionOne.setIcon(R.drawable.ic_search)
        optionTwo.setIcon(R.drawable.ic_save)

        val menus = menuViewModel.menus

        val menuEditListAdapter = MenuEditListAdapter(
            menuViewModel,
            requireContext(),
            saveStateViewModel,
        )

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        recyclerView.adapter = menuEditListAdapter

        menus.observe(this.viewLifecycleOwner) {
            it.let {
                menuEditListAdapter.submitList(it)
            }
        }

        navBar.setOnItemSelectedListener {
            if (it.itemId == R.id.homeMenu) {
                slidePaneLayout.closePane()
                navBar.visibility = View.GONE
            }
            if (it.itemId == R.id.backToHome) {
                findNavController().popBackStack()
            }
            if (it.itemId == R.id.optionOne) {

            }
            if (it.itemId == R.id.optionTwo) {
                val menuDb = if (menuEditListAdapter.currentList.size == 0) {
                    MenuDb(menuName = menuSubmitString.text.toString(), isUsed = true)
                } else {
                    MenuDb(menuName = menuSubmitString.text.toString(), isUsed = false)
                }
                menuViewModel.insertMenu(menuDb)
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
         * @return A new instance of fragment EditCreateMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditCreateMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
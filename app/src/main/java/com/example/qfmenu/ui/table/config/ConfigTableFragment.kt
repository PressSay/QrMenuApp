package com.example.qfmenu.ui.table.config

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
import com.example.qfmenu.database.entity.TableDb
import com.example.qfmenu.databinding.FragmentConfigTableBinding
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ConfigTableFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfigTableFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentConfigTableBinding? = null
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
        _binding = FragmentConfigTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slidingPaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val btnLock = binding.lockTableBtn
        val btnUnlock = binding.unlockTableBtn


        val tableDao = (activity?.application as QrMenuApplication).database.tableDao()
        val tableDb = saveStateViewModel.stateTableDb!!

        btnLock.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val newTableDb = TableDb(
                    tableDb.tableId,
                    requireContext().getString(R.string.lock)
                )
                tableDao.update(tableDb = newTableDb)
                findNavController().popBackStack()
            }
        }

        btnUnlock.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val newTableDb = TableDb(
                    tableDb.tableId,
                    requireContext().getString(R.string.status_table_free)
                )
                tableDao.update(tableDb = newTableDb)
                findNavController().popBackStack()
            }
        }

        val navGlobal = NavGlobal(navBar, findNavController(), slidingPaneLayout, saveStateViewModel) {}
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, false, optTwo = false)
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, 0, 0)
        navGlobal.impNav()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditWattingTableFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfigTableFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package com.example.qfmenu.ui.shop

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.TableDb
import com.example.qfmenu.databinding.FragmentConfigShopBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ConfigShopFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfigShopFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentConfigShopBinding? = null
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
        _binding = FragmentConfigShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scrollView = binding.scrollViewOfConfig
        val width = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)

        val btnMenu = binding.btnMenuConfigShop
        val btnReview = binding.btnReviewConfigShop
        val btnOverView = binding.btnOverviewConfigShop
        val btnStaff = binding.btnStaffConfigShop

        btnMenu.setOnClickListener {
            findNavController().navigate(R.id.action_configShopFragment_to_editCreateMenuFragment)
        }
        btnReview.setOnClickListener {
            findNavController().navigate(R.id.action_configShopFragment_to_reviewListFragment)
        }
        btnOverView.setOnClickListener {
            findNavController().navigate(R.id.action_configShopFragment_to_overviewFragment)
        }
        btnStaff.setOnClickListener {
            findNavController().navigate(R.id.action_configShopFragment_to_memberManagerFragment)
        }

        val backMenu = navBar.menu.findItem(R.id.backToHome)
        val homeMenu = navBar.menu.findItem(R.id.homeMenu)
        val optionOne = navBar.menu.findItem(R.id.optionOne)
        val optionTwo = navBar.menu.findItem(R.id.optionTwo)

        backMenu.isVisible = false
        homeMenu.isVisible = width < SCREEN_LARGE
        optionOne.isVisible = false
        optionTwo.isVisible = true

        homeMenu.setIcon(R.drawable.ic_home)
        optionTwo.setIcon(R.drawable.ic_save)

        val shopName = binding.shopName
        val shopAddress = binding.shopAddress
        val enShopReview = binding.enShopReview
        val numberOfTable = binding.numberOfTableTextField
        val promotionVipShop = binding.promotionVipShop
        val promotionBill = binding.promotionBill


        val path = requireContext().filesDir
        val letDirectory = File(path, "Config Shop")

        if (!letDirectory.exists())
            letDirectory.mkdirs()

        val file = File(letDirectory, "config_shop.txt")


        val isEnableReviewArray = arrayOf<String>("Enable", "Disable")
        var isEnableReview: Boolean = true
        val ad: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            isEnableReviewArray
        )

        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        enShopReview.adapter = ad

        enShopReview.onItemSelectedListener = object : OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                isEnableReview = isEnableReviewArray[position] == "Enable"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

        }

        if (file.exists()) {
            val inputAsString = FileInputStream(file).bufferedReader().use { it.readText() }
            val arrayInputString = inputAsString.split(" ")
            if (arrayInputString.size == 7) {
                val isEnableReviewPos: Int = if (arrayInputString[2] == "true") 0 else 1

                shopName.setText(arrayInputString[0])
                shopAddress.setText(arrayInputString[1])
                enShopReview.setSelection(isEnableReviewPos,true);
                numberOfTable.setText(arrayInputString[3])
                promotionVipShop.setText(arrayInputString[4])
                promotionBill.setText(arrayInputString[5])
            }
            arrayInputString.forEach {
                Log.d("FileShop", it)
            }
            Log.d("FileShopSize", arrayInputString.size.toString())
        }

        binding.btnSaveNumberOfTable.setOnClickListener {
            if (numberOfTable.text?.isNotBlank() == true) {

                CoroutineScope(Dispatchers.IO).launch {
                    val tableDao = (activity?.application as QrMenuApplication).database.tableDao()
                    async { tableDao.getTables() }.await().forEach {
                        tableDao.delete(it)
                    }
                    for (i in 1..numberOfTable.text.toString().toInt()) {
                        val tableDb = TableDb(
                            tableId = i.toLong(),
                            "free"
                        )
                        Log.d("table", "true " + tableDb.tableId)
                        tableDao.insert(tableDb)
                    }
                }
            }
        }

        navBar.setOnItemSelectedListener {
            if (it.itemId == R.id.homeMenu) {
                slidePaneLayout.closePane()
                navBar.visibility = View.GONE
            }
            if (it.itemId == R.id.optionTwo) {
                file.writeText(
                    shopName.text.toString() + " " +
                            shopAddress.text.toString() + " " +
                            isEnableReview.toString() + " " +
                            numberOfTable.text.toString() + " " +
                            promotionVipShop.text.toString() + " " +
                            promotionBill.text.toString() + " "
                )
                AlertDialog.Builder(context)
                    .setTitle("Config Shop")
                    .setMessage("Your config has been saved")
                    .setPositiveButton(android.R.string.ok,
                        DialogInterface.OnClickListener { _, _ ->
                        }).show()
            }
            true
        }

//        scrollView.post {
//            scrollView.fullScroll(View.FOCUS_DOWN)
//        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ConfigShopFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfigShopFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
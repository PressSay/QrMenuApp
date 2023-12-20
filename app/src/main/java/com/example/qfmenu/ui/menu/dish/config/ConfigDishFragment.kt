package com.example.qfmenu.ui.menu.dish.config

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.databinding.FragmentConfigDishBinding
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.repository.MenuRepository
import com.example.qfmenu.repository.TableRepository
import com.example.qfmenu.util.ConfigDishAdapter
import com.example.qfmenu.util.NavGlobal
import com.example.qfmenu.viewmodels.DishViewModel
import com.example.qfmenu.viewmodels.DishViewModelFactory
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

/**
 * A simple [Fragment] subclass.
 * Use the [ConfigDishFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ConfigDishFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentConfigDishBinding? = null
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
        _binding = FragmentConfigDishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuDao = (activity?.application as QrMenuApplication).database.menuDao()
        val categoryDao = (activity?.application as QrMenuApplication).database.categoryDao()
        val dishDao = (activity?.application as QrMenuApplication).database.dishDao()
        val tableDao  = (activity?.application as QrMenuApplication).database.tableDao()
        val dishViewModel: DishViewModel by viewModels {
            DishViewModelFactory(
                dishDao,
                categoryDao
            )
        }
        val sharePref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharePref.getString("token", "") ?: ""
        val networkRetrofit = NetworkRetrofit(token)
        val menuRepository = MenuRepository(networkRetrofit, menuDao, categoryDao, dishDao)


        val recyclerView = binding.recyclerViewEditCreateDish
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = (resources.displayMetrics.widthPixels / resources.displayMetrics.density)
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val linearTextField0 = binding.linearEditTextEditCreateDish as ViewGroup
        // first linear
        val linearTextField1 = linearTextField0.getChildAt(0) as ViewGroup
        val categoryEditText = linearTextField1.getChildAt(2) as TextInputEditText
        val categorySaveBtn = linearTextField1.getChildAt(3) as ImageButton
        // second linear
        val linearTextField2 = linearTextField0.getChildAt(1) as ViewGroup
        val titleDish = linearTextField2.getChildAt(2) as TextInputEditText
        val costDish = linearTextField2.getChildAt(3) as TextInputEditText
        // description
        val descriptionDish = linearTextField0.getChildAt(2) as TextInputEditText

        var categoryDb = saveStateViewModel.stateCategoryDb
        val listAdapter = ConfigDishAdapter(dishViewModel, requireContext(), saveStateViewModel)

        var isSearch = false
        val icSearch = requireActivity().findViewById<AppCompatImageButton>(R.id.icSearch)
        val textSearch = requireActivity().findViewById<TextView>(R.id.textSearch)
        icSearch.setOnClickListener {
            dishViewModel.getDishesLiveData(saveStateViewModel.stateCategoryDb.categoryId)
                .observe(this.viewLifecycleOwner) { dishDbs ->
                    val filtered = dishDbs.filter {
                        it.name.contains(
                            textSearch.text.toString(),
                            ignoreCase = true
                        )
                    }
                    if (filtered.isNotEmpty()) {
                        listAdapter.submitList(filtered)
                    } else {
                        listAdapter.submitList(dishDbs)
                    }
                }
        }
        dishViewModel.getDishesLiveData(saveStateViewModel.stateCategoryDb.categoryId)
            .observe(this.viewLifecycleOwner) {
                listAdapter.submitList(it)
            }
        categoryEditText.setText(saveStateViewModel.stateCategoryDb.name)
        categorySaveBtn.setOnClickListener {
            if (categoryEditText.text.toString() != categoryDb.name) {
                categoryDb = CategoryDb(
                    categoryDb.categoryId,
                    categoryEditText.text.toString(),
                    categoryDb.menuId
                )
                CoroutineScope(Dispatchers.IO).launch {
                    menuRepository.updateCategoryNet(categoryDb)
                    categoryDao.update(categoryDb)
                }
            }
        }
        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        recyclerView.adapter = listAdapter
        val searchView = requireActivity().findViewById<LinearLayout>(R.id.searchView)
        val navGlobal =
            NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel, searchView) { it ->
                if (it == R.id.optionOne) {
                    dishViewModel.getDishesLiveData(saveStateViewModel.stateCategoryDb.categoryId)
                        .observe(this.viewLifecycleOwner) {
                            listAdapter.submitList(it)
                        }
                    isSearch = !isSearch
                    searchView.visibility =
                        if (isSearch) View.VISIBLE else View.GONE
                }
                if (it == R.id.optionTwo) {
                    if (titleDish.text.toString().isNotEmpty() && costDish.text.toString()
                            .isNotEmpty() && descriptionDish.text.toString().isNotEmpty()) {
                        val dishDb = DishDb(
                            name = titleDish.text.toString(),
                            cost = costDish.text.toString().toInt(),
                            description = descriptionDish.text.toString(),
                            categoryId = saveStateViewModel.stateCategoryDb.categoryId
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                menuRepository.createDish(dishDb)
                                dishViewModel.insertDish(dishDb)
                            } catch (e: Exception) {
                                dishViewModel.insertDish(dishDb)
                            }
                        }
                    }
                    Log.d("optionTwo", "optionTwo")
                }
            }
        navGlobal.setVisibleNav(true, width < SCREEN_LARGE, true, optTwo = true)
        navGlobal.setIconNav(
            R.drawable.ic_arrow_back,
            R.drawable.ic_home,
            R.drawable.ic_search,
            R.drawable.ic_check_fill
        )
        navGlobal.impNav()
    }

    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ConfigDishFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfigDishFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
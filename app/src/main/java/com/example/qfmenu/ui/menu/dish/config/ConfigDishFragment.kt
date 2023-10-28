package com.example.qfmenu.ui.menu.dish.config

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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

    private val dishViewModel: DishViewModel by viewModels {
        DishViewModelFactory(
            (activity?.application as QrMenuApplication).database.dishDao(),
            (activity?.application as QrMenuApplication).database.categoryDao(),

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
        _binding = FragmentConfigDishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharePref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val recyclerView = binding.recyclerViewEditCreateDish
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = (resources.displayMetrics.widthPixels / resources.displayMetrics.density)
        val spanCount = if (width < SCREEN_LARGE) 1 else 2
        val slidePaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val linearTextField0 = binding.linearEditTextEditCreateDish as ViewGroup
        val linearTextField1 = linearTextField0.getChildAt(1) as ViewGroup
        val categoryEditText = linearTextField1.getChildAt(2) as TextInputEditText
        val categorySaveBtn = linearTextField1.getChildAt(3) as ImageButton
        val linearTextField2 = linearTextField0.getChildAt(2) as ViewGroup
        val titleDish = linearTextField2.getChildAt(2) as TextInputEditText
        val costDish = linearTextField2.getChildAt(3) as TextInputEditText
        val descriptionDish = linearTextField0.getChildAt(3) as TextInputEditText
        var categoryDb = saveStateViewModel.stateCategoryDb
        val categoryDao = (activity?.application as QrMenuApplication).database.categoryDao()
        val listAdapter = ConfigDishAdapter(dishViewModel, requireContext(), saveStateViewModel)
        val linearUploadImage = linearTextField0.getChildAt(0) as ViewGroup
        val imageViewUpload = linearUploadImage.getChildAt(0) as ImageView
        val btnUploadImage = linearUploadImage.getChildAt(1) as AppCompatButton
        val token = sharePref.getString("token", "") ?: ""
        val networkRetrofit = NetworkRetrofit(token)

        val imageNet = ImageNet(
            requireActivity().activityResultRegistry
        ) {
            val curUri = it
            imageViewUpload.setImageURI(curUri)
            btnUploadImage.setOnClickListener {
                val filesDir = requireContext().filesDir
                val file = File(filesDir, "image.jpg")

                val inputStream = requireActivity().contentResolver.openInputStream(curUri)
                val outputStream = FileOutputStream(file)
                inputStream!!.copyTo(outputStream)
                inputStream.close()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val requestBody: RequestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("forWhat", "dish")
                            .addFormDataPart("dishId", "1")
                            .addFormDataPart(
                                "image",
                                file.name,
                                file.asRequestBody("image/*".toMediaTypeOrNull())
                            ).build()
                        val response = networkRetrofit.image().create(requestBody)

                        Log.d("ImageRetrofit", response.toString())
                    } catch (networkError: IOException) {
                        Log.d("NoInternet", true.toString())
                    }
                }
            }
        }
        lifecycle.addObserver(imageNet)

        imageViewUpload.setOnClickListener {
            imageNet.selectImage()
        }

        var isSearch = false
        val icSearch = requireActivity().findViewById<AppCompatImageButton>(R.id.icSearch)
        val textSearch = requireActivity().findViewById<TextView>(R.id.textSearch)
        icSearch.setOnClickListener {
            dishViewModel.getDishesLiveData(saveStateViewModel.stateCategoryDb.categoryId)
                .observe(this.viewLifecycleOwner) { dishDbs ->
                    val filtered = dishDbs.filter {  it.dishName.contains(textSearch.text.toString(), ignoreCase = true) }
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
                CoroutineScope(Dispatchers.IO).launch {
                    categoryDb = CategoryDb(
                        categoryDb.categoryId,
                        categoryEditText.text.toString(),
                        categoryDb.menuId
                    )
                    categoryDao.update(categoryDb)
                }
            }
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        recyclerView.adapter = listAdapter


        val navGlobal = NavGlobal(navBar, findNavController(), slidePaneLayout, saveStateViewModel) { it ->
            if (it == R.id.optionOne) {
                dishViewModel.getDishesLiveData(saveStateViewModel.stateCategoryDb.categoryId)
                    .observe(this.viewLifecycleOwner) {
                        listAdapter.submitList(it)
                    }
                isSearch = !isSearch
                requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility =
                    if (isSearch) View.VISIBLE else View.GONE
            }
            if (it == R.id.optionTwo) {
                val dishDb = DishDb(
                    dishName = titleDish.text.toString(),
                    categoryId = categoryDb.categoryId,
                    description = descriptionDish.text.toString(),
                    cost = costDish.text.toString().toInt()
                )
                dishViewModel.insertDish(dishDb)
            }
        }
        navGlobal.setVisibleNav(true, width <  SCREEN_LARGE, true, optTwo = true)
        navGlobal.setIconNav(R.drawable.ic_arrow_back, R.drawable.ic_home, R.drawable.ic_search, R.drawable.ic_check_fill)
        navGlobal.impNav()
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
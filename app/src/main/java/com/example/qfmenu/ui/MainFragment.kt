package com.example.qfmenu.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.qfmenu.MainActivity
import com.example.qfmenu.QrMenuApplication
import com.example.qfmenu.R
import com.example.qfmenu.SCREEN_LARGE
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.database.entity.OrderDb
import com.example.qfmenu.databinding.FragmentMainBinding
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.repository.CustomerRepository
import com.example.qfmenu.repository.InvestmentRepository
import com.example.qfmenu.repository.MenuRepository
import com.example.qfmenu.repository.ReviewRepository
import com.example.qfmenu.repository.StaffRepository
import com.example.qfmenu.repository.TableRepository
import com.example.qfmenu.util.WebSocketListen
import com.example.qfmenu.viewmodels.SaveStateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMainBinding? = null
    private lateinit var ws: WebSocket
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
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun popToStartDestination(navController: NavController) {
        val startDestination = navController.graph.startDestination
        val navOptions = NavOptions.Builder()
            .setPopUpTo(startDestination, true)
            .build()
        navController.navigate(startDestination, null, navOptions)
    }

    private val saveStateViewModel: SaveStateViewModel by activityViewModels()


    private fun formatNumRev(number: Long): String {
        return if (number > 1000000000) {
            "${number / 1000000000}B"
        } else if (number > 1000000) {
            "${number / 1000000}M"
        } else if (number > 1000) {
            "${number / 1000}K"
        } else {
            number.toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val swipeRefreshLayout = binding.refreshLayout
        val myNavHostFragment1: NavHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_detail) as NavHostFragment
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.navBar)
        val width: Float = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val slidingPaneLayout =
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        val sharePref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editPref = sharePref.edit()
        val token =
            sharePref.getString("token", "1|1TudhRqXy7ebTzelqlme0rD6uRAxlZVHDdROWKmW9f480231")!!

        val menuDao = (activity?.application as QrMenuApplication).database.menuDao()
        val categoryDao = (activity?.application as QrMenuApplication).database.categoryDao()
        val dishDao = (activity?.application as QrMenuApplication).database.dishDao()
        val customerDao = (activity?.application as QrMenuApplication).database.customerDao()
        val orderDao = (activity?.application as QrMenuApplication).database.orderDao()
        val customerDishDao =
            (activity?.application as QrMenuApplication).database.customerDishCrossRefDao()
        val investmentDao = (activity?.application as QrMenuApplication).database.investmentDao()
        val accountDb = (activity?.application as QrMenuApplication).database.accountDao()
        val tableDao = (activity?.application as QrMenuApplication).database.tableDao()
        val reviewDao = (activity?.application as QrMenuApplication).database.reviewDao()
        val networkRetrofit = NetworkRetrofit(token)
        val menuRepo = MenuRepository(networkRetrofit, menuDao, categoryDao, dishDao)
        val customerRepo =
            CustomerRepository(networkRetrofit, customerDao, customerDishDao, orderDao)
        val investmentRepo = InvestmentRepository(networkRetrofit, investmentDao)
        val staffRepository = StaffRepository(networkRetrofit, accountDb)
        val tableRepository = TableRepository(networkRetrofit, tableDao)
        val reviewRepository = ReviewRepository(networkRetrofit, reviewDao)

        val accountBtn = binding.accountBtn
        val totalReview = binding.totalReview
        // Refresh function for the layout

        swipeRefreshLayout.setOnRefreshListener {
            CoroutineScope(Dispatchers.IO).launch {
                val isSync = if (sharePref.getString("sync", "") == "") {
                    editPref.putString("sync", "1")
                    editPref.apply()
                    false
                } else {
                    true
                }
                async { customerRepo.fetchCustomer(isSync) }.await()
                async { menuRepo.fetchMenu(isSync) }.await()
                async { menuRepo.fetchCategory(isSync) }.await()
                async { menuRepo.fetchDish(isSync) }.await()
                async { reviewRepository.fetchReview() }.await()
                async { investmentRepo.fetchInvestment() }.await()
                async { staffRepository.fetchStaff() }.await()
                async { tableRepository.fetchTable() }.await()

                swipeRefreshLayout.isRefreshing = false
            }
        }


        if (width < SCREEN_LARGE) {
            navBar.visibility = View.GONE
        } else {
            navBar.menu.findItem(R.id.homeMenu).isVisible = false
        }

        reviewDao.countRevBill().observe(this.viewLifecycleOwner) { countBill ->
            reviewDao.countRevDish().observe((this.viewLifecycleOwner)) { countDish ->
                if (countBill != null && countDish != null) {
                    val countRev = countBill + countDish
                    val formatted = formatNumRev(countRev)
                    totalReview.text = formatted
                }
            }
        }

        accountBtn.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.profileFragment)

            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                slidingPaneLayout.openPane()
            }

        }

        binding.btnStartOrder.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            saveStateViewModel.stateIsStartOrder = true
            saveStateViewModel.stateIsOffOnOrder = false
            saveStateViewModel.setStateDishesDb(listOf())
            saveStateViewModel.stateCategoryPosition = 0
            saveStateViewModel.stateDishesByCategories.clear()
            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                slidingPaneLayout.openPane()
            }
            requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility = View.GONE
        }

        binding.btnBuyTakeAway.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            saveStateViewModel.stateIsStartOrder = false
            saveStateViewModel.stateIsOffOnOrder = false
            saveStateViewModel.setStateDishesDb(listOf())
            saveStateViewModel.stateCategoryPosition = 0
            saveStateViewModel.stateDishesByCategories.clear()
            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                slidingPaneLayout.openPane()
            }
            requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility = View.GONE
        }

        binding.btnConfigShop.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.configShopFragment)

            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                slidingPaneLayout.openPane()
            }
            requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility = View.GONE
        }

        binding.btnOfflineList.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.orderUnconfirmedFragment)
            saveStateViewModel.stateIsOffOnOrder = true
            saveStateViewModel.setStateDishesDb(listOf())
            saveStateViewModel.stateDishesByCategories.clear()
            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                slidingPaneLayout.openPane()
            }
            requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility = View.GONE
        }

        binding.btnOnlineList.setOnClickListener {
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.onlineOrderFragment)
            saveStateViewModel.setStateDishesDb(listOf())
            saveStateViewModel.stateDishesByCategories.clear()
            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                slidingPaneLayout.openPane()
            }
            requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility = View.GONE
        }

        binding.btnTableUnlock.setOnClickListener {
            saveStateViewModel.stateIsTableUnClock = true
            popToStartDestination(myNavHostFragment1.navController)
            myNavHostFragment1.navController.navigate(R.id.tableOrderFragment)
            if (width < SCREEN_LARGE) {
                saveStateViewModel.isOpenSlide = !saveStateViewModel.isOpenSlide
                slidingPaneLayout.openPane()
            }
            requireActivity().findViewById<LinearLayout>(R.id.searchView).visibility = View.GONE
        }

        val client = OkHttpClient()
        val request = Request.Builder().url(NetworkRetrofit.BASE_URL_SOCKET).build()
        val listener = WebSocketListen {
            val rltJson = JSONObject(it)
            Log.e("rltJson", rltJson.toString())
            if ("data" in it) {
                val dataStr = rltJson["data"] as String
                val dataJson = JSONObject(dataStr)
                if ("customerId" in dataStr) {
                    Log.d("rltJson", dataJson["orderId"].toString())
                    CoroutineScope(Dispatchers.IO).launch {
                        val response = networkRetrofit.customer().findOne(dataJson["customerId"].toString())
                        if (response.isSuccessful) {
                            val customer = response.body()!!
                            val customerDb = CustomerDb(
                                customerId = customer.customerId,
                                accountCreatorId = customer.userId,
                                dateExpireCode = customer.dateExpireCode,
                                name = customer.name,
                                code = customer.code,
                                phone = customer.phoneNumber,
                                address = customer.address,
                                created = customer.created_at.split(" ")[0]
                            )
                            val orderDb = OrderDb(
                                customerOwnerId = customer.customerId,
                                status = customer.order.status,
                                payments = customer.order.payments,
                                promotion = customer.order.promotion,
                                tableId = customer.order.nameTable.toLong()
                            )
                            customerDao.insert(customerDb)
                            orderDao.insert(orderDb)
                            for (custmr in customer.customerDishCrossRefs) {
                                val customerDishCrossRefs = CustomerDishDb(
                                    customerId = custmr.customerId,
                                    dishId = custmr.dishId,
                                    amount = custmr.amount,
                                    promotion = custmr.promotion
                                )
                                customerDishDao.insert(customerDishCrossRefs)
                            }

                            showNotification(requireContext().getString(R.string.notification), requireContext().getString(R.string.have_new_customer, orderDb.tableId.toString()))
                        }
                    }
                }
            }

            // insert customer to database
        }
        ws = client.newWebSocket(request, listener)
        ws.request()

    }

    private fun showNotification(title: String, message: String) {
        val mNotificationManager = requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("YOUR_CHANNEL_ID",
                "YOUR_CHANNEL_NAME",
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "YOUR_NOTIFICATION_CHANNEL_DESCRIPTION"
            mNotificationManager.createNotificationChannel(channel)
        }
        val mBuilder = NotificationCompat.Builder(requireContext(), "YOUR_CHANNEL_ID")
            .setSmallIcon(R.mipmap.ic_launcher) // notification icon
            .setContentTitle(title) // title for notification
            .setContentText(message)// message for notification
            .setAutoCancel(true) // clear notification after click
        val intent = Intent(requireContext(), MainActivity::class.java)
        val pi = PendingIntent.getActivity(requireActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager.notify(0, mBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        ws.close(1000, "Goodbye !")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
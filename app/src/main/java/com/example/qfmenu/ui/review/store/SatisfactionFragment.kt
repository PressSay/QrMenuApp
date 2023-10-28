package com.example.qfmenu.ui.review.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.qfmenu.R
import com.example.qfmenu.databinding.FragmentSatisfactionBinding
import com.example.qfmenu.viewmodels.SaveStateViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SatisfactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SatisfactionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentSatisfactionBinding? = null
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
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSatisfactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layout1 = binding.layoutStoreReviewYN as ViewGroup
        val btnYes = layout1.getChildAt(1) as AppCompatButton
        val btnNo = layout1.getChildAt(2) as AppCompatButton
        val btnIdk = layout1.getChildAt(3) as AppCompatButton

        btnYes.setOnClickListener {
            saveStateViewModel.stateReviewDb = 1
            findNavController().navigate(R.id.action_satisfactionFragment_to_reviewCommentFragment)
        }

        btnNo.setOnClickListener {
            saveStateViewModel.stateReviewDb = 0
            findNavController().navigate(R.id.action_satisfactionFragment_to_reviewCommentFragment)
        }

        btnIdk.setOnClickListener {
            saveStateViewModel.stateReviewDb = -1
            findNavController().navigate(R.id.action_satisfactionFragment_to_reviewCommentFragment)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SatisfactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SatisfactionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package com.mobile.cw2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.cw2.databinding.FragHomepageBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class HomePage : Fragment() {

    private var _binding: FragHomepageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragHomepageBinding.inflate(inflater, container, false)

        val view: View = binding.root


        return view

//        val view =  inflater.inflate(R.layout.fragment_second, container, false)
//        val addBtn = view.findViewById<Button>(R.id.floatingActionButton)
//        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homePage_to_addPage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
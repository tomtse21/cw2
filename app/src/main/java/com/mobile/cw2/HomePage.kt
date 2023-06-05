package com.mobile.cw2

import MyAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.cw2.databinding.FragHomepageBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class HomePage : Fragment() {

    private var _binding: FragHomepageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var mAdapter : MyAdapter
    val values = mutableListOf("Sony", "Samsung", "Huawei", "Apple")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragHomepageBinding.inflate(inflater, container, false)

        val view: View = binding.root
        val mRecyclerView = view.findViewById<RecyclerView>(R.id.list)
        mRecyclerView.layoutManager = LinearLayoutManager(view.context);
        mRecyclerView.itemAnimator = DefaultItemAnimator()

        val helper = ItemTouchHelper(object  : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if(direction == ItemTouchHelper.RIGHT) {
                    val item = viewHolder.adapterPosition
                    values.removeAt(item)
                    mAdapter.notifyDataSetChanged()
                }
            }
        })
        helper.attachToRecyclerView(mRecyclerView)
        mAdapter = MyAdapter(values, R.layout.my_row, view.context)
        mRecyclerView.adapter = mAdapter

        return view

//        val view =  inflater.inflate(R.layout.fragment_second, container, false)
//        val addBtn = view.findViewById<Button>(R.id.floatingActionButton)
//        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homePage_to_mainActivity2)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
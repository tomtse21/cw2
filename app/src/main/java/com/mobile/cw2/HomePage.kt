package com.mobile.cw2

import MyAdapter
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ClipboardManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mobile.cw2.databinding.FragHomepageBinding
import com.mobile.cw2.uitl.AppDatabase
import com.mobile.cw2.uitl.QA
import com.mobile.cw2.uitl.QADao
import java.util.Random

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class HomePage : Fragment() {

    private var _binding: FragHomepageBinding? = null
    private lateinit var appDb : AppDatabase
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var mAdapter : MyAdapter
//    val values = mutableListOf("Sony", "Samsung", "Huawei", "Apple")
    private var qaDao: QADao? = null
    private var qas : List<QA>? = null
    private lateinit var swipeContainer : SwipeRefreshLayout
    private lateinit var mRecyclerView : RecyclerView
    private lateinit var view: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragHomepageBinding.inflate(inflater, container, false)


        view = binding.root
         swipeContainer = view.findViewById<SwipeRefreshLayout>(R.id.swipeContainer)


        mRecyclerView = view.findViewById<RecyclerView>(R.id.list)
        mRecyclerView.layoutManager = LinearLayoutManager(view.context);
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        appDb = AppDatabase.getDatabase(view.context)
        qaDao = appDb.qaDao()
        val helper = ItemTouchHelper(object  : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = viewHolder.adapterPosition


                if(direction == ItemTouchHelper.RIGHT) {

                    qaDao?.delete( qas?.get(item))
                    refreshList()
                }
                if(direction == ItemTouchHelper.LEFT ){
                    //TODO
                    val textToCopy = qas?.get(item)?.answer.toString()
                    Toast.makeText(activity, "Text ${textToCopy} copied to clipboard", Toast.LENGTH_LONG).show()
                }
            }
        })
        helper.attachToRecyclerView(mRecyclerView)
        qas  = qaDao?.loadAllQa()
        mAdapter = MyAdapter(qas, R.layout.my_row, view.context)
        mRecyclerView.adapter = mAdapter

        swipeContainer.setOnRefreshListener {
            refreshList()
        }

        return view

    }



    private fun refreshList(){
        swipeContainer.isRefreshing = false
        qas = null
        qas  = qaDao?.loadAllQa()
        mAdapter = MyAdapter(qas, R.layout.my_row, view.context)
        mRecyclerView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
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
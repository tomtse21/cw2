package com.mobile.cw2

import MyAdapter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mobile.cw2.databinding.FragHomepageBinding
import com.mobile.cw2.uitl.AppDatabase
import com.mobile.cw2.uitl.QA
import com.mobile.cw2.uitl.QADao


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class HomePage : Fragment() {

    internal enum class ButtonsState {
        GONE, LEFT_VISIBLE, RIGHT_VISIBLE
    }
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
    private val deleteBg = ColorDrawable(Color.RED)
    private val updateBg = ColorDrawable(Color.GRAY)
    private val buttonWidth = 300f
    private val buttonShowedState: ButtonsState = ButtonsState.GONE
    private var buttonInstance: RectF? = null
    private var swipeBack = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
                if (swipeBack) {
                    swipeBack = buttonShowedState !== ButtonsState.GONE
                    return 0
                }
                return super.convertToAbsoluteDirection(flags, layoutDirection)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = viewHolder.adapterPosition


                if(direction == ItemTouchHelper.RIGHT) {

                    qaDao?.delete( qas?.get(item))
                    refreshList()
                }
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                actionState: Int, isCurrentlyActive: Boolean,
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                drawButtons(c, viewHolder);
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

    private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val buttonWidthWithoutPadding: Float = buttonWidth - 20
        val corners = 16f
        val itemView = viewHolder.itemView
        val p = Paint()
        val leftButton = RectF(
            itemView.left - buttonWidthWithoutPadding,
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat()
        )
        p.setColor(Color.GRAY)
        c.drawRoundRect(leftButton, corners, corners, p)
        drawText("DEL", c, leftButton, p)
    }

    private fun drawText(text: String, c: Canvas, button: RectF, p: Paint) {
        val textSize = 30f
        p.setColor(Color.WHITE)
        p.setAntiAlias(true)
        p.setTextSize(textSize)
        val textWidth: Float = p.measureText(text)
        c.drawText(text, button.centerX() - textWidth / 2, button.centerY() + textSize / 2, p)
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
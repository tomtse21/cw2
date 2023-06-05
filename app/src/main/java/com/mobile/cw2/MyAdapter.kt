import android.view.LayoutInflater
import com.mobile.cw2.R
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(var myList: List<String>, var rowLayout: Int, var context: Context) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return ViewHolder(v)
    }
    override fun getItemCount(): Int {
        return myList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = myList.get(position)
        holder.myName.text = entry
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var pic : ImageView
        lateinit var myName : TextView
        init {
            myName = itemView.findViewById(R.id.name)
            //pic = itemView.findViewById(R.id.picture)
        }
    }
}
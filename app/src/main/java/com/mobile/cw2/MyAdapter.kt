import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.mobile.cw2.R
import com.mobile.cw2.uitl.QA

class MyAdapter(var myList: List<QA>?, var rowLayout: Int, var context: Context) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return ViewHolder(v)
    }
    override fun getItemCount(): Int {
        return myList?.size ?: 0
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = myList!!.get(position)
        holder.question.text = entry.question
        holder.answer.text = entry.answer

        holder.dismissAnswerBtn.setOnClickListener{
            holder.answer.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var question : TextView
        lateinit var answer : TextView

        lateinit var dismissAnswerBtn: ShapeableImageView

        init {
            question = itemView.findViewById(R.id.question)
            answer = itemView.findViewById(R.id.answer)
            dismissAnswerBtn = itemView.findViewById(R.id.dismissAnswerBtn)

        }


    }
}
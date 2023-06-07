import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
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

//        holder.copyBtn.setOnClickListener{
//            holder.answer.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//        }

        holder.itemView.setOnClickListener{
                val textToCopy = holder.answer.text
                val clipboardManager =
                    context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager as ClipboardManager
                val clipData = ClipData.newPlainText("text", textToCopy)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(context, textToCopy, Toast.LENGTH_LONG).show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var question : TextView
        lateinit var answer : TextView

        lateinit var copyBtn: ShapeableImageView

        init {
            question = itemView.findViewById(R.id.question)
            answer = itemView.findViewById(R.id.answer)
            copyBtn = itemView.findViewById(R.id.copyBtn)

        }


    }
}
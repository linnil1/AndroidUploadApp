package tw.edu.ntu.bime.toolmen.demo_android

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.card.view.*


class CardAdapter :
    RecyclerView.Adapter<CardAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CardAdapter.MyViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card, parent, false) as CardView
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.cardView.card_text.text = result_datas[position].resText
        Glide.with(holder.cardView)
            .load(result_datas[position].resImg)
            .into(holder.cardView.card_image)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = result_datas.size
}
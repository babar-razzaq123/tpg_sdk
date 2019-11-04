package telenor.com.ep_v1_sdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_payment_method.view.*
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.config.model.AllowedPaymentMethods

class EPPaymentMethodAdapter (var clickListener: (AllowedPaymentMethods) -> Unit):RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    val list = arrayListOf<AllowedPaymentMethods>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.item_payment_method, parent, false)
        return ListHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListHolder).bind(list[position], clickListener)
    }

    fun addItem(item: AllowedPaymentMethods) {
        list.add(item)
        notifyDataSetChanged()
    }

    fun addItems(items: List<AllowedPaymentMethods>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): AllowedPaymentMethods {
        return list[position]
    }

    override fun getItemCount() = list.size


    class ListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(allowedPaymentMethods: AllowedPaymentMethods, clickListener: (AllowedPaymentMethods) -> Unit) {


            itemView.imgIcon.setImageResource(if (allowedPaymentMethods.icon != -1) {
                allowedPaymentMethods.icon
            } else {
                0
            })

            itemView.listTitle.text = allowedPaymentMethods.description
            itemView.setOnClickListener { (clickListener(allowedPaymentMethods)) }
        }
    }
}

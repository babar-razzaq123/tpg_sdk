package telenor.com.ep_v1_sdk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ComplexColorCompat
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.config.model.DirectDebitBank
import telenor.com.ep_v1_sdk.config.model.ExpiryYears

class YearDropDownAdapter(val context: Context, var listItemsTxt: ArrayList<ExpiryYears>) : BaseAdapter() {


    val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.view_drop_down_menu, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }

        // setting adapter item height programatically.

        val params = view.layoutParams
        params.height = 100
        view.layoutParams = params

        if(position==0){
        vh.label.text = listItemsTxt.get(position).value

            vh.label.setTextColor(ContextCompat.getColor(context, R.color.grey))
        }
        else{

            vh.label.text = listItemsTxt.get(position).value
            vh.label.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
        return view
    }

    override fun getItem(position: Int): Any? {

        return null

    }

    override fun getItemId(position: Int): Long {

        return 0

    }

    override fun getCount(): Int {
        return listItemsTxt.size
    }

    private class ItemRowHolder(row: View?) {

        val label: TextView

        init {
            this.label = row?.findViewById(R.id.txtDropDownLabel) as TextView

        }
    }
}
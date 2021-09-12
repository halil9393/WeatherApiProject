package com.example.weatherapiproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapiproject.R
import com.example.weatherapiproject.model.Parent

//class RecyclerViewAdapter(val dataList: ArrayList<Parent>):RecyclerView.Adapter<RecyclerViewAdapter.DataViewHolder>(){
class RecyclerViewAdapter(val dataList: ArrayList<Parent>):RecyclerView.Adapter<RecyclerViewAdapter.DataViewHolder>(){

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private var myListener: MyOnItemClickListener? = null

    interface MyOnItemClickListener {
        fun myOnItemClick(itemView: View?, position: Int)
    }

    fun mySetOnItemClickListener(listener: MyOnItemClickListener?) {
        myListener = listener
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {

        var inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_parent, parent, false)
        return DataViewHolder(view)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        var parent : Parent = dataList.get(position)
        holder.setData(parent)

        holder.itemView.setOnClickListener  { myListener!!.myOnItemClick(holder.itemView,position) }
//        holder.view.setOnClickListener  { myListener!!.myOnItemClick(holder.view,position) }   // arasında bir fark gozukmedi şimdilik

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun getItemCount(): Int {
        return dataList.size
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    class DataViewHolder(var view: View) : RecyclerView.ViewHolder(view){

        var title: TextView = view.findViewById(R.id.tvTitle)
        var locationType: TextView= view.findViewById(R.id.tvLocationType)
        var woeid: TextView= view.findViewById(R.id.tvWoeid)
        var lattLong: TextView= view.findViewById(R.id.tvLattLong)


        fun setData(parent: Parent){

            title.text = parent.title
            locationType.text = parent.locationType
            woeid.text = parent.woeid.toString()
            lattLong.text = parent.lattLong





        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // resfresh için kullanılacak
    fun updateDataList(newDataList: List<Parent>) {
        dataList.clear()
        dataList.addAll(newDataList)
        notifyDataSetChanged()
    }
}


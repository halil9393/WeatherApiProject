package com.example.weatherapiproject.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.example.weatherapiproject.BuildConfig
import com.example.weatherapiproject.R
import com.example.weatherapiproject.model.ConsolidatedWeather
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ConsolidateWeatherAdapter(val dataList: ArrayList<ConsolidatedWeather>) :
    RecyclerView.Adapter<ConsolidateWeatherAdapter.DataViewHolder>() {

    //////////////////////////////////   CLICK EVENTI   ////////////////////////////////////////////

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
        val view = inflater.inflate(R.layout.item_weathermodel, parent, false)
        return DataViewHolder(view)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        var consolidatedWeather: ConsolidatedWeather = dataList.get(position)
        holder.setData(consolidatedWeather,position)


        holder.itemView.setOnClickListener {
            myListener!!.myOnItemClick(
                holder.itemView,
                position,
            )
        }


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun getItemCount(): Int {
        return dataList.size
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    class DataViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        var tvTarih: TextView = view.findViewById(R.id.tvTarih)
        var tvHavaDurumu: TextView = view.findViewById(R.id.tvHavaDurumu)
        var tvEnYuksekSicaklik: TextView = view.findViewById(R.id.tvEnYuksekSicaklik)
        var tvEnDusukSicaklik: TextView = view.findViewById(R.id.tvEnDusukSicaklik)
        var ivHavaDurumuResim: ImageView = view.findViewById(R.id.ivHavaDurumuResim)
        var tvWindSpeed: TextView = view.findViewById(R.id.tvWindSpeed)
        var tvWindDirection: TextView = view.findViewById(R.id.tvWindDirection)
        var tvAirPressure: TextView = view.findViewById(R.id.tvAirPressure)
        var tvHumidity: TextView = view.findViewById(R.id.tvHumidity)
        var tvVisibility: TextView = view.findViewById(R.id.tvVisibility)
        var tvPredictability: TextView = view.findViewById(R.id.tvPredictability)


        var subItem: LinearLayout = view.findViewById(R.id.sub_item)

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun setData(consolidatedWeather: ConsolidatedWeather,position: Int) {


            var sdf = SimpleDateFormat("yyyy-MM-dd")    // gelen veri tipi
            val mDate = sdf.parse(consolidatedWeather.applicableDate)
            sdf = SimpleDateFormat("EEE-dd MMM")       // istenilen format
            var duzeltilmisDate = sdf.format(mDate)           // cevirme islemi

            if(position==0){
                // bugün tarihi yazılmayacak, Bugun diye yazdırılacak
                tvTarih.text = "Today"
                subItem.visibility = View.VISIBLE   // ilk acılısta genisletilmis olsun
                consolidatedWeather.expanded = true
            }else{
                tvTarih.text = duzeltilmisDate
                subItem.visibility = View.GONE  // swipe sonrası default hale gelmesi icin
                consolidatedWeather.expanded = false
            }


            tvHavaDurumu.text = consolidatedWeather.weatherStateName
            tvEnYuksekSicaklik.text = consolidatedWeather.maxTemp.toInt().toString() + " \u2103"
            tvEnDusukSicaklik.text = consolidatedWeather.minTemp.toInt().toString() + " \u2103"



            // details texts
            tvWindSpeed.text= String.format("%.2f",consolidatedWeather.windSpeed) + " Mph"        // virgülden sonra 2 basamak yapdık
            tvWindDirection.text = consolidatedWeather.windDirectionCompass
            tvAirPressure.text = consolidatedWeather.airPressure.toString() + " mBar"
            tvHumidity.text = consolidatedWeather.humidity.toString() + " %"
            tvVisibility.text = String.format("%.2f",consolidatedWeather.visibility) + " Miles"
            tvPredictability.text = consolidatedWeather.predictability.toString() + " %"


            // image
            val path: Uri =
                Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/drawable/" + consolidatedWeather.weatherStateAbbr)
            Log.i(
                "tag_path",
                "weather icon path : $path  ---- ${consolidatedWeather.weatherStateAbbr}"
            )

            Glide
                .with(itemView)
                .load(path)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .dontTransform()// kalite dusmesin, bu uygulamada gerek yok
                .into(ivHavaDurumuResim);

        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // swipe isleminde resfresh için kullanılacak
    fun updateDataList(newDataList: List<ConsolidatedWeather>) {
        dataList.clear()
        dataList.addAll(newDataList)
        notifyDataSetChanged()
    }

}
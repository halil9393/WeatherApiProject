package com.example.weatherapiproject.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.weatherapiproject.R
import com.example.weatherapiproject.adapter.ConsolidateWeatherAdapter
import com.example.weatherapiproject.databinding.ActivityThirdBinding
import com.example.weatherapiproject.model.ConsolidatedWeather
import com.example.weatherapiproject.utils.MyAnimations
import com.example.weatherapiproject.utils.MyEventBusEvents
import com.example.weatherapiproject.viewmodel.SecondViewModel
import com.example.weatherapiproject.viewmodel.ThirdViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.androidx.viewmodel.ext.android.viewModel


class ThirdActivity : AppCompatActivity() {


    val thirdViewModel by viewModel<ThirdViewModel>()
    private lateinit var binding: ActivityThirdBinding
    private val consolidateWeatherAdapter = ConsolidateWeatherAdapter(arrayListOf())

    lateinit var mDataList: List<ConsolidatedWeather>
    var woeid: String = ""

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("tag_flow", "ThirdActivity => onCreate")

        // recycler setup
        binding.recyclerViewWeather.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewWeather.adapter = consolidateWeatherAdapter
        consolidateWeatherAdapter.mySetOnItemClickListener(object :
            ConsolidateWeatherAdapter.MyOnItemClickListener {
            override fun myOnItemClick(itemView: View?, position: Int) {

                // tıklanılan view genişleyip detay gosterecek
                val consolidatedWeather:ConsolidatedWeather = mDataList.get(position)
                val show = toggleLayout(!consolidatedWeather.expanded,itemView!!,
                    itemView.findViewById(R.id.sub_item) as LinearLayout
                )
                consolidatedWeather.expanded = show
            }

        })



        // listedeki view ler arasına renk katmak cizgi cekmek icin
        val decorator = DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
        decorator.setDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.divider_line
            )!!
        )
        binding.recyclerViewWeather.addItemDecoration(decorator)


        binding.ivBack2.setOnClickListener {
            thirdViewModel.backPressEvent(this)
        }


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun toggleLayout(isExpanded:Boolean,v:View, layoutExpand:LinearLayout):Boolean {
//        MyAnimations.toggleArrow(v, isExpanded);  // takla attırılacak spinner iconu için kullanilacak
        if (isExpanded) {
            MyAnimations.expand(layoutExpand)
        } else {
            MyAnimations.collapse(layoutExpand)
        }
        return isExpanded

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onStart() {
        super.onStart()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.recyclerViewWeather.visibility = View.GONE
            binding.tvError.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            thirdViewModel.refreshdata(woeid)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        observeLiveData()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun observeLiveData() {

        thirdViewModel.dataSuccess.observe(this, Observer { data ->
            data?.let {
                binding.recyclerViewWeather.visibility = View.VISIBLE
                consolidateWeatherAdapter.updateDataList(data)
                mDataList = data
            }
        })

        thirdViewModel.dataError.observe(this, Observer { error ->
            error?.let {
                if (it) {
                    binding.tvError.visibility = View.VISIBLE
                } else {
                    binding.tvError.visibility = View.GONE
                }
            }
        })

        thirdViewModel.dataLoading.observe(this, Observer { loading ->
            loading?.let {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewWeather.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

    }



    //////////////////////////////        EVENT BUS       //////////////////////////////////////////

    @Subscribe(sticky = true)
    internal fun getLattLongFromEventBus(gelenObje: MyEventBusEvents.woeidDegeriniGonder) {

        woeid = gelenObje.woeid
        thirdViewModel.refreshdata(gelenObje.woeid)
        binding.tvTitle.text = gelenObje.title

    }

    override fun onResume() {
        super.onResume()
        // unregister duzgun calısmıyor, kontrol gerekli
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // yukarıda kontrol ekledigimiz icin buna da ekledik
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }

    }

    // Fragment için
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        EventBus.getDefault().register(this)
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        EventBus.getDefault().unregister(this)
//    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}
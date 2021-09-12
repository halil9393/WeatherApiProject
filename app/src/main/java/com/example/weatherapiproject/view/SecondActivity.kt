package com.example.weatherapiproject.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapiproject.R
import com.example.weatherapiproject.adapter.RecyclerViewAdapter
import com.example.weatherapiproject.databinding.ActivitySecondBinding
import com.example.weatherapiproject.model.Parent
import com.example.weatherapiproject.utils.MyEventBusEvents
import com.example.weatherapiproject.viewmodel.MainViewModel
import com.example.weatherapiproject.viewmodel.SecondViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.androidx.viewmodel.ext.android.viewModel


class SecondActivity : AppCompatActivity() {


    val secondViewModel by viewModel<SecondViewModel>()
    private val recyclerViewAdapter = RecyclerViewAdapter(arrayListOf())
    private lateinit var binding: ActivitySecondBinding

    lateinit var mDataList: List<Parent>

    var latt: String = ""
    var long: String = ""

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("tag_flow", "SecondActivity => onCreate")


        recyclerViewAdapter.mySetOnItemClickListener(object :
            RecyclerViewAdapter.MyOnItemClickListener {
            override fun myOnItemClick(itemView: View?, position: Int) {

                Log.i("tag_click","Basılan item: ${mDataList[position].title}")

                secondViewModel.goToThirdActivityWithWoeid(
                    this@SecondActivity,
                    mDataList[position].woeid.toString(),
                    mDataList[position].title
                )
            }

        })



        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = recyclerViewAdapter


        // listedeki view ler arasına renk katmak cizgi cekmek icin
        val decorator = DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
        decorator.setDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.divider_line
            )!!
        )
        binding.recyclerView.addItemDecoration(decorator)



        binding.etSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                secondViewModel.performSearch(binding.etSearch.text.toString())
                return@OnEditorActionListener true
            }
            false
        })


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onStart() {
        super.onStart()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.recyclerView.visibility = View.GONE
            binding.tvError.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            secondViewModel.refreshdata(latt, long)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        observeLiveData()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onBackPressed() {
//        super.onBackPressed()
        secondViewModel.backPressEvent(this)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun observeLiveData() {
        secondViewModel.dataSuccess.observe(this, Observer { data ->
            data?.let {
                binding.recyclerView.visibility = View.VISIBLE
                recyclerViewAdapter.updateDataList(data)
                mDataList = data
            }

        })
        secondViewModel.dataError.observe(this, Observer { error ->
            error?.let {
                if (it) {
                    binding.tvError.visibility = View.VISIBLE
                } else {
                    binding.tvError.visibility = View.GONE
                }
            }

        })
        secondViewModel.dataLoading.observe(this, Observer { loading ->
            loading?.let {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }

        })
    }

    //////////////////////////////        EVENT BUS       //////////////////////////////////////////

    @Subscribe(sticky = true)
    internal fun getLattLongFromEventBus(gelenObje: MyEventBusEvents.lattVeLongDegerleriniGonder) {

        latt = gelenObje.latt
        long = gelenObje.long
        binding.tvLatt.text = latt
        binding.tvLong.text = long
        secondViewModel.refreshdata(latt, long)

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
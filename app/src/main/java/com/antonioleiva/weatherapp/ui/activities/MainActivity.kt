package com.antonioleiva.weatherapp.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import com.antonioleiva.weatherapp.R
import com.antonioleiva.weatherapp.domain.commands.RequestForecastCommand
import com.antonioleiva.weatherapp.domain.model.ForecastList
import com.antonioleiva.weatherapp.extensions.DelegatesExt
import com.antonioleiva.weatherapp.ui.adapters.ForecastListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), ToolbarManager {

    val TAG = "MainActivity"

    val zipCode: Long by DelegatesExt.preference(this, SettingsActivity.ZIP_CODE,
            SettingsActivity.DEFAULT_ZIP)
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        //设置RecyclerView的LayoutManager
        forecastList.layoutManager = LinearLayoutManager(this)
        //添加RecyclerView的滚动监听
        attachToScroll(forecastList)
    }

    override fun onResume() {
        super.onResume()
        //加载天气数据
        loadForecast()
    }

    private fun loadForecast() = async(UI) {
        //在后台线程池中执行网络请求
        val result = bg { RequestForecastCommand(zipCode).execute() }
        println("async")
        updateUI(result.await())
    }

    private fun updateUI(weekForecast: ForecastList) {
        Log.d(TAG, "updateUI")
        val adapter = ForecastListAdapter(weekForecast) {
            startActivity<DetailActivity>(DetailActivity.ID to it.id,
                    DetailActivity.CITY_NAME to weekForecast.city)
        }
        //设置RecyclerView的Adapter
        forecastList.adapter = adapter
        //设置Toolbar标题
        toolbarTitle = "${weekForecast.city} (${weekForecast.country})"
    }
}

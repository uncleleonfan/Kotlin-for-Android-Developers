package com.antonioleiva.weatherapp.domain.datasource

import com.antonioleiva.weatherapp.data.db.ForecastDb
import com.antonioleiva.weatherapp.data.server.ForecastServer
import com.antonioleiva.weatherapp.domain.model.Forecast
import com.antonioleiva.weatherapp.domain.model.ForecastList
import com.antonioleiva.weatherapp.extensions.firstResult

class ForecastProvider(val sources: List<ForecastDataSource> = ForecastProvider.SOURCES) {

    companion object {
        val DAY_IN_MILLIS = 1000 * 60 * 60 * 24
        val SOURCES by lazy { listOf(ForecastDb(), ForecastServer()) }
    }

    /**
     *     fun requestByZipCode(zipCode: Long, days: Int): ForecastList = requestToSources ({
                 val res = it.requestForecastByZipCode(zipCode, todayTimeSpan())
                if (res != null && res.size >= days) res else null
            })
     */
    fun requestByZipCode(zipCode: Long, days: Int): ForecastList = requestToSources {
        println("requestByZipCode ${zipCode} ${days}")
        val res = it.requestForecastByZipCode(zipCode, todayTimeSpan())
        if (res == null) {
            println("result is null")
        } else {
            println("result size is ${res.size}")
        }
        if (res != null && res.size >= days) res else null
    }

    //请求天气详情
    fun requestForecast(id: Long): Forecast = requestToSources { it.requestDayForecast(id) }

    private fun todayTimeSpan() = System.currentTimeMillis() / DAY_IN_MILLIS * DAY_IN_MILLIS

    //泛型函数 高阶函数
    //requestToSources接收一个函数作为参数f: (ForecastDataSource) -> T？  函数的参数为ForecastDataSource类型，返回值为T
    // sources.firstResult { f(it) } =  sources.firstResult({ f(it) })
    //firstResult会遍历 List<ForecastDataSource>集合，直到返回的ForecastList不为空
    private fun <T : Any> requestToSources(f: (ForecastDataSource) -> T?): T
            = sources.firstResult { f(it) }

}
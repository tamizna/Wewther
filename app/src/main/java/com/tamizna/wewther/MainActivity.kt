package com.tamizna.wewther

import android.app.SearchManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.tamizna.wewther.databinding.ActivityMainBinding
import com.tamizna.wewther.services.WeatherApiService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherApiService: WeatherApiService
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = (application as CustomApplication).retrofit
        weatherApiService = retrofit.create(WeatherApiService::class.java)

        loadWeatherData("Jakarta")
    }

    private fun loadWeatherData(city: String) {
        // with coroutine
        GlobalScope.launch {

            try {

                val result =
                    weatherApiService.getWeather(city, "cbef379ddf4a2dcb5f352ad82a730e8c")

                runOnUiThread {
                    binding.txtCity.text = result.name
                    binding.txtTemp.text = convertKelvinToCelcius(result.main.temp)
                    binding.txtWeather.text = result.weather.first().main
                    binding.txtHumidity.text = "${result.main.humidity}%"
                    binding.txtPressure.text = result.main.pressure.toString()
                    setImageTemp(result.weather.first().main)

                    val countryId = result.sys.country
                    Glide.with(this@MainActivity)
                        .load("https://www.countryflags.io/$countryId/flat/64.png")
                        .placeholder(R.drawable.ic_loading_circle).error(R.drawable.ic_broken_image)
                        .into(binding.imgCountry)
                }
            } catch (e: HttpException) {
                Log.d("ERROR", "Exception ${e.message}")

                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Data not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Throwable) {
                Log.d("ERROR", "Ooops: Something else went wrong")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Data not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun convertKelvinToCelcius(temp: Double): String {
        val celcius = temp - 273.15F
        return "${celcius.toInt()}\u2103"
    }

    private fun setImageTemp(temp: String) {

        val imageTemp: Int

        when (temp) {
            "Clear" -> {
                imageTemp = R.drawable.ic_outline_wb_sunny_24
            }
            "Clouds" -> {
                imageTemp = R.drawable.ic_outline_cloud_24
            }
            "Rain" -> {
                imageTemp = R.drawable.ic_rain
            }
            "Mist" -> {
                imageTemp = R.drawable.ic_mist
            }
            "Thunderstorm" -> {
                imageTemp = R.drawable.ic_thunderstorm
            }
            else -> {
                imageTemp = R.drawable.ic_outline_wb_sunny_24
            }
        }

        Glide.with(this@MainActivity)
            .load(imageTemp)
            .placeholder(R.drawable.ic_loading_circle).error(R.drawable.ic_broken_image)
            .into(binding.imgTemp)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val search: MenuItem = menu.findItem(R.id.action_search)
        searchView = search.actionView as SearchView
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query?.trim()!!.isNotEmpty()) {
            loadWeatherData(query)
            searchView.setQuery("", false)
            searchView.clearFocus()
            searchView.onActionViewCollapsed()
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

}
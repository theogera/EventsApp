package com.example.app

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.adapters.EventAdapter
import com.example.app.databinding.ActivityEventsBinding
import com.example.app.models.Event
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventsBinding
    private lateinit var eventAdapter: EventAdapter
    private var cityId: String? = null
    private var categoryId: String? = null
    private var events: List<Event> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cityId = intent.getStringExtra("City")
        categoryId = intent.getStringExtra("Category")

        setupRecyclerView()

        if (cityId != null && categoryId == null) {
            fetchEvents()
            setUpCategorySpinner()
        } else if (categoryId != null && cityId == null) {
            fetchCategory()
            setupCitySpinner()
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(events, this)
        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EventsActivity)
            adapter = eventAdapter
        }
    }

    private fun fetchEvents() {
        val city = cityId
        val category = categoryId
        if (city != null) {
            RetrofitClient.apiService.getEventsForCity(city).enqueue(object : Callback<List<Event>> {
                override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                    if (response.isSuccessful) {
                        val allEvents = response.body() ?: listOf()
                        val filteredEvents = if (category != null) {
                            allEvents.filter { it.category == category }
                        } else {
                            allEvents
                        }
                        events = filteredEvents
                        eventAdapter.updateEvents(events)
                    } else {
                        showError("Server error: ${response.code()} ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    showError("Network error: ${t.localizedMessage}")
                }
            })
        } else {
            showError("City ID is not provided.")
        }
    }



    private fun fetchCategory() {
        val category = categoryId  // Copy nullable categoryId to a local variable
        if (category != null) {  // Check if category is not null
            RetrofitClient.apiService.getEventsByCategory(category).enqueue(object : Callback<List<Event>> {
                override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            events = responseBody
                            eventAdapter.updateEvents(events)
                        } else {
                            events = emptyList()
                            eventAdapter.updateEvents(events)
                            showError("No data received from the server")
                        }
                    } else {
                        showError("Server error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    showError("Network error: ${t.localizedMessage}")
                }
            })
        } else {
            showError("Category ID is not provided.")
        }
    }


    private fun setupCitySpinner() {
        val cities = listOf("Επιλέξτε πόλη", "Αθήνα", "Θεσσαλονίκη", "Κέρκυρα", "Σέρρες")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cities)
        binding.categorySpinner.adapter = adapter
        binding.categorySpinner.visibility = View.VISIBLE

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val city = parent.getItemAtPosition(position).toString()
                if (city != "Επιλέξτε πόλη") {
                    cityId = when (city) {
                        "Αθήνα" -> "66143c76dec0b20b64f4f21d"
                        "Θεσσαλονίκη" -> "66143c77dec0b20b64f4f21f"
                        "Κέρκυρα" -> "66143c77dec0b20b64f4f221"
                        "Σέρρες" -> "66143c77dec0b20b64f4f223"
                        else -> null
                    }
                    fetchEvents() // Re-fetch the events based on the new city selection
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionally handle the case where nothing is selected
            }
        }
    }

    private fun setUpCategorySpinner() {
        val categories = listOf("Επιλέξτε κατηγορία", "Αθλητικά", "Θέατρο", "Κινηματογράφος", "Μουσική")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        binding.categorySpinner.adapter = adapter
        binding.categorySpinner.visibility = View.VISIBLE

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val category = parent.getItemAtPosition(position).toString()
                if (category != "Επιλέξτε κατηγορία") {
                    categoryId = category
                    fetchEvents() // Updated to fetch events with both city and category filters
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

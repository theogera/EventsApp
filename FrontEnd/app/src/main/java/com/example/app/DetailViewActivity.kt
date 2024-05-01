package com.example.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.app.databinding.ActivityDetailViewBinding
import com.example.app.models.Event
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class DetailViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailViewBinding
    private var currentRemainingTickets: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra("EXTRA_EVENT_ID")
        eventId?.let {
            fetchEventDetails(it)
        } ?: showError("No Event ID provided.")

        setupButtonListeners(eventId)
    }

    private fun setupButtonListeners(eventId: String?) {
        binding.buttonIncrease.setOnClickListener {
            increaseTicketCount()
        }
        binding.buttonDecrease.setOnClickListener {
            decreaseTicketCount()
        }
        binding.agora.setOnClickListener {
            eventId?.let { id ->
                val ticketsToBuy = binding.textViewTicketCount.text.toString().toInt()
                if (ticketsToBuy > 0 && ticketsToBuy <= currentRemainingTickets) {
                    purchaseTickets(id, ticketsToBuy)
                } else {
                    showError("Invalid number of tickets selected.")
                }
            }
        }
    }

    private fun increaseTicketCount() {
        var ticketCount = binding.textViewTicketCount.text.toString().toInt()
        ticketCount++
        binding.textViewTicketCount.text = ticketCount.toString()
        updateTicketCountAndPrice()
    }

    private fun decreaseTicketCount() {
        var ticketCount = binding.textViewTicketCount.text.toString().toInt()
        if (ticketCount > 1) {
            ticketCount--
            binding.textViewTicketCount.text = ticketCount.toString()
            updateTicketCountAndPrice()
        }
    }

    private fun updateTicketCountAndPrice() {
        val ticketCount = binding.textViewTicketCount.text.toString().toInt()
        val ticketPriceText = binding.textViewTicketPrice.text.toString()
        val ticketPrice = ticketPriceText.substringAfter("€", "0").toFloatOrNull() ?: 0f
        val totalPrice = ticketCount * ticketPrice
        binding.textViewTotalPrice.text = "Συνολική τιμή: €${String.format("%.2f", totalPrice)}"
    }


    private fun purchaseTickets(eventId: String, ticketsBought: Int) {
        val remainingTickets = currentRemainingTickets - ticketsBought
        val updateMap = mapOf("remainingTickets" to remainingTickets)

        RetrofitClient.apiService.updateEventTickets(eventId, updateMap).enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.isSuccessful) {
                    response.body()?.let { event ->
                        currentRemainingTickets = event.remainingTickets
                        displayEventDetails(event) // Refresh the UI with the updated event details
                        Toast.makeText(this@DetailViewActivity, "Επιτυχής αγορά εισητηρίου", Toast.LENGTH_LONG).show()
                        // after user buys ticket wait 3 secdonds and return to main activity
                        Handler().postDelayed({
                            navigateToMainActivity()
                        }, 3000) // Delay for 3 seconds
                    }
                } else {
                    showError("Update failed: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                showError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // close DetailView after going to MainActivity
    }


    private fun fetchEventDetails(eventId: String) {
        RetrofitClient.apiService.getEventDetails(eventId).enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.isSuccessful) {
                    response.body()?.let { event ->
                        currentRemainingTickets = event.remainingTickets
                        displayEventDetails(event)
                    } ?: showError("Failed to retrieve event details.")
                } else {
                    showError("Error: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                showError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun displayEventDetails(event: Event) {
        binding.apply {
            textViewTitle.text = event.title
            textViewCategory.text = event.category
            textViewDescription.text = event.description

            val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val targetFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            event.date.let {
                val parsedDate = originalFormat.parse(it)
                val formattedDate = targetFormat.format(parsedDate)
                textViewDate.text = "Ημερομηνία: $formattedDate"
            }

            textViewTime.text = "Ώρα: ${event.time}"
            textViewRemainingTickets.text = "Διαθέσιμα εισητήρια: ${event.remainingTickets}"
            textViewTicketPrice.text = "Τιμή: €${event.ticketPrice}"

            event.image?.let { imageUrl ->
                Glide.with(this@DetailViewActivity)
                    .load(imageUrl)
                    .into(eventImage)
            }

            // Set initial ticket count and price display
            updateTicketCountAndPrice()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

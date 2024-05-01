package com.example.app.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app.DetailViewActivity
import com.example.app.R
import com.example.app.models.Event
import java.text.SimpleDateFormat
import java.util.Locale

class EventAdapter(private var events: List<Event>,var currentContext: Context) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])

        holder.itemView.setOnClickListener {
            Log.d("Event Clicked", events[position].title)
            val intent = Intent(currentContext, DetailViewActivity::class.java).apply {
                putExtra("EXTRA_EVENT_ID", events[position].id)
            }
            currentContext.startActivity(intent)
        }
    }

    override fun getItemCount() = events.size

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.eventTitle)
        private val date: TextView = itemView.findViewById(R.id.eventDate)
        private val price: TextView = itemView.findViewById(R.id.eventPrice)
        private val available: TextView = itemView.findViewById(R.id.eventAvailable)
        private val town: TextView = itemView.findViewById(R.id.eventTown)
        private val image: ImageView = itemView.findViewById(R.id.eventImage)

        fun bind(event: Event) {
            title.text = event.title
            price.text = "Τιμή: " + event.ticketPrice.toInt().toString() + "€"
            available.text = "Απομένουν " + event.remainingTickets.toString() + " εισητήρια"

            when (event.cityId) {
                "66143c76dec0b20b64f4f21d" -> {
                    town.text = event.cityId.toString("Αθήνα")
                }
                "66143c77dec0b20b64f4f21f" -> {
                    town.text = event.cityId.toString("Θεσσαλονίκη")
                }
                "66143c77dec0b20b64f4f221" -> {
                    town.text = event.cityId.toString("Κέρκυρα")
                }
                "66143c77dec0b20b64f4f223" -> {
                    town.text = event.cityId.toString("Σέρρες")
                }
            }

            // date format
            val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val targetFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val parsedDate = originalFormat.parse(event.date) ?: return
            val formattedDate = targetFormat.format(parsedDate)

            date.text = formattedDate

            // glide to load images
            Glide.with(itemView.context)
                .load(event.image)
                .into(image)
        }
    }
}
private fun String.toString(town: String): String {
    return town
}
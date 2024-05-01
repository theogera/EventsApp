package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start_AutoCompleteTextView
        val choice = resources.getStringArray(R.array.choice)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, choice)
        val autocomplete = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)

        autocomplete.setAdapter(arrayAdapter)

        autocomplete.setOnItemClickListener { adapterView, _, i, _ ->
            val selectedCategory = adapterView.getItemAtPosition(i) as String
            navigationByCategory(selectedCategory)
        }


        val athina = findViewById<ImageView>(R.id.imageAthina)
        val thessaloniki = findViewById<ImageView>(R.id.imageThessaloniki)
        val kerkyra = findViewById<ImageView>(R.id.imageKerkyra)
        val serres = findViewById<ImageView>(R.id.imageSerres)

        athina.setOnClickListener {
            onCitySelected("66143c76dec0b20b64f4f21d")
        }

        thessaloniki.setOnClickListener {
            onCitySelected("66143c77dec0b20b64f4f21f")
        }

        kerkyra.setOnClickListener {
            onCitySelected("66143c77dec0b20b64f4f221")
        }

        serres.setOnClickListener {
            onCitySelected("66143c77dec0b20b64f4f223")
        }
    }

    private fun onCitySelected(city: String) {
        val intent = Intent(this, EventsActivity::class.java)
        intent.putExtra("City", city)
        startActivity(intent)
    }

    private fun navigationByCategory(category: String) {
        val intent = Intent(this, EventsActivity::class.java).apply {
            putExtra("Category", category)
            startActivity(intent)
        }
        startActivity(intent)
    }
}
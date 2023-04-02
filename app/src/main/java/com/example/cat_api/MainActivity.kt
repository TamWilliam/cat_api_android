package com.example.cat_api

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var generateCatButton: Button
    private lateinit var catImageView: ImageView
    private lateinit var catDescriptionTextView: TextView
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        generateCatButton = findViewById(R.id.generate_cat_button)
        catImageView = findViewById(R.id.cat_image_view)
        catDescriptionTextView = findViewById(R.id.cat_description_text_view)

        generateCatButton.setOnClickListener {
            if (isNetworkAvailable()) {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.thecatapi.com/v1/images/search?mime_types=jpg,png")
                    .header("x-api-key", "live_0SN1ufOW3t3xtgMktKQHwWsWAtSFLYZJuCWnzLrdjUxt1sRbhXv1Cv2vteKt6jUH ")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            Log.e(TAG, "Error: ${response.code}")
                            return
                        }

                        val json = response.body?.string() ?: return
                        val jsonArray = JSONArray(json)
                        val jsonObject = jsonArray.getJSONObject(0)
                        imageUrl = jsonObject.getString("url")
                        val breeds = jsonObject.getJSONArray("breeds")
                        var description = ""
                        if (breeds.length() > 0) {
                            val breed = breeds.getJSONObject(0)
                            description = breed.getString("description")
                        }

                        runOnUiThread {
                            catDescriptionTextView.text = description
                            loadImage()
                        }
                    }
                })
            } else {
                Toast.makeText(this, "Error, internet connection required !", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.homepage_button).setOnClickListener {
            if (isNetworkAvailable()) {
                val intent = Intent(this@MainActivity, HomePage::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error, internet connection required!", Toast.LENGTH_SHORT).show()
            }
        }

        if (savedInstanceState != null) {
            imageUrl = savedInstanceState.getString("imageUrl")
            loadImage()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("imageUrl", imageUrl)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        loadImage()
    }

    private fun loadImage() {
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.cat_placeholder)
                .error(R.drawable.cat_placeholder_fail)
                .into(catImageView)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }
}

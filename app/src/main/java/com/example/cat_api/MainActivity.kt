package com.example.cat_api

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val generateCatButton = findViewById<Button>(R.id.generate_cat_button)
        val catImageView = findViewById<ImageView>(R.id.cat_image_view)

        generateCatButton.setOnClickListener {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.thecatapi.com/v1/images/search?mime_types=jpg,png")
                .header("x-api-key", "live_0SN1ufOW3t3xtgMktKQHwWsWAtSFLYZJuCWnzLrdjUxt1sRbhXv1Cv2vteKt6jUH ")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                 override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                    }

                    val json = response.body?.string() ?: return
                    val jsonArray = JSONArray(json)
                    val jsonObject = jsonArray.getJSONObject(0)
                    val url = jsonObject.getString("url")

                    runOnUiThread {
                        Glide.with(this@MainActivity)
                            .load(url)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(catImageView)
                    }
                }
            })
        }

    }
}
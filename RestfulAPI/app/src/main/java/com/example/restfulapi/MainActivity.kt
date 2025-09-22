package com.example.restfulapi

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.restfulapi.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    companion object {
        const val BASE_URL = "https://api.giphy.com/v1/gifs/trending?api_key=p8uXvKGzZRFXJdYKNCtKysrwT4XGle0t&limit=25"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.FetchJoke.setOnClickListener{
            fetchJoke()
        }
    }

    private fun fetchJoke() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://api.giphy.com/v1/gifs/trending?api_key=p8uXvKGzZRFXJdYKNCtKysrwT4XGle0t&limit=25")
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 10_000
                    readTimeout = 10_000
                }

                val jokeText = if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)

                    when (json.getString("type")) {
                        "single" -> json.getString("joke")
                        "twopart" -> "${json.getString("setup")}\n\n${json.getString("delivery")}"
                        else -> "Unknown joke format."
                    }
                } else {
                    "Error: ${connection.responseCode}"
                }

                connection.disconnect()

                withContext(Dispatchers.Main) {
                    binding.textViewJoke.text = jokeText
                }
            } catch (e: Exception) {
                Log.e("FetchJoke", "Error fetching joke", e)
                withContext(Dispatchers.Main) {
                    binding.textViewJoke.text = "Failed to fetch joke: ${e.message}"
                }
            }
        }

    }
}
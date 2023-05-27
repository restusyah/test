package com.example.testing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testing.databinding.ActivityListQuotesBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class ListQuotesActivity : AppCompatActivity() {
    companion object {
        private val TAG = ListQuotesActivity::class.java.simpleName
    }
    private lateinit var binding: ActivityListQuotesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_quotes)
        binding = ActivityListQuotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "List of Quotes"
        val layoutManager = LinearLayoutManager(this)
        binding.list.setLayoutManager(layoutManager)
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.list.addItemDecoration(itemDecoration)
        getListQuotes()
    }

    private fun getListQuotes() {
        binding.progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://trukpedia.com/json/list.php"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                binding.progressBar.visibility = View.INVISIBLE

                val list = ArrayList<String>()
                val result = String(responseBody!!)
                Log.d(TAG, result)
                try {
                    val jsonArray = JSONArray(result)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val idpendaftar = jsonObject.getString("idpendaftar")
                        val nama = jsonObject.getString("nama")
                        val alamat = jsonObject.getString("alamat")
                        val nohp = jsonObject.getString("nohp")
                        val email = jsonObject.getString("email")
                        val password = jsonObject.getString("password")
                        list.add("\n$idpendaftar\n — $nama\n — $alamat\n — $nohp\n — $email\n — $password\n")
                    }
                    val adapter = QuoteAdapter(list)
                    binding.list.adapter = adapter
                } catch (e: Exception) {
                    Toast.makeText(this@ListQuotesActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                binding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error!!.message}"
                }
                Toast.makeText(this@ListQuotesActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }
    override fun onSupportNavigateUp(): Boolean {
        this.onBackPressed()
        return true
    }
}

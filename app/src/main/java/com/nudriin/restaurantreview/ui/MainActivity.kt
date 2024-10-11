package com.nudriin.restaurantreview.ui

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nudriin.restaurantreview.data.response.CustomerReviewsItem
import com.nudriin.restaurantreview.data.response.PostReviewResponse
import com.nudriin.restaurantreview.data.response.Restaurant
import com.nudriin.restaurantreview.data.response.RestaurantResponse
import com.nudriin.restaurantreview.data.retrofit.ApiConfig
import com.nudriin.restaurantreview.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "MainActivity"
        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // hide action bar
        supportActionBar?.hide()

        val layoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvReview.addItemDecoration(itemDecoration)

        getRetaurant()

        binding.btnSend.setOnClickListener { view ->
            saveReview(binding.edReview.text.toString()) // get text from input view
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun getRetaurant() {
        showLoading(true) // set loading

        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID) // create client for get restaurant api
        client.enqueue(object : Callback<RestaurantResponse> {
            override fun onResponse(
                call: Call<RestaurantResponse>,
                response: Response<RestaurantResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val body = response.body()
                    if(body != null) {
                        setRestaurantData(body.restaurant)
                        setReviewData(body.restaurant.customerReviews)
                    }
                }
            }

            override fun onFailure(call: Call<RestaurantResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun saveReview(review: String) {
        showLoading(true)

        val client = ApiConfig.getApiService().saveReview(RESTAURANT_ID, "Elon Zuckerburg", review)
        client.enqueue(object: Callback<PostReviewResponse> {
            override fun onResponse(
                call: Call<PostReviewResponse>,
                response: Response<PostReviewResponse>
            ) {
                showLoading(false)
                val body = response.body()
                if(response.isSuccessful && body != null) {
                    setReviewData(body.customerReviews)
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostReviewResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun setReviewData(customerReviews: List<CustomerReviewsItem>) {
        val adapter = ReviewListAdapter() // create adapter
        adapter.submitList(customerReviews) // set response data to adapter
        binding.rvReview.adapter = adapter
        binding.edReview.setText("")
    }

    private fun setRestaurantData(restaurant: Restaurant) {
        binding.tvTitle.text = restaurant.name
        binding.tvDescription.text= restaurant.description
        Glide.with(this)
            .load("https://restaurant-api.dicoding.dev/images/large/${restaurant.pictureId}")
            .into(binding.ivPicture)
    }
}
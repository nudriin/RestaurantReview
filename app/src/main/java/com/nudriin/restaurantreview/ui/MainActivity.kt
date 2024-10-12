package com.nudriin.restaurantreview.ui

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var viewModel: MainViewModel

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

        // create view model
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        // observe restaurant live data data
        viewModel.restaurant.observe(this) {
            setRestaurantData(it)
        }

        viewModel.listReview.observe(this){
            setReviewData(it)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.btnSend.setOnClickListener { view ->
            viewModel.saveReview(binding.edReview.text.toString())
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
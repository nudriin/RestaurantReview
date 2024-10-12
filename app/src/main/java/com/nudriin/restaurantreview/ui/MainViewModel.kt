package com.nudriin.restaurantreview.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nudriin.restaurantreview.data.response.CustomerReviewsItem
import com.nudriin.restaurantreview.data.response.PostReviewResponse
import com.nudriin.restaurantreview.data.response.Restaurant
import com.nudriin.restaurantreview.data.response.RestaurantResponse
import com.nudriin.restaurantreview.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    // Create mutable live data and live data of restaurant
    private val _restaurant = MutableLiveData<Restaurant>()
    val restaurant: LiveData<Restaurant> = _restaurant

    // create mutable live data ad life data of list review
    private val _listReview = MutableLiveData<List<CustomerReviewsItem>>()
    val listReview: LiveData<List<CustomerReviewsItem>> = _listReview

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "MainViewModel"
        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }

    // inti view model with init function

    init {
        getRetaurant()
    }

    private fun getRetaurant() {
        // variable.value => setValue()
        // variable.postValue => postValue()

        _isLoading.value = true // set loading

        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID) // create client for get restaurant api
        client.enqueue(object : Callback<RestaurantResponse> {
            override fun onResponse(
                call: Call<RestaurantResponse>,
                response: Response<RestaurantResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val body = response.body()
                    if(body != null) {
                        _restaurant.value = body.restaurant
                        _listReview.value = body.restaurant.customerReviews
                    }
                }
            }

            override fun onFailure(call: Call<RestaurantResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun saveReview(review: String) {
        _isLoading.value = true // set loading

        val client = ApiConfig.getApiService().saveReview(RESTAURANT_ID, "Elon Zuckerburg", review)
        client.enqueue(object: Callback<PostReviewResponse> {
            override fun onResponse(
                call: Call<PostReviewResponse>,
                response: Response<PostReviewResponse>
            ) {
                _isLoading.value = false
                val body = response.body()
                if(response.isSuccessful && body != null) {
                    _listReview.value = body.customerReviews
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostReviewResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }



}
package com.example.suitmediatest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.suitmediatest.databinding.ActivityThirdBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ThirdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThirdBinding
    private lateinit var adapter: UserAdapter
    private val userList = mutableListOf<User>()

    private var currentPage = 1
    private var totalPages = 1
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }

        loadUsers(page = 1)
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter(userList) { selectedUser ->
            val fullName = "${selectedUser.firstName} ${selectedUser.lastName}"
            val resultIntent = Intent().apply {
                putExtra("SELECTED_USER_NAME", fullName)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvUsers.layoutManager = layoutManager
        binding.rvUsers.adapter = adapter

        // Infinite Scroll / Pagination Listener
        binding.rvUsers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && currentPage < totalPages) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        loadUsers(page = currentPage + 1)
                    }
                }
            }
        })
    }

    private fun refreshData() {
        currentPage = 1
        totalPages = 1
        userList.clear()
        adapter.notifyDataSetChanged()
        loadUsers(page = 1)
    }

    private fun loadUsers(page: Int) {
        isLoading = true
        binding.progressBar.visibility = if (page == 1 && !binding.swipeRefresh.isRefreshing) View.VISIBLE else View.GONE

        ApiConfig.getApiService().getUsers(ApiConfig.API_KEY, page = page, perPage = 10)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    isLoading = false
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false

                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            totalPages = body.totalPages
                            currentPage = body.page

                            userList.addAll(body.data)
                            adapter.notifyDataSetChanged()

                            // Empty State check
                            if (userList.isEmpty()) {
                                binding.tvEmptyState.visibility = View.VISIBLE
                                binding.rvUsers.visibility = View.GONE
                            } else {
                                binding.tvEmptyState.visibility = View.GONE
                                binding.rvUsers.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        Toast.makeText(this@ThirdActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    isLoading = false
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(this@ThirdActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

package com.astro.test.robinDharmaPutera

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.astro.test.robinDharmaPutera.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    private val adapter = MainAdapter()
    private var isAscending = true
    private var listUser: ArrayList<User> = ArrayList()
    private var page: Int = 1
    private var nameSearched = ""
    private val PER_PAGE = 50
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val retrofitService = RetrofitService.getInstance()
        val mainRepository = MainRepository(retrofitService)
        binding.rvUser.adapter = adapter
        viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepository))
            .get(MainViewModel::class.java)

        binding.sortIcon.setOnClickListener {
            if (isAscending){
                isAscending = false
                binding.sortIcon.setImageResource(R.drawable.ic_sortdescending)
                if(listUser.size>0){
                    adapter.clear()
                    adapter.setUser(listUser.sortedByDescending { it.login })
                }
            }else{
                isAscending = true
                binding.sortIcon.setImageResource(R.drawable.ic_sortascending)
                if (listUser.size>0){
                    adapter.clear()
                    adapter.setUser(listUser.sortedBy { it.login })
                }
            }

        }

        binding.rvUser.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    // LOAD MORE
                    viewModel.getUsers(nameSearched, PER_PAGE, page)
                    Log.d("LOAD MORE PAGE: ", page.toString())
                }
            }
        })

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            var timer = Timer()
            val DELAY: Long = 300
            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()){
                    listUser = ArrayList()
                    page = 1
                    nameSearched = ""
                }else{
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                viewModel.getUsers(s.toString(), PER_PAGE, page)
                                Log.d("PAGE: ", page.toString())
                                nameSearched = s.toString()
                            }
                        },
                        DELAY
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

        viewModel.userList.observe(this){ list ->
            if (list.isEmpty()) {
                Toast.makeText(this, "No user match your keyword!", Toast.LENGTH_SHORT).show()
                adapter.clear()
                binding.progressDialog.visibility = View.GONE
            }
            else{
                if (page == 1){
                    listUser = ArrayList()
                    for (x in list)listUser.add(x)
                    if (isAscending){
                        adapter.setUser(listUser.sortedBy { it.login })
                    }else{
                        adapter.setUser(listUser.sortedByDescending { it.login })
                    }
                    Log.d("PAGE 1: ", adapter.itemCount.toString())

                }else{
                    for (x in list)listUser.add(x)
                    adapter.setUser(listUser)
                    Log.d("MORE PAGE: ", page.toString())
                    Log.d("MORE PAGE: ", adapter.itemCount.toString())
                }
                page++
            }
        }

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.loading.observe(this) {
            if (it) {
                binding.progressDialog.visibility = View.VISIBLE
            } else {
                binding.progressDialog.visibility = View.GONE
            }
        }

    }
}
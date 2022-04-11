package com.ultratechies.ghala.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ultratechies.ghala.R
import com.ultratechies.ghala.databinding.ActivitySplashBinding
import com.ultratechies.ghala.ui.main.MainActivity
import com.ultratechies.ghala.utils.isNetworkAvailable
import com.ultratechies.ghala.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel = SplashViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE

        //check network connectivity
        if(!isNetworkAvailable(this)){
            //show error
            binding.root.snackbar(getString(R.string.no_internet_connection))
            binding.progressBar.visibility = View.GONE

            GlobalScope.launch {
                delay(15000)
                if(!isNetworkAvailable(this@SplashActivity)){
                    binding.root.snackbar(getString(R.string.no_internet_connection))
                }else{
                    loadMainActivity()
                }
            }

        } else {
            GlobalScope.launch {
                delay(3000)
                runOnUiThread {
                    loadMainActivity()
                }
            }
        }
    }

    private fun loadMainActivity(){
        binding.progressBar.visibility = View.GONE
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
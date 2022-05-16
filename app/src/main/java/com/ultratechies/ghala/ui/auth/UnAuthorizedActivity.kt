package com.ultratechies.ghala.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ultratechies.ghala.R

class UnAuthorizedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_un_authorized)
    }

      override fun onBackPressed() {
          super.onBackPressed()
          finishAffinity()
      }
}
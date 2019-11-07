package com.yogile.appractiqa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.yogile.appractiqa.ui.login.CodeActivity
import com.yogile.appractiqa.ui.login.LoginActivity
import com.yogile.appractiqa.ui.login.UserDetailsActivity

class SplashActivity : AppCompatActivity() {
    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(mAuth?.currentUser != null)
        {
            if(mAuth.currentUser!!.displayName.isNullOrEmpty()) {
                val i = Intent(this, UserDetailsActivity::class.java)
                i.putExtra("email", mAuth.currentUser!!.email)
                println("123456")
                startActivity(i)
            }
            else if(mAuth.currentUser!!.displayName?.contains("withInfo")!!){
                val i = Intent(this, CodeActivity::class.java)
                println("1234567")
                startActivity(i)
            }
            else{
                val i = Intent(this, MainActivity::class.java)
                println("12345")
                startActivity(i)
            }
        }
        else{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }
}
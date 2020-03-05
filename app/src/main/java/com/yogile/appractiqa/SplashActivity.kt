package com.yogile.appractiqa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.yogile.appractiqa.ui.login.CodeActivity
import com.yogile.appractiqa.ui.login.LoginActivity
import com.yogile.appractiqa.ui.login.UserDetailsActivity
var isAdmin = false
var groupCode = ""
var userName = ""
var groupMainAdminUid = ""
var logo = ""
class SplashActivity : AppCompatActivity() {
    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings
        if(mAuth?.currentUser != null)
        {
            when {
                mAuth.currentUser!!.displayName.isNullOrEmpty() -> {
                    val i = Intent(this, UserDetailsActivity::class.java)
                    i.putExtra("email", mAuth.currentUser!!.email)
                    startActivity(i)
                }
                mAuth.currentUser!!.displayName?.contains("withInfo")!! -> {


                    val i = Intent(this, CodeActivity::class.java)
                    startActivity(i)
                }
                else -> {
                    val i = Intent(this, LoadDataActivity::class.java)
                    startActivity(i)

                }
            }
        }
        else{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }


}
package com.yogile.appractiqa.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import com.google.firebase.auth.FirebaseAuth
import com.yogile.appractiqa.R
import kotlinx.android.synthetic.main.activity_code.*

class CodeActivity : AppCompatActivity() {
    private lateinit var anim:MyAnimationDrawable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_code)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        container.doOnLayout {
            anim = MyAnimationDrawable(container.width)
            container.background = anim
            anim.start()
        }
        join.setOnClickListener {
            join.isEnabled = false
            startActivity(Intent(this, JoinActivity::class.java))
        }
        create.setOnClickListener {
            create.isEnabled = false
            startActivity(Intent(this,CreateCodeActivity::class.java))
        }
        logout.setOnClickListener {
            logout.isEnabled = false
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,LoginActivity::class.java))
            overridePendingTransition(R.anim.left_intent, R.anim.left_intent_out)

        }
    }

    override fun onBackPressed() {
    }
}

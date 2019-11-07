package com.yogile.appractiqa.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
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
            startActivity(Intent(this, JoinActivity::class.java))
        }
        create.setOnClickListener {
            startActivity(Intent(this,CreateCodeActivity::class.java))
        }
    }
}

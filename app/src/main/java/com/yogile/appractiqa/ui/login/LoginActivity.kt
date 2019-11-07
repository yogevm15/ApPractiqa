package com.yogile.appractiqa.ui.login

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import co.revely.gradient.RevelyGradient
import com.google.android.material.snackbar.Snackbar

import com.yogile.appractiqa.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.yogile.appractiqa.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception
import java.util.regex.Pattern.compile







class LoginActivity : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var isBack = false
    private lateinit var valueAnimator:ValueAnimator
    private val emailRegex = compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val layout = findViewById<ConstraintLayout>(R.id.container)
        back.setOnClickListener{
            isBack = false
            login.setText("Continue")
            username.setText("")
            back.visibility = View.INVISIBLE
            username.visibility = View.VISIBLE
            password.setText("")
            password.visibility = View.INVISIBLE
            setLoginOnclick()
        }

        setLoginOnclick()
        layout.doOnLayout {
            val anim = MyAnimationDrawable(layout.width)
            layout.background = anim
            anim.start()
        }




    }
    @SuppressLint("RestrictedApi")
    fun setLoginOnclick(){
        login.setOnClickListener { view ->
            (container.background as MyAnimationDrawable).setSpeed(30)
            try {
                mAuth?.fetchSignInMethodsForEmail(username.text.toString())
                    ?.addOnSuccessListener { result ->
                        val signInMethods = result.signInMethods
                        if (signInMethods.toString().contains("password")) {
                           (container.background as MyAnimationDrawable).setSpeed(1)
                            isBack = true
                            login.setText("Sign In")
                            back.visibility = View.VISIBLE
                            username.visibility = View.INVISIBLE
                            password.setText("")
                            password.visibility = View.VISIBLE
                            login.setOnClickListener { view ->
                                (container.background as MyAnimationDrawable).setSpeed(30)
                                mAuth?.signInWithEmailAndPassword(
                                    username.text.toString(),
                                    password.text.toString()
                                )
                                    ?.addOnCompleteListener(
                                        this
                                    ) { task ->
                                        if (task.isSuccessful) {

                                            // Sign in success, update UI with the signed-in user's information
                                            updateUi()

                                        } else {
                                           (container.background as MyAnimationDrawable).setSpeed(1)
                                            // If sign in fails, display a message to the user.
                                            if (task.exception?.message.toString().contains("The password is invalid")) {
                                                Snackbar.make(
                                                    view,
                                                    "Invalid password...",
                                                    Snackbar.LENGTH_LONG
                                                ).setAction("Action", null).show()
                                            } else if (task.exception?.message.toString().contains("We have blocked")) {
                                                Snackbar.make(
                                                    view,
                                                    "You tried too much times...\nTry again later",
                                                    Snackbar.LENGTH_LONG
                                                ).setAction("Action", null).show()
                                            } else {
                                                if (password.text.toString().length < 6 && !isEmailValid(
                                                        username.text.toString()
                                                    )
                                                ) {
                                                    Snackbar.make(
                                                        view,
                                                        "The email address is badly formatted\nThe password must be 6 or more letters!",
                                                        Snackbar.LENGTH_LONG
                                                    ).setAction("Action", null).show()
                                                } else if (!isEmailValid(username.text.toString())) {
                                                    Snackbar.make(
                                                        view,
                                                        "The email address is badly formatted",
                                                        Snackbar.LENGTH_LONG
                                                    ).setAction("Action", null).show()
                                                } else {
                                                    Snackbar.make(
                                                        view,
                                                        "The password must be 6 or more letters!",
                                                        Snackbar.LENGTH_LONG
                                                    ).setAction("Action", null).show()
                                                }
                                            }
                                        }

                                        // ...
                                    }

                            }
                        }
                    else{
                        if (!isEmailValid(username.text.toString())) {
                           (container.background as MyAnimationDrawable).setSpeed(1)
                            Snackbar.make(
                                view,
                                "The email address is badly formatted",
                                Snackbar.LENGTH_LONG
                            ).setAction("Action", null).show()
                        } else {
                            updateUi()
                        }
                    }
                }?.addOnFailureListener { exception ->
                   (container.background as MyAnimationDrawable).setSpeed(1)
                    Snackbar.make(view, "Try again later", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
            catch(e: Exception) {
                Snackbar.make(
                    view,
                    "Email is empty",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
               (container.background as MyAnimationDrawable).setSpeed(1)

            }


        }
    }
    fun updateUi(){
        if(mAuth?.currentUser !=null) {
            if(mAuth.currentUser!!.displayName.isNullOrEmpty()) {
                var i = Intent(this, UserDetailsActivity::class.java)
                i.putExtra("email", mAuth.currentUser!!.email)
                startActivity(i)
            }
            else if(mAuth.currentUser!!.displayName == "withInfo"){
                var i = Intent(this, CodeActivity::class.java)
                startActivity(i)
            }
            else{
                var i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
        }
        else{
            var i = Intent(this, UserDetailsActivity::class.java)
            i.putExtra("email",username.text.toString())
            startActivity(i)
        }

    }
    fun isEmailValid(email: String): Boolean {
        return emailRegex.matcher(email).matches()
    }


    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        if(isBack){
            isBack = false
            login.setText("Continue")
            username.setText("")
            back.visibility = View.INVISIBLE
            username.visibility = View.VISIBLE
            password.setText("")
            password.visibility = View.INVISIBLE
            setLoginOnclick()
        }
    }
}




class MyAnimationDrawable(width:Int) : AnimationDrawable(){
    @Volatile private var duration:Int = 1
    private var currentFrame:Int = 0
    private var width1 = width
    private var speed:Int = 1

    init {
        width1 = width
        println(width)
        for(i in 0 until 1441){
            val temp = RevelyGradient.linear().colors(intArrayOf(Color.parseColor("#2bc0e4"),Color.parseColor("#eaecc6"))).angle(i/4f).gradient
            addFrame(temp,0)
        }
    }
    fun setFrame(frameIndex:Int){
        currentFrame = frameIndex
    }
    fun getFrame(): Int {
        return currentFrame
    }
    override fun run() {
        val n = numberOfFrames
        currentFrame+=speed
        if (currentFrame >= n-1) {
            currentFrame=0
        }
        selectDrawable(currentFrame)
        scheduleSelf(this, SystemClock.uptimeMillis()+duration)
    }


    fun setSpeed(speed:Int){
        this.speed = speed
    }


}

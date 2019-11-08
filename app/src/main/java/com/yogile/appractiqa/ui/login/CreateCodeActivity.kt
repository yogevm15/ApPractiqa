package com.yogile.appractiqa.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.yogile.appractiqa.MainActivity
import com.yogile.appractiqa.R
import kotlinx.android.synthetic.main.activity_code.*
import kotlinx.android.synthetic.main.activity_code.container
import kotlinx.android.synthetic.main.activity_create_code.*

class CreateCodeActivity : AppCompatActivity() {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var anim: MyAnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_code)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        container.doOnLayout {
            anim = MyAnimationDrawable(container.width)
            container.background = anim
            anim.start()
        }
        ok.setOnClickListener { view ->
            anim.setSpeed(30)
            if(name.text.toString().isNullOrEmpty()){
                Snackbar.make(
                    view,
                    "The name cannot be empty...",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
                anim.setSpeed(1)
            }
            else if(code.text.toString().isNullOrEmpty()){
                Snackbar.make(
                    view,
                    "The code cannot be empty...",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
                anim.setSpeed(1)
            }
            else{
                db.collection("groups").document(code.text.toString()).get().addOnCompleteListener {task: Task<DocumentSnapshot> ->
                    if(task.result?.exists()!!) {
                        Snackbar.make(
                            view,
                            "This code is already taken...",
                            Snackbar.LENGTH_LONG
                        ).setAction("Action", null).show()
                        anim.setSpeed(1)
                    }
                    else{
                        val groupD = HashMap<String,Any>()
                        groupD.put("name",name.text.toString())
                        groupD.put("admin", FirebaseAuth.getInstance().currentUser?.uid!!)
                        db.collection("groups").document(code.text.toString()).set(groupD).addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName("withGroup")
                                    .build()

                                FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            startActivity(Intent(this,MainActivity::class.java))

                                        }
                                        else{
                                            Snackbar.make(
                                                view,
                                                "Something went wrong.\nTry again later",
                                                Snackbar.LENGTH_LONG
                                            ).setAction("Action", null).show()
                                            anim.setSpeed(1)
                                        }
                                    }
                            }
                            else{
                                Snackbar.make(
                                    view,
                                    "Something went wrong.\nTry again later",
                                    Snackbar.LENGTH_LONG
                                ).setAction("Action", null).show()
                                anim.setSpeed(1)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,CodeActivity::class.java))
    }
}

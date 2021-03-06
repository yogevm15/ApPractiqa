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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.functions.FirebaseFunctions
import com.yogile.appractiqa.LoadDataActivity
import com.yogile.appractiqa.R
import kotlinx.android.synthetic.main.activity_code.container
import kotlinx.android.synthetic.main.activity_join.back
import kotlinx.android.synthetic.main.activity_join.code
import kotlinx.android.synthetic.main.activity_join.ok

class JoinActivity : AppCompatActivity() {
    private lateinit var anim:MyAnimationDrawable
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        container.doOnLayout {
            anim = MyAnimationDrawable(container.width)
            container.background = anim
            anim.start()
        }
        ok.setOnClickListener { view ->
            anim.setSpeed(30)
            if(code.text.toString().isNullOrEmpty()){
                Snackbar.make(
                    view,
                    "The code cannot be empty...",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
                anim.setSpeed(1)
            }
            else{
                db.collection("groups").document(code.text.toString()).get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (!task.result?.exists()!!) {
                        Snackbar.make(
                            view,
                            "Invalid code...",
                            Snackbar.LENGTH_LONG
                        ).setAction("Action", null).show()
                        anim.setSpeed(1)
                    } else {
                        val groupD = HashMap<String,Any>()
                        groupD["admin"] = FirebaseAuth.getInstance().currentUser?.uid!!
                        groupD["code"] = code.text.toString()
                        groupD["uid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
                        FirebaseFunctions.getInstance("europe-west2").getHttpsCallable("joinGroup").call(groupD).addOnCompleteListener {
                            if(it.isSuccessful) {
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName("withGroup")
                                    .build()
                                FirebaseAuth.getInstance()
                                    .currentUser?.updateProfile(profileUpdates)!!.addOnCompleteListener {
                                    if(it.isSuccessful){
                                        var i = Intent(this,LoadDataActivity::class.java)
                                        startActivity(i)
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
        back.setOnClickListener { onBackPressed() }

    }

    override fun onBackPressed() {
        startActivity(Intent(this,CodeActivity::class.java))
        overridePendingTransition(R.anim.left_intent, R.anim.left_intent_out)
    }
}

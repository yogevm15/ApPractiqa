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
import kotlinx.android.synthetic.main.activity_create_code.*
import java.util.*
import kotlin.collections.HashMap

class CreateCodeActivity : AppCompatActivity() {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val STRING_LENGTH = 6
    private val charPool : List<Char> = ('A'..'Z') + ('0'..'9')
    private lateinit var anim: MyAnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_code)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        fab.setOnClickListener {
            val randomString = (1..STRING_LENGTH)
                .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
            code.setText(randomString)
        }
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
                UUID.randomUUID()
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
                        groupD["name"] = name.text.toString()
                        groupD["admin"] = FirebaseAuth.getInstance().currentUser?.uid!!
                        groupD["code"] = code.text.toString()
                        groupD["uid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
                        FirebaseFunctions.getInstance("europe-west2").getHttpsCallable("createGroup").call(groupD).addOnCompleteListener {
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

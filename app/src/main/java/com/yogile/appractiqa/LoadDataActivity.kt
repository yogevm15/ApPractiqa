package com.yogile.appractiqa

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yogile.appractiqa.ui.login.MyAnimationDrawable

import kotlinx.android.synthetic.main.activity_load_data.*
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.yogile.appractiqa.ui.login.LoginActivity


class LoadDataActivity : AppCompatActivity() {
    private lateinit var anim: MyAnimationDrawable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_data)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        container.doOnLayout {
            anim = MyAnimationDrawable(container.width)
            container.background = anim
            anim.start()
            (container.background as MyAnimationDrawable).setSpeed(30)
        }
        getData()
    }

    private fun getData(){
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            when {
                it.result?.get("isAdmin")==null -> {
                    FirebaseAuth.getInstance().signOut()
                    val i = Intent(this, LoginActivity::class.java)
                    startActivity(i)
                }
                it.isSuccessful -> {
                    isAdmin = it.result?.get("isAdmin") as Boolean
                    groupCode = it.result?.get("groupCode") as String
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                }
                else -> {
                    anim.setSpeed(1)
                    val pDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setConfirmText("Try Again").setConfirmClickListener { sweetAlertDialog ->
                        sweetAlertDialog.dismissWithAnimation()
                        anim.setSpeed(30)
                        getData()
                    }
                    pDialog.titleText = "Can't Connect"
                    pDialog.contentText = "It seems like you don't have internet connection."
                    pDialog.setCancelable(false)

                    pDialog.show()
                }
            }

        }
    }

}

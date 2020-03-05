package com.yogile.appractiqa

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.yogile.appractiqa.ui.login.CodeActivity
import com.yogile.appractiqa.ui.login.MyAnimationDrawable

import kotlinx.android.synthetic.main.activity_load_data.*
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
        checkAndChangedGroupStatus()


    }

    private fun getUserData(){
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            when {

                it.isSuccessful -> {
                    if(it.result?.get("isAdmin")==null) {
                        FirebaseAuth.getInstance().signOut()
                        val i = Intent(this, LoginActivity::class.java)
                        startActivity(i)
                    }
                    else {
                        isAdmin = it.result?.get("isAdmin") as Boolean
                        groupCode = it.result?.get("groupCode") as String
                        userName = it.result?.get("name") as String
                        logo = it.result?.get("logo") as String
                        println("123 $userName")
                        getGroupData()
                    }
                }
                else -> {
                    anim.setSpeed(1)
                    val pDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setConfirmText("Try Again").setConfirmClickListener { sweetAlertDialog ->
                        sweetAlertDialog.dismissWithAnimation()
                        anim.setSpeed(30)
                        getUserData()
                    }
                    pDialog.titleText = "Can't Connect"
                    pDialog.contentText = "It seems like you don't have internet connection."
                    pDialog.setCancelable(false)

                    pDialog.show()
                }
            }

        }
    }

    private fun getGroupData(){
        FirebaseFirestore.getInstance().collection("groups").document(groupCode).get().addOnCompleteListener {
            when {

                it.isSuccessful -> {
                    if(it.result?.get("admin")==null) {
                        FirebaseAuth.getInstance().signOut()
                        val i = Intent(this, LoginActivity::class.java)
                        startActivity(i)
                    }
                    else {
                        groupMainAdminUid = it.result?.get("admin") as String
                        val i = Intent(this, MainActivity::class.java)
                        startActivity(i)
                    }
                }
                else -> {
                    anim.setSpeed(1)
                    val pDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setConfirmText("Try Again").setConfirmClickListener { sweetAlertDialog ->
                        sweetAlertDialog.dismissWithAnimation()
                        anim.setSpeed(30)
                        getGroupData()
                    }
                    pDialog.titleText = "Can't Connect"
                    pDialog.contentText = "It seems like you don't have internet connection."
                    pDialog.setCancelable(false)

                    pDialog.show()
                }
            }

        }
    }

    private fun checkAndChangedGroupStatus() {
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance()!!.currentUser!!.uid)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if ((it.result!!["groupCode"] as String).isNullOrEmpty()) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName("withInfo")
                            .build()
                        FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    startActivity(Intent(this, CodeActivity::class.java))
                                }
                                else{
                                    checkAndChangedGroupStatus()
                                }
                            }
                    } else {
                        getUserData()
                    }
                }
                else{
                    checkAndChangedGroupStatus()
                }
            }
    }

}

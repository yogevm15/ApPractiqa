package com.yogile.appractiqa.ui.users.tabs.users

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yogile.appractiqa.ui.login.MyAnimationDrawable
import kotlinx.android.synthetic.main.tab_users.*
import kotlinx.android.synthetic.main.tab_users.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.yogile.appractiqa.*
import com.yogile.appractiqa.R
import kotlinx.android.synthetic.main.activity_create_code.*


class UsersTab : Fragment() {

    private lateinit var usersTabViewModel: UsersTabViewModel
    private lateinit var users: ArrayList<User>
    private lateinit var anim: MyAnimationDrawable
    private var counter = 0
    private var size = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        usersTabViewModel =
            ViewModelProviders.of(this).get(UsersTabViewModel::class.java)

        val root = inflater.inflate(R.layout.tab_users, container, false)

        root.container.doOnLayout {
            anim = MyAnimationDrawable(container!!.width)
            container.background = anim
            anim.start()

            users = ArrayList()
            lv_users.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = UserArrayAdapter(users, activity,anim)
            }


            pullToRefresh.isRefreshing = true
            getData()
            pullToRefresh.setOnRefreshListener {
                getData()
            }

        }
        return root
    }

        private fun getData() {
            anim.setSpeed(30)
            activity?.runOnUiThread {
                users.clear()
                (lv_users.adapter as UserArrayAdapter).notifyDataSetChanged()
            }

            FirebaseFirestore.getInstance().collection("groups").document(groupCode)
                .collection("participants").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    counter = 0
                    size = task.result?.documents?.size!!
                    if (size == 0) {
                        getData()
                    } else {
                        task.result?.documents?.iterator()?.forEach {
                            getUserData(it)
                        }
                    }


                } else {
                    getData()
                }
            }
        }

        private fun getUserData(it: DocumentSnapshot) {
            FirebaseFirestore.getInstance().collection("users").document(it.id).get()
                .addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        activity?.runOnUiThread {
                            users.add(User(task2.result!!))
                            users.sortWith(compareBy<User> { it.uid != FirebaseAuth.getInstance().currentUser!!.uid }.thenBy { !it.isAdmin }.thenBy { it.name })
                            (lv_users.adapter as UserArrayAdapter).notifyDataSetChanged()
                        }

                        counter++
                        if (counter >= size) {
                            pullToRefresh.isRefreshing = false
                            anim.setSpeed(1)
                        }
                    } else {
                        getUserData(it)
                    }

                }
        }


}
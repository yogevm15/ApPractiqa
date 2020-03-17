package com.yogile.appractiqa

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.functions.FirebaseFunctions
import com.squareup.picasso.Picasso
import com.yogile.appractiqa.ui.login.MyAnimationDrawable
import kotlinx.android.synthetic.main.tab_users.*
import kotlinx.android.synthetic.main.user_row.view.*

class UserArrayAdapter(private val dataSource: ArrayList<User>,private val activity: FragmentActivity?,private val anim:MyAnimationDrawable) : RecyclerView.Adapter<UserViewHolder>() {
    override fun getItemCount(): Int = dataSource.size
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user: User = dataSource[position]

        holder.itemView.setOnLongClickListener { view ->
            if (dataSource[position].uid != FirebaseAuth.getInstance().currentUser!!.uid) {
                var alertDialog = AlertDialog.Builder(activity)
                var temp: Array<String>?
                if (FirebaseAuth.getInstance().currentUser!!.uid == groupMainAdminUid) {
                    if (dataSource[position].isAdmin) {
                        temp = arrayOf("Remove", "Remove Admin", "Show Details")
                        alertDialog.setItems(
                            temp
                        ) { dialog, which ->
                            if (which == 0) {
                                removeUserFromPart(position)
                            } else if (which == 1) {
                                removeAdmin(dataSource, position, view)
                            }

                        }
                    } else {
                        temp = arrayOf("Remove", "Make Admin", "Show Details")
                        alertDialog.setItems(
                            temp,
                            DialogInterface.OnClickListener { dialog, which ->
                                if (which == 0) {
                                    removeUserFromPart(position)
                                } else if (which == 1) {
                                    addAdmin(dataSource, position, view)
                                }

                            })
                    }
                } else if (isAdmin) {
                    if (!dataSource[position].isAdmin) {
                        temp = arrayOf("Remove", "Show Details")
                        alertDialog.setItems(
                            temp,
                            DialogInterface.OnClickListener { dialog, which ->
                                if (which == 0) {
                                    removeUserFromPart(position)
                                } else if (which == 1) {
                                }

                            })
                    } else {
                        temp = arrayOf("Show Details")
                        alertDialog.setItems(
                            temp,
                            DialogInterface.OnClickListener { dialog, which ->
                                if (which == 0) {

                                } else if (which == 1) {
                                }

                            })
                    }
                } else {
                    temp = arrayOf("Show Details")
                    alertDialog.setItems(
                        temp,
                        DialogInterface.OnClickListener { dialog, which ->
                            if (which == 0) {

                            } else if (which == 1) {
                            }

                        })
                }


                alertDialog.create()
                alertDialog.show()

            }
            true

        }
        holder.bind(user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return  UserViewHolder(inflater,parent)
    }
    private fun removeAdmin(users: ArrayList<User>, position: Int, view: View) {
        val user = HashMap<String, Any>()
        user["isAdmin"] = false
        FirebaseFirestore.getInstance().collection("users")
            .document(users[position].uid).set(
                user,
                SetOptions.merge()
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activity?.runOnUiThread {
                        users[position].isAdmin = false
                        users.sortWith(compareBy<User> { it.uid != FirebaseAuth.getInstance().currentUser!!.uid }.thenBy { !it.isAdmin }.thenBy { it.name })
                        notifyDataSetChanged()
                        anim.setSpeed(1)
                    }
                } else {
                    removeAdmin(users,position,view)

                }
            }
    }

    private fun addAdmin(users:ArrayList<User>,position: Int,view: View){
        val user = HashMap<String, Any>()
        user["isAdmin"] = true
        FirebaseFirestore.getInstance().collection("users")
            .document(users[position].uid).set(
                user,
                SetOptions.merge()
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activity?.runOnUiThread {
                        users[position].isAdmin = true
                        users.sortWith(compareBy<User> { it.uid != FirebaseAuth.getInstance().currentUser!!.uid }.thenBy { !it.isAdmin }.thenBy { it.name })
                        notifyDataSetChanged()
                        anim.setSpeed(1)
                    }
                } else {
                    addAdmin(users,position,view)

                }
            }
    }

    private fun removeUserFromPart(position: Int){
        anim.setSpeed(30)
        val data = hashMapOf(
            "group" to groupCode,
            "uid" to dataSource[position].uid
        )
        FirebaseFunctions.getInstance("europe-west2").getHttpsCallable("deleteParticipant").call(data).addOnCompleteListener {
            if(it.isSuccessful){
                activity?.runOnUiThread {
                    dataSource.remove(dataSource[position])
                    dataSource.sortWith(compareBy<User> { it.uid != FirebaseAuth.getInstance().currentUser!!.uid }.thenBy { !it.isAdmin }.thenBy { it.name })
                    notifyDataSetChanged()
                    anim.setSpeed(1)

                }
            }
        }
    }






}
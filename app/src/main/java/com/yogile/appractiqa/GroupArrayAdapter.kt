package com.yogile.appractiqa

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.squareup.picasso.Picasso
import com.yogile.appractiqa.ui.login.MyAnimationDrawable
import kotlinx.android.synthetic.main.tab_groups.*
import kotlinx.android.synthetic.main.user_row.view.*

class GroupArrayAdapter(private val activity: FragmentActivity?,
                        private val dataSource: ArrayList<Group>, private val anim: MyAnimationDrawable) : RecyclerView.Adapter<GroupViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return  GroupViewHolder(inflater,parent)
    }

    override fun getItemCount(): Int = dataSource.size

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        var group = dataSource[position]
        holder.itemView.setOnLongClickListener {
            if (dataSource[position].uid != FirebaseAuth.getInstance().currentUser!!.uid) {
                var alertDialog = AlertDialog.Builder(activity)
                var temp: Array<String>?
                temp = arrayOf("Remove Group", "Show Details")
                alertDialog.setItems(
                    temp
                ) { dialog, which ->
                    if (which == 0) {
                        removeGroup(dataSource, position)
                    }

                }



                alertDialog.create()
                alertDialog.show()
            }

            true
        }
        holder.bind(group)
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun removeGroup(groups:ArrayList<Group>, position: Int){
        anim.setSpeed(30)
        val data = hashMapOf(
            "group" to groupCode,
            "innerGroup" to groups[position].uid
        )
        FirebaseFunctions.getInstance("europe-west2").getHttpsCallable("deleteInnerGroup").call(data).addOnCompleteListener {
            if(it.isSuccessful){
                activity?.runOnUiThread {
                    groups.remove(groups[position])
                    groups.sortWith(compareBy { it.name })
                    notifyDataSetChanged()
                    anim.setSpeed(1)
                }
            }
            else{
                println("1234: " + it.exception.toString())
            }
        }
    }




}
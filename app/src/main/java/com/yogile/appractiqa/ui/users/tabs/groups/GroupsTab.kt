package com.yogile.appractiqa.ui.groups.tabs.groups

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.yogile.appractiqa.*
import com.yogile.appractiqa.ui.login.MyAnimationDrawable
import com.yogile.appractiqa.ui.users.tabs.groups.GroupsTabViewModel
import kotlinx.android.synthetic.main.tab_groups.*
import kotlinx.android.synthetic.main.tab_groups.pullToRefresh
import kotlinx.android.synthetic.main.tab_groups.view.*
import kotlinx.android.synthetic.main.tab_users.*


class GroupsTab : Fragment() {

    private lateinit var groupsTabViewModel: GroupsTabViewModel
    private lateinit var groups: ArrayList<Group>
    private lateinit var anim: MyAnimationDrawable
    private var counter = 0
    private var size = 0
    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        groupsTabViewModel =
            ViewModelProviders.of(this).get(GroupsTabViewModel::class.java)

        val root = inflater.inflate(R.layout.tab_groups, container, false)

        root.container.doOnLayout {
            anim = MyAnimationDrawable(container!!.width)
            container.background = anim
            anim.start()

            groups = ArrayList()
            lv_groups.adapter = GroupArrayAdapter(context!!, groups)
            lv_groups.setOnItemLongClickListener { parent, view, position, id ->

                if (groups[position].uid != FirebaseAuth.getInstance().currentUser!!.uid) {
                    var alertDialog = AlertDialog.Builder(activity)
                    var temp: Array<String>?
                    temp = arrayOf("Remove Group", "Show Details")
                    alertDialog.setItems(
                        temp
                    ) { dialog, which ->
                        if (which == 0) {
                            removeGroup(groups, position, view)
                        }

                    }



                    alertDialog.create()
                    alertDialog.show()
                }

                true
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
            groups.clear()
            (lv_groups.adapter as GroupArrayAdapter).notifyDataSetChanged()
        }

        FirebaseFirestore.getInstance().collection("groups").document(groupCode)
            .collection("groups").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    print("1234567")
                    counter = 0
                    size = task.result?.documents?.size!!
                    task.result?.documents?.iterator()?.forEach {
                        print("12345")
                        getGroupData(it)
                    }
                    pullToRefresh.isRefreshing = false
                    anim.setSpeed(1)
                } else {
                    getData()
                }
            }
    }

    private fun getGroupData(it: DocumentSnapshot) {

        activity?.runOnUiThread {
            groups.add(Group(it))
            groups.sortWith(compareBy { it.name })
            (lv_groups.adapter as GroupArrayAdapter).notifyDataSetChanged()
        }
    }



    private fun removeGroup(groups:ArrayList<Group>, position: Int, view: View){
        FirebaseFirestore.getInstance().collection("groups").document(groupCode).collection("groups")
            .document(groups[position].uid).delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activity?.runOnUiThread {
                        groups.remove(groups[position])
                        groups.sortWith(compareBy { it.name })
                        (lv_groups.adapter as GroupArrayAdapter).notifyDataSetChanged()
                        anim.setSpeed(1)

                    }
                } else {
                    removeGroup(groups,position,view)

                }
            }
    }
    

}
package com.yogile.appractiqa.ui.users.tabs.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.yogile.appractiqa.R
import kotlinx.android.synthetic.main.app_bar_main.*

class GroupsTab : Fragment() {

    private lateinit var groupsTabViewModel: GroupsTabViewModel
    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        groupsTabViewModel =
            ViewModelProviders.of(this).get(GroupsTabViewModel::class.java)

        val root = inflater.inflate(R.layout.tab_groups, container, false)

        val textView: TextView = root.findViewById(R.id.text_send)
        groupsTabViewModel.text.observe(this, Observer {
            textView.text = "groups"
        })

        return root
    }

}
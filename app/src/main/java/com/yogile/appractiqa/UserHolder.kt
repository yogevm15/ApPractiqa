package com.yogile.appractiqa

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_row.view.*

class UserViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.user_row, parent, false)) {
    private var mName: TextView? = null
    private var mLogo: ImageView? = null
    private var mAdmin: TextView? = null

    init {
        mName = itemView.findViewById(R.id.username)
        mLogo = itemView.findViewById(R.id.userLogo)
        mAdmin = itemView.findViewById(R.id.admin)
    }

    fun bind(user: User) {
        mName?.text = user.name
        Picasso.get().load(user.logoUrl).placeholder(R.drawable.user).fit()
            .into(mLogo)
        if(user.isAdmin){
            mAdmin?.visibility = View.VISIBLE
        }
        if(user.uid== FirebaseAuth.getInstance().currentUser!!.uid){
            mName?.text = "You"
            mName?.setTextColor(Color.parseColor("#003E66"))
        }
    }

}
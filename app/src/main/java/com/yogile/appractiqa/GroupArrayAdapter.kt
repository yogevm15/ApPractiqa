package com.yogile.appractiqa

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_row.view.*

class GroupArrayAdapter(private val context: Context,
                        private val dataSource: ArrayList<Group>) : BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view =  inflater.inflate(R.layout.user_row,parent,false)
        Picasso.get().load(dataSource[position].logoUrl).placeholder(R.drawable.user).into(view.userLogo)
        view.username.text = dataSource[position].name
        return view
    }


}
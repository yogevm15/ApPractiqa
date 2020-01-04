package com.yogile.appractiqa.ui.admin_settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.yogile.appractiqa.R

class AdminSettingsFragment : Fragment() {

    private lateinit var galleryViewModel: AdminSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(AdminSettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_admin_settings, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(this, Observer {
            textView.text = "settings"
        })
        return root
    }
}
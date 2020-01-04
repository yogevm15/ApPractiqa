package com.yogile.appractiqa;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yogile.appractiqa.ui.users.tabs.groups.GroupsTab;
import com.yogile.appractiqa.ui.users.tabs.users.UsersTab;

class TabPagerAdapter(fm: FragmentManager, private var tabCount: Int) :
        FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

                return when (position) {
                    0 -> UsersTab()
                    1 -> GroupsTab()

                        else -> GroupsTab()
                }
        }

        override fun getCount(): Int {
        return tabCount
        }
}
package com.example.join.LogIn

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

class LogInImageFragmentAdapter : FragmentPagerAdapter{

    constructor(fragmentManager: FragmentManager) : super(fragmentManager) {

    }

    // ViewPager에 들어갈 Fragment들을 담을 리스트
    val fragments = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    //뷰의 사이즈 결정
    override fun getCount(): Int {
        return fragments.size
    }

    internal fun addItem(fragment: Fragment) {
        fragments.add(fragment)
    }
}
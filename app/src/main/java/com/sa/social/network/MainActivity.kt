package com.sa.social.network

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sa.social.network.fragment.HomeFragment
import com.sa.social.network.fragment.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*








class MainActivity : AppCompatActivity() {
    private var TAG=this.javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.FragMainActivity, HomeFragment.newInstance())
        transaction.addToBackStack(null)
        transaction.commit()

        BtnBottonNavigationMainActivity.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                var currentFragment: Fragment? = null
                when(item.itemId){
                    R.id.bottom_navigation_menu_home -> {
                        currentFragment = HomeFragment.newInstance();
                    }
                    R.id.bottom_navigation_menu_search -> {
                        currentFragment = HomeFragment.newInstance();
                     Log.d(TAG,item.itemId.toString())
                    }
                    R.id.bottom_navigation_menu_chat -> {
                        currentFragment = HomeFragment.newInstance();
                        Log.d(TAG,item.itemId.toString())
                    }
                    R.id.bottom_navigation_menu_profile -> {
                        currentFragment = ProfileFragment.newInstance();
                        Log.d(TAG,item.itemId.toString())
                    }

                }
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.FragMainActivity, currentFragment!!)
                transaction.commit()
                return true
            }

        })
    }

    fun ReplaceFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.FragMainActivity, fragment!!)
        transaction.commit()

    }
}

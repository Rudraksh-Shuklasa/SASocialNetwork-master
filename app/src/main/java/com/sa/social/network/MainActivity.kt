package com.sa.social.network

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sa.social.network.base.BaseActivity
import com.sa.social.network.fragment.HomeFragment
import com.sa.social.network.fragment.NotificationFragment
import com.sa.social.network.fragment.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*











class MainActivity : BaseActivity() {
    private var TAG=this.javaClass.simpleName
    var doubleBackToExitPressedOnce=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val extras = intent.extras
        if(extras!=null)
        {
            var isCameFromNotification = extras?.getInt("Firebase")

            if(isCameFromNotification==1){
                ReplaceFragment(NotificationFragment.newInstance())
                BtnBottonNavigationMainActivity.selectedItemId=R.id.bottom_navigation_menu_notification
            }
            else{
                ReplaceFragment(HomeFragment.newInstance())
                BtnBottonNavigationMainActivity.selectedItemId=R.id.bottom_navigation_menu_home

            }
        }
        ReplaceFragment(HomeFragment.newInstance())
        BtnBottonNavigationMainActivity.selectedItemId=R.id.bottom_navigation_menu_home

        BtnBottonNavigationMainActivity.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                var currentFragment: Fragment? = null
                when(item.itemId){
                    R.id.bottom_navigation_menu_home -> {
                        currentFragment = HomeFragment.newInstance();
                    }
                    R.id.bottom_navigation_menu_notification -> {
                        currentFragment = NotificationFragment.newInstance();
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
                ReplaceFragment(currentFragment!!)
                return true
            }

        })
    }

    fun ReplaceFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.FragMainActivity, fragment!!)
        transaction.commit()

    }

    override fun onBackPressed() {
        if (BtnBottonNavigationMainActivity.getSelectedItemId() !== R.id.bottom_navigation_menu_home) {
            val transaction = getSupportFragmentManager().beginTransaction()
            transaction.replace(R.id.FragMainActivity, HomeFragment.newInstance() )
            transaction.commit()
            BtnBottonNavigationMainActivity.setSelectedItemId(R.id.bottom_navigation_menu_home)
        }
        else{
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        }
    }
}

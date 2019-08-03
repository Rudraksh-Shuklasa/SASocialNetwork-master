package com.sa.social.network.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.sa.social.network.adapter.SliderAdapterIntroducation
import kotlinx.android.synthetic.main.activity_introduction.*
import android.widget.Toast
import com.sa.social.network.R
import com.sa.social.network.base.BaseActivity
import com.sa.social.network.services.CheckConnetionService
import com.sa.social.network.utils.SharedPrefrenceUtils


class IntroductionActivity : BaseActivity(){
    var doubleBackToExitPressedOnce = false
    private var TAG=this.javaClass.simpleName
    private var photoList=ArrayList<Int>()
    private var lastPagePostion : Int=0
    private val dots = ArrayList<ImageView>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_introduction)
        var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(this)


        //if user not come to screen first time than redirect to splash screen
        if(!sharedPrefManager!!.getBoolean(SharedPrefrenceUtils.Companion.IsOpenFirstTime,true))
        {
            if(sharedPrefManager!!.getBoolean(SharedPrefrenceUtils.Companion.IsLogin,false))
            {
                var goToSplash=Intent(this, SplachScreen::class.java)
                startActivity(goToSplash)
                finish()
            }
            else
            {
                var goToSplash=Intent(this, SplachScreen::class.java)
                startActivity(goToSplash)
                finish()
            }

        }

        // set page Viewr
        setViewPager()
        //init indecaor dots array
        initIndecetor()
        //set  first active
        indecatorStatus(0)



        CrdSignUpIntroducation.setOnClickListener{
            var editor= sharedPrefManager!!.edit()
            editor.putBoolean(SharedPrefrenceUtils.Companion.IsOpenFirstTime,false)
            editor.commit()
            var goToLogin=Intent(this, LoginActivity::class.java)
            startActivity(goToLogin)
            finish()

        }


    }

    private fun initIndecetor() {
        dots.add(ImgSliderFirstIntroduction)
        dots.add(ImgSliderSecondIntroduction)
        dots.add(ImgSliderThirdIntroduction)
    }

    private fun setViewPager() {
        photoList.add(R.drawable.img_introduction_first_slider)
        photoList.add(R.drawable.img_introducation_second_slider)
        photoList.add(R.drawable.img_introduction_third_slider)
        var sliderAdapter = SliderAdapterIntroducation(photoList,this@IntroductionActivity)
        ViewPagerSliderIntroducation.setAdapter(sliderAdapter)

        ViewPagerSliderIntroducation.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                indecatorStatus(position)
            }

        })
    }

    fun indecatorStatus(posiotion : Int)
    {
        dots[lastPagePostion].setImageResource(R.drawable.ic_intro_non_selected_tab)
        dots[posiotion].setImageResource(R.drawable.ic_intro_selected_tab)
        lastPagePostion=posiotion

    }

    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)

    }

}

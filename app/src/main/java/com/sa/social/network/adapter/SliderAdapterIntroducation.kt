package com.sa.social.network.adapter

import android.content.Context
import android.view.View
import androidx.viewpager.widget.PagerAdapter
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import com.sa.social.network.R
import kotlinx.android.synthetic.main.item_slider_introduction.view.*


class SliderAdapterIntroducation(var photoList : ArrayList<Int>,var context: Context) : PagerAdapter(){

   lateinit var layoutInflator : LayoutInflater

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var view=layoutInflator.inflate(R.layout.item_slider_introduction,container,false)
        view.ImgSliderIntroducation.setImageResource(photoList[position])
        container.addView(view)

        return view
    }

    override fun getCount(): Int {
       return photoList.size
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        var view=`object` as View
        container.removeView(view)
    }

}
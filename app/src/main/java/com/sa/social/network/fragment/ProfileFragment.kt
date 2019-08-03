package com.sa.social.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sa.social.network.R
import com.sa.social.network.adapter.PostsAdapterProfile
import com.sa.social.network.model.Posts
import com.sa.social.network.model.User
import com.sa.social.network.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.view.*







class ProfileFragment : Fragment(){
    lateinit var profileViewModel : ProfileViewModel
    var TAG = this.javaClass.simpleName
    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel= ViewModelProviders.of(this).get(ProfileViewModel::class.java);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view= inflater.inflate(R.layout.fragment_profile, container, false);
        view.PrgLoadDataProfile.visibility=View.VISIBLE
        var profilePostsData=profileViewModel.getProfileData()
        var profileData=profileViewModel.getProfile()

        view.RecyPhotosPostsProfile.apply {
            layoutManager=  GridLayoutManager(activity,3)
            adapter = PostsAdapterProfile(profilePostsData,context!!)
        }



        profileViewModel.getProfilePost().observe(this, Observer<ArrayList<Posts>> {
            (view.RecyPhotosPostsProfile.adapter as PostsAdapterProfile).notifyDataSetChanged()
            view.PrgLoadDataProfile.visibility=View.GONE
        })

        profileViewModel.getUsetDate().observe(this, Observer <User>{
               profileData=it
               view.TxtUserNameProfile.text=it.userName
                 val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                 Glide.with(view.context)
                .load(profileData.profilePhotoUrl)
                     .apply(requestOptions)
                     .placeholder(com.sa.social.network.R.drawable.ic_avatar_profile)
                .into(view.ImgProfileImgProfile)



             view.PrgLoadDataProfile.visibility=View.GONE
        })
        view.CrdEditProfile.setOnClickListener {
            var editProfile=EditProfileFragment.newInstance()
            val bundle = Bundle()
            val obj = profileData
            bundle.putSerializable("user", obj)
            editProfile.setArguments(bundle);
            val transaction =activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.FragMainActivity,editProfile)
            transaction.addToBackStack("ProfileFragment")
            transaction.commit()
        }


        return view
    }

}
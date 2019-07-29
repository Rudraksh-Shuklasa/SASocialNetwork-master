package com.sa.social.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sa.social.network.R
import com.sa.social.network.databinding.FragmentEditProfileBinding
import com.sa.social.network.model.User
import com.sa.social.network.viewmodel.EditProfileViewModel
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*


class EditProfileFragment : Fragment(){
    lateinit var editProfileViewModel : EditProfileViewModel
    var TAG = this.javaClass.simpleName
    companion object {
        fun newInstance(): EditProfileFragment {
            return EditProfileFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editProfileViewModel= ViewModelProviders.of(this).get(EditProfileViewModel::class.java);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding = DataBindingUtil.inflate<FragmentEditProfileBinding>(inflater, R.layout.fragment_edit_profile, container, false)
        var view= binding.root
        binding.model=editProfileViewModel.getProfile()

        editProfileViewModel.getUsetDate().observe(this, Observer <User>{
            binding.model=it
        })



        view!!.ToolEditProfile.title="Edit Profile"
        view!!.ToolEditProfile.inflateMenu(R.menu.menu_toolbar_edit_profile)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        when(item.itemId)
        {
            R.id.btn_save_edit_menu ->
            {

            }
            R.id.btn_close_edit_menu ->
            {

            }
        }
    }



}
package com.sa.social.network.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.sa.social.network.views.LoginActivity
import com.sa.social.network.R
import com.sa.social.network.databinding.FragmentEditProfileBinding
import com.sa.social.network.model.User
import com.sa.social.network.utils.SharedPrefrenceUtils
import com.sa.social.network.viewmodel.EditProfileViewModel
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
import java.io.IOException


class EditProfileFragment : Fragment(){
    lateinit var editProfileViewModel : EditProfileViewModel
    val requestIdForPermissions = 1
    val requestIdForCameraIntent= 2
    val requestIdForGalleryIntent= 3
    var user = User()
    lateinit var updatedImage : Bitmap

    var isUserChangeInfo = false
    lateinit var profilePhoto : ImageView

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
        var bundle = getArguments()
        var view= binding.root
        user=editProfileViewModel.getProfile()
        binding.model=bundle!!.getSerializable("user") as User

        editProfileViewModel.getUsetDate().observe(this, Observer <User>{
            user=it
            val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(view.context)
                .load(user.profilePhotoUrl)
                .apply(requestOptions)
                .placeholder(com.sa.social.network.R.drawable.ic_avatar_profile)
                .into(view.ImgProfileImgProfile)
            binding.model=it
        })

        profilePhoto=view.findViewById(R.id.ImgProfileImgProfile)
        view.TxtChangeProfilePhotoEditProfile.setOnClickListener {
            if (((ContextCompat.checkSelfPermission(activity!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)) != PackageManager.PERMISSION_GRANTED))
            {

                if ((ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(
                        activity!!, Manifest.permission.CAMERA)))
                {

                    Snackbar.make(
                        activity!!.findViewById(android.R.id.content),
                        "Please Grant Permissions to upload profile photo",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE"
                    ) {
                        requestPermissions(
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), requestIdForPermissions)
                    }.show()
                }
                else
                {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), requestIdForPermissions)
                }
            }
            else
            {
                val pictureDialog = AlertDialog.Builder(activity!!)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems
                ) { dialog, which ->
                    when (which) {
                        0 -> {
                            val galleryIntent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                            startActivityForResult(galleryIntent, requestIdForGalleryIntent)
                        }
                        1 -> {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, requestIdForCameraIntent)
                        }
                    }
                }
                pictureDialog.show()
            }

        }

        view!!.ToolEditProfile.title="Edit Profile"
        view!!.ToolEditProfile.inflateMenu(R.menu.menu_toolbar_edit_profile);
        view!!.ToolEditProfile.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener{

            when(it.itemId)
            {
                R.id.btn_save_edit_menu ->
                {
                    user.userName=EdtUpdateUserNameEditProfile.text.toString()
                    editProfileViewModel.setUserProfile(user)
                    activity?.getSupportFragmentManager()?.popBackStack()
                }
                R.id.btn_close_edit_menu ->
                {
                    activity?.getSupportFragmentManager()?.popBackStack()
                }

                R.id.btn_logout_menu ->
                {
                    FirebaseAuth.getInstance().signOut()

                    SharedPrefrenceUtils.removeSharedPrefrence(activity!!.applicationContext)
                    var goToLogin=Intent(activity, LoginActivity::class.java)
                    startActivity(goToLogin)
                    activity!!.finish()
                }
            }
            return@OnMenuItemClickListener true



    });
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu. menu_toolbar_edit_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        when(item.itemId)
        {
            R.id.btn_save_edit_menu ->
            {
                editProfileViewModel.setUserProfile(updatedImage,user)
                activity?.getSupportFragmentManager()?.popBackStack()
            }
            R.id.btn_close_edit_menu ->
            {
                activity?.getSupportFragmentManager()?.popBackStack()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK)
        {
            when(requestCode){
                requestIdForGalleryIntent -> {
                    if (data != null)
                    {
                        val contentURI = data!!.data
                        try
                        {
                            updatedImage = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI)
                            profilePhoto.setImageBitmap(updatedImage)
                            editProfileViewModel.setUserProfile(updatedImage,user)
                        }
                        catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                requestIdForCameraIntent -> {
                    val updatedImage = data!!.extras!!.get("data") as Bitmap
                    profilePhoto.setImageBitmap(updatedImage)
                    editProfileViewModel.setUserProfile(updatedImage,user)

                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()

    }



}
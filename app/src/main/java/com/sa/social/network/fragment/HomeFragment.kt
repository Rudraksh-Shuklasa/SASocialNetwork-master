package com.sa.social.network.fragment

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sa.social.network.R
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sa.social.network.adapter.PostsAdapterProfile
import com.sa.social.network.adapter.PostsFeedAdapter
import com.sa.social.network.model.Posts
import com.sa.social.network.viewmodel.HomeViewModel
import com.sa.social.network.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.dialog_post.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.item_feed_post.view.*
import java.io.IOException


class HomeFragment : Fragment(){

    val requestIdForPermissions = 1
    val requestIdForCameraIntent= 2
    val requestIdForGalleryIntent= 3
    lateinit var homeViewModel : HomeViewModel

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel= ViewModelProviders.of(this).get(HomeViewModel::class.java);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view =inflater.inflate(com.sa.social.network.R.layout.fragment_home, container, false);

        var profilePostsData=homeViewModel.getPostFeed()


        view.RcyPostFeedHome.apply {
            layoutManager=  GridLayoutManager(activity,1)
            adapter = PostsFeedAdapter(profilePostsData,context!!,homeViewModel)
        }

        homeViewModel.getPostFeedLiveData().observe(this, Observer<ArrayList<Posts>> {
            var profilePostsData=homeViewModel.getPostFeed()
            (view.RcyPostFeedHome.adapter as PostsFeedAdapter).notifyDataSetChanged()
            profilePostsData=it
        })

        homeViewModel.getImageUploadLiveData().observe(this, Observer<Boolean> {
            if(it){
                PrgUploadImageHome.visibility=View.GONE
            }

        })

        view.SwpLytFeedHome.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                (view.RcyPostFeedHome.adapter as PostsFeedAdapter).notifyDataSetChanged()
                view.SwpLytFeedHome.isRefreshing=false
            }
        })

        view.FabAddImageFeedHome.setOnClickListener {
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
                            val galleryIntent = Intent(Intent.ACTION_PICK,
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

        return view
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode)
        {
            requestIdForPermissions -> {

            if (grantResults.size > 0)
            {
                val cameraPermission = grantResults[1] === PackageManager.PERMISSION_GRANTED
                val readExternalFile = grantResults[0] === PackageManager.PERMISSION_GRANTED

               if (cameraPermission && readExternalFile)
                {
                    Toast.makeText(activity,"Permission Granted",Toast.LENGTH_SHORT).show()
                }
                else
               {
                  Snackbar.make(
                              activity!!.findViewById(android.R.id.content),
                        "Please Grant Permissions to upload profile photo",
                              Snackbar.LENGTH_INDEFINITE).setAction("ENABLE")
                  {
                   requestPermissions(
                          arrayOf(Manifest.permission
                          .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                           requestIdForPermissions)
                  }.show()
                }
             }
            }
        }
    }

    fun uploadPOstDialog(bitmap : Bitmap)
    {
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_post, null)
        val mBuilder = android.app.AlertDialog.Builder(context)
            .setView(mDialogView)
            .setCancelable(false)
        val  mAlertDialog = mBuilder.show()
        mDialogView.ImgPostImageDialog.setImageBitmap(bitmap)




        mDialogView.BtnUploadPostDialog.setOnClickListener {
            val descripition=mDialogView.EdtDescripitionPostdialog.text.toString()
            PrgUploadImageHome.visibility=View.VISIBLE
            homeViewModel.uploadImage(bitmap,descripition)
            mAlertDialog.dismiss()
        }
        mDialogView.ImgClosePostDialog.setOnClickListener {
            mAlertDialog.dismiss()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==RESULT_OK)
        {
            when(requestCode){
                requestIdForGalleryIntent -> {
                    if (data != null)
                    {
                        val contentURI = data!!.data
                        try
                        {
                            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI)
                            uploadPOstDialog(bitmap)
                        }
                        catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                requestIdForCameraIntent -> {
                    val thumbnail = data!!.extras!!.get("data") as Bitmap
                    uploadPOstDialog(thumbnail)
                }
            }
        }
    }
}
package com.sa.social.network.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sa.social.network.R
import com.sa.social.network.model.Posts
import kotlinx.android.synthetic.main.dialog_full_post.view.*

class PostsAdapterProfile(val data: ArrayList<Posts>,val context:Context): RecyclerView.Adapter<PostsAdapterProfile.ViewHolder>()
{
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number = data[position]
        holder.bind(number.photosUrl)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_photos_profile, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var img: ImageView = view.findViewById(R.id.ImgPhotosFeed)
        init {
            img=view.findViewById(R.id.ImgPhotosFeed)
            img.setOnClickListener {
                uploadPOstDialog(data[adapterPosition].photosUrl)
            }
        }

        fun bind(url: String) {
            Glide.with(context).load(url).thumbnail(0.5f)
                .centerCrop()
                .placeholder(R.drawable.ic_avatar_profile)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(img);
        }
    }

    fun uploadPOstDialog(url: String) {
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_full_post, null)
        val mBuilder = android.app.AlertDialog.Builder(context)
            .setView(mDialogView)
            .setCancelable(false)
        val mAlertDialog = mBuilder.show()

        Glide.with(context).load(url).thumbnail(0.5f)
            .centerCrop()
            .placeholder(R.drawable.ic_avatar_profile)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(mDialogView.ImgPostImage);



        mDialogView.ImgCloseImageDialog.setOnClickListener {
            mAlertDialog.dismiss()
        }


    }
}

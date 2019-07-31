package com.sa.social.network.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextClock
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sa.social.network.R
import com.sa.social.network.model.Posts
import com.sa.social.network.viewmodel.HomeViewModel
import android.R.attr.author
import android.os.Bundle
import com.sa.social.network.fragment.CommentsFragment
import androidx.appcompat.app.AppCompatActivity





class PostsFeedAdapter(val data: ArrayList<Posts>, val context: Context,var homeViewModel: HomeViewModel): RecyclerView.Adapter<PostsFeedAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsFeedAdapter.ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_feed_post, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PostsFeedAdapter.ViewHolder, position: Int) {
        val post = data[position]
        holder.bind(post)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view =view
        private var postImage: ImageView
        private var userName : TextView
        private var numberOfLikes : TextView
        private var likeToggle:ToggleButton
        private var description : TextView
        private var numberOfComments : TextView
        private var imgAddComment : ImageView

        init {
            postImage= view.findViewById(R.id.ImgPostPicturePostFeed)
            userName = view.findViewById(R.id.TxtUserNamePostFeed)
            numberOfLikes = view.findViewById(R.id.TxtNumberOfLikedPostFeed)
            likeToggle = view.findViewById(R.id.TglbtnIsPostLikedPostFeed)
            description =view.findViewById(R.id.TxtDesciprtionPostFeed)
            numberOfComments = view.findViewById(R.id.TxtCommentOfLikedPostFeed)
            imgAddComment =view.findViewById(R.id.ImgAddCommentPostFeed)

            likeToggle.setOnClickListener {
                if (likeToggle.isChecked) {
                    data.get(adapterPosition).isLikedPost=true
                    data.get(adapterPosition).likes= data.get(adapterPosition).likes+1
                    numberOfLikes.text= data.get(adapterPosition).likes.toString()
                    homeViewModel.updatePost(data.get(adapterPosition),true)

                }
                else
                {
                    data.get(adapterPosition).isLikedPost=false
                    data.get(adapterPosition).likes= data.get(adapterPosition).likes-1
                    numberOfLikes.text= data.get(adapterPosition).likes.toString()
                    homeViewModel.updatePost(data.get(adapterPosition),false)
                }
                notifyDataSetChanged()
            }
        }

        fun bind(post: Posts) {

            Glide.with(context).load(post.photosUrl).thumbnail(0.5f)
                .centerCrop()
                .placeholder(R.drawable.ic_avatar_profile)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(postImage)

            userName.text=post.userName
            numberOfLikes.text=post.likes.toString()
            description.text=post.descripition
            numberOfComments.text=post.comments.toString()

            if(post.isLikedPost)
                likeToggle.setChecked(true)
            else
                likeToggle.setChecked(false)


            imgAddComment.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("postId",post.postId)


                val commentFragment = CommentsFragment.newInstance()
                commentFragment.setArguments(bundle)
                val activity =  view.context as AppCompatActivity
                activity.supportFragmentManager.apply {
                    beginTransaction().replace(com.sa.social.network.R.id.FragMainActivity, commentFragment)
                        .addToBackStack("HomeFragment").commit()
                }
            }
        }
    }

}
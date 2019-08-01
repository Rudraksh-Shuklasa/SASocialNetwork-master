package com.sa.social.network.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sa.social.network.R
import com.sa.social.network.model.Posts
import com.sa.social.network.viewmodel.HomeViewModel
import android.R.attr.author
import android.os.Bundle
import android.util.Log
import com.sa.social.network.fragment.CommentsFragment
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sa.social.network.model.Comments


class PostsFeedAdapter(var context: Context,homeViewModel : HomeViewModel): PagedListAdapter<Posts, PostsFeedAdapter.ViewHolder>(diffCallback)
{
    lateinit var homeViewModel : HomeViewModel

    init {
        this.homeViewModel = homeViewModel
    }
    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Posts>() {
            override fun areItemsTheSame(oldItem: Posts, newItem: Posts): Boolean {
                return  oldItem.postId == newItem.postId
            }


            override fun areContentsTheSame(oldItem: Posts, newItem: Posts): Boolean {
                return oldItem == newItem
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsFeedAdapter.ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_feed_post, parent, false)
        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: PostsFeedAdapter.ViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post!!)

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
                    getItem(adapterPosition)!!.isLikedPost=true
                    getItem(adapterPosition)!!.likes=  getItem(adapterPosition)!!.likes+1
                    numberOfLikes.text=  getItem(adapterPosition)!!.likes.toString()
                    homeViewModel.updatePostLikes( getItem(adapterPosition)!!.postId,true)

                }
                else
                {
                    getItem(adapterPosition)!!.isLikedPost=false
                    getItem(adapterPosition)!!.likes= getItem(adapterPosition)!!.likes-1
                    numberOfLikes.text=  getItem(adapterPosition)!!.likes.toString()
                    homeViewModel.updatePostLikes( getItem(adapterPosition)!!.postId,false)
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


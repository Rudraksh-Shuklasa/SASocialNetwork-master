package com.sa.social.network.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sa.social.network.R
import com.sa.social.network.model.ChatUser
import com.sa.social.network.model.User
import com.sa.social.network.viewmodel.ChatViewModel



class ChatUserAdapter(var chatUser : ArrayList<ChatUser>, context: Context, chatViewHolder: ChatViewModel): RecyclerView.Adapter<ChatUserAdapter.ViewHolder>()
{


    var context : Context
    var chatViewHolder : ChatViewModel

    init{
        this.context= context
        this.chatViewHolder=chatViewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = chatUser[position]
        holder.bind(user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_chat_user, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chatUser.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var userImg: ImageView
        private var userName: TextView
        private var unreadCount : TextView

        init {
            userImg=view.findViewById(R.id.ImgUserProfileChatItem)
            userName=view.findViewById(R.id.TxtUserNameChatItem)
            unreadCount=view.findViewById(R.id.TxtNumberOfUnreadMessages)


        }

        fun bind(user: ChatUser) {
            Glide.with(context).load(user.userProfilePicture).thumbnail(0.5f)
                .centerCrop()
                .placeholder(R.drawable.ic_avatar_profile)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(userImg);
            userName.text=user.userName
            unreadCount.text=user.numberOfUnreadMessage.toString()
        }
    }
}

package com.sa.social.network.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.sa.social.network.R
import com.sa.social.network.model.Comments


class PostCommentAdapter(val data: ArrayList<Comments>, val context: Context): RecyclerView.Adapter<PostCommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_comments_post, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = data[position]
        holder.bind(post)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var userName: TextView = view.findViewById(R.id.TxtNotificationTitle)
        private var commentText : TextView = view.findViewById(R.id.TxtCommentTextCommentItme)

        fun bind(comment: Comments) {
            userName.text=comment.userName
            commentText.text=comment.commentText
        }
    }
}








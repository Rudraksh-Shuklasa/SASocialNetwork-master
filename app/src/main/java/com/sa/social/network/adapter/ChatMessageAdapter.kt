package com.sa.social.network.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sa.social.network.R
import com.sa.social.network.model.ChatMessage
import com.sa.social.network.utils.SharedPrefrenceUtils
import android.widget.LinearLayout


class ChatMessageAdapter(context: Context): PagedListAdapter<ChatMessage, ChatMessageAdapter.ViewHolder>(diffCallback)
{


    var context : Context
    var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(context)


    companion object {

    private val diffCallback = object : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return  oldItem.messageId == newItem.messageId
        }


        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }

    }
}
    init{
        this.context= context

    }

    fun updateData(chatMessge : ChatMessage){

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ViewHolder(itemView)
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var userName: TextView
        private var message : TextView
        private var chatView : CardView
        private var messageTime : TextView

        init {
            userName=view.findViewById(R.id.TxtChatOwnerName)
            message=view.findViewById(R.id.TxtChatMessage)
            chatView = view.findViewById(R.id.CrdChatMessageBody)
            messageTime = view.findViewById(R.id.TxtMesageTimeChatMessage)

        }

        fun bind(user: ChatMessage) {
            userName.text=user.userName
            message.text=user.message

            val sdf = java.text.SimpleDateFormat("HH:mm'  'MM-dd")
            val date = java.util.Date(user.timestamp * 1000)
            messageTime.text=sdf.format(date)


            val paramsMsg = LinearLayout.LayoutParams(
              chatView.layoutParams.width,chatView.layoutParams.height
            )
            if(user.userId.equals(sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId, "user").toString()))
            {
                paramsMsg.gravity = Gravity.END;
            }
            else{
                paramsMsg.gravity = Gravity.START;
            }
            chatView.layoutParams=paramsMsg
        }
    }
}
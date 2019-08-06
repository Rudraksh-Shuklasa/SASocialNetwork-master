package com.sa.social.network.fragment

import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sa.social.network.R
import com.sa.social.network.adapter.ChatMessageAdapter
import com.sa.social.network.adapter.ChatMessageAdapterSimple
import com.sa.social.network.adapter.NotificationAdapter
import com.sa.social.network.model.ChatMessage
import com.sa.social.network.model.ChatUser
import com.sa.social.network.utils.SharedPrefrenceUtils
import com.sa.social.network.viewmodel.ChatScreenViewHolder
import com.sa.social.network.viewmodel.ChatViewModel
import com.sa.social.network.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.dialog_full_post.view.*
import kotlinx.android.synthetic.main.fragment_chat_screen.*
import kotlinx.android.synthetic.main.fragment_chat_screen.view.*







class ChatScreenFragment : Fragment(){

    lateinit var chatScreenViewHolder: ChatScreenViewHolder
    var TAG = this.javaClass.simpleName
    private lateinit var mAdapter : ChatMessageAdapterSimple

    companion object {
        fun newInstance(): ChatScreenFragment {
            return  ChatScreenFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chatScreenViewHolder = ViewModelProviders.of(this).get(ChatScreenViewHolder::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(com.sa.social.network.R.layout.fragment_chat_screen, container, false)

        var bundle = getArguments()
        var chatUser=bundle!!.getSerializable("user") as ChatUser
        var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(activity!!.applicationContext)

        var chatMessage = chatScreenViewHolder.getMessageData(chatUser.userId)

        chatScreenViewHolder.makeUnreadMessageCountZero(chatUser)
        view.TxtUserNameChatScreen.text=chatUser.userName

        chatScreenViewHolder.setView(chatUser.userId)
        activity?.let {
            Glide.with(it).load(chatUser.userProfilePicture).thumbnail(0.5f)
                .centerCrop()
                .placeholder(R.drawable.ic_avatar_profile)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view.ImgUserProfileChatScreen)
        }
        mAdapter = ChatMessageAdapterSimple(chatMessage,context!!)



        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.reverseLayout = true
        view.RcyChatMessages.apply {
            layoutManager =linearLayoutManager
            adapter=mAdapter
           }





//        chatScreenViewHolder.chatMessagePagedList.observe(this , Observer {
//            mAdapter.submitList(it)
//
//        })

        chatScreenViewHolder.getMessageLiveData().observe(this, Observer {
            mAdapter.notifyChange(it)
            linearLayoutManager.scrollToPositionWithOffset(0, 0);
        })

        chatScreenViewHolder.getIsMessageIsLoding().observe(this, Observer {
            if(it)
            {
                view.PrgChatMessageLoding.visibility=View.GONE
            }
        })

        view.TxtChat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d(TAG,p0.toString())
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d(TAG,p0.toString())
            }

            override fun afterTextChanged(text: Editable?) {
                if (view.TxtChat.text.toString().length > 0) {
                    view.BtnAddChat.setTextColor(resources.getColor(R.color.darkPrimary))
                    view.BtnAddChat.isEnabled = true
                    view.BtnAddChat.isClickable = true
                } else {
                    view.BtnAddChat.setTextColor(resources.getColor(R.color.lightPrimary))
                    view.BtnAddChat.isEnabled = false
                    view.BtnAddChat.isClickable = false

                }
            }
        })




                chatScreenViewHolder.isChatMessageLoaded().observe(this, Observer<Boolean> {
            if (it) {
                PrgChatMessageLoding.visibility = View.GONE
            }
            else{
                PrgChatMessageLoding.visibility = View.VISIBLE
            }

        })

        view.BtnAddChat.setOnClickListener {
            var chatMessage=ChatMessage("",
                sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId, "user").toString(),
                sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserName, "user").toString(),
                TxtChat.text.toString(),0)
            chatScreenViewHolder.sendMessage(chatMessage,chatUser.userId)
            chatScreenViewHolder.refreshChat()

            view.TxtChat.setText("")

            Toast.makeText(activity,"Chat Added",Toast.LENGTH_SHORT).show()

            Log.d(TAG,"Chat Added")
        }

        return view
    }


}
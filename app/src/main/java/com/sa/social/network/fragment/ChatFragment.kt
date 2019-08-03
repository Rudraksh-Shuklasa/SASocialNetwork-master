package com.sa.social.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.sa.social.network.R
import com.sa.social.network.adapter.ChatUserAdapter
import com.sa.social.network.adapter.ChatUserRequestAdapter
import com.sa.social.network.model.ChatUser
import com.sa.social.network.model.User
import com.sa.social.network.viewmodel.ChatViewModel
import kotlinx.android.synthetic.main.fragment_chat.view.*

class ChatFragment : Fragment()
{
   lateinit var chatViewHolder : ChatViewModel
    var TAG = this.javaClass.simpleName
    companion object {

        fun newInstance(): ChatFragment {


            return  ChatFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatViewHolder= ViewModelProviders.of(this).get(ChatViewModel::class.java)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(com.sa.social.network.R.layout.fragment_chat, container, false);
        view.PrgChatLoding.visibility=View.VISIBLE
        var userData=chatViewHolder.getChatUserData()


        view.RcyUserChatListChatFragment.apply {
            layoutManager=  GridLayoutManager(activity,1)
            adapter = ChatUserAdapter(userData,context!!,chatViewHolder)
        }
        chatViewHolder.getChatUserLiveData().observe(this, Observer<ArrayList<ChatUser>> {
            view.RcyUserChatListChatFragment.apply {
                layoutManager=  GridLayoutManager(activity,1)
                adapter = ChatUserAdapter(it,context!!,chatViewHolder)
            }
            view.PrgChatLoding.visibility=View.GONE
        })
        chatViewHolder.ChatIsLoaded().observe(this, Observer {
            if(it)
            {
                view.PrgChatLoding.visibility=View.GONE
            }
        })




        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val transaction =activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.FragMainActivity,HomeFragment.newInstance())
        transaction.commit()
    }





}

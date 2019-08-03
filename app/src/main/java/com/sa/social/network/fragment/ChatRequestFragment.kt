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
import com.sa.social.network.adapter.ChatUserRequestAdapter
import com.sa.social.network.model.User
import com.sa.social.network.viewmodel.ChatViewModel
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.fragment_chat_request.view.*

class ChatRequestFragment : Fragment()
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
        var view = inflater.inflate(com.sa.social.network.R.layout.fragment_chat_request, container, false);
        view.PrgChatLoding.visibility=View.VISIBLE
        var userData=chatViewHolder.getUserData()


        view.RcyUserChatListRequestChatFragment.apply {
            layoutManager=  GridLayoutManager(activity,1)
            adapter = ChatUserRequestAdapter(userData,context!!,chatViewHolder)
        }
        chatViewHolder.getUserRequestLiveData().observe(this, Observer<ArrayList<User>> {
            view.RcyUserChatListChatFragment.apply {
                layoutManager=  GridLayoutManager(activity,1)
                adapter = ChatUserRequestAdapter(it,context!!,chatViewHolder)
            }
            view.PrgRequestChatLoding.visibility=View.GONE
        })
        chatViewHolder.isUserAdded().observe(this, Observer {
            if(it)
            {
                view.PrgRequestChatLoding.visibility=View.GONE
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

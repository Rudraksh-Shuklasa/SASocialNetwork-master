package com.sa.social.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sa.social.network.R
import com.sa.social.network.adapter.ChatUserAdapter
import com.sa.social.network.adapter.ChatUserRequestAdapter
import com.sa.social.network.model.User
import com.sa.social.network.viewmodel.ChatViewModel
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.fragment_chat_request.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class ChatRequestFragment : Fragment()
{
    lateinit var chatViewHolder : ChatViewModel
    var TAG = this.javaClass.simpleName
    companion object {

        fun newInstance(): ChatRequestFragment {


            return  ChatRequestFragment()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatViewHolder= ViewModelProviders.of(this).get(ChatViewModel::class.java)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(com.sa.social.network.R.layout.fragment_chat_request, container, false);
        view.PrgRequestChatLoding.visibility=View.VISIBLE
        var userData=chatViewHolder.getUserData()

        view!!.ToolBackToChat.title="Friend Request"
        view!!.ToolBackToChat.inflateMenu(R.menu.menu_friend_request)
        view!!.ToolBackToChat.setTitleTextColor(resources.getColor(R.color.colorPrimaryDark))
        view!!.ToolBackToChat.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener{

            when(it.itemId)
            {
                R.id.btn_close_firend_reqest_menu ->
                {
                    val transaction =activity!!.supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.FragMainActivity,ChatFragment.newInstance())
                    transaction.addToBackStack("ChatFragment")
                    transaction.commit()
                }

            }
            return@OnMenuItemClickListener true



        });

        view.SwpLytChatRequest.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                var userData=chatViewHolder.getUserData()
                view.RcyUserChatListRequestChatFragment.apply {
                    layoutManager=  GridLayoutManager(activity,1)
                    adapter = ChatUserRequestAdapter(userData,context!!,chatViewHolder)
                }
                view.SwpLytChatRequest.isRefreshing = false
            }
        })

        view.RcyUserChatListRequestChatFragment.apply {
            layoutManager=  GridLayoutManager(activity,1)
            adapter = ChatUserRequestAdapter(userData,context!!,chatViewHolder)
        }
        chatViewHolder.getUserRequestLiveData().observe(this, Observer<ArrayList<User>> {
            view.RcyUserChatListRequestChatFragment.apply {
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







}

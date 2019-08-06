package com.sa.social.network.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sa.social.network.adapter.NotificationAdapter
import com.sa.social.network.adapter.PostsFeedAdapter
import com.sa.social.network.viewmodel.HomeViewModel
import com.sa.social.network.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_notication.*
import kotlinx.android.synthetic.main.fragment_notication.view.*

class NotificationFragment : Fragment(){
    var TAG = this.javaClass.simpleName
    lateinit var notificationViewModel: NotificationViewModel
    private lateinit var mAdapter : NotificationAdapter

    companion object {
        private lateinit var notificationFragment: NotificationFragment
        fun newInstance(): NotificationFragment
        {
            return NotificationFragment()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(com.sa.social.network.R.layout.fragment_notication, container, false)

        mAdapter = NotificationAdapter(context!!,notificationViewModel)

        view.RcyNotificationListNotification.apply {
            layoutManager = GridLayoutManager(context,1)
            adapter=mAdapter
        }

        notificationViewModel.notificationPagedList.observe(this , Observer {
            mAdapter.submitList(it)

        })

        view.SwpLytNotification.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                notificationViewModel.refreshNotification()
                view.SwpLytNotification.isRefreshing = false
            }
        })
        notificationViewModel.isNotificationLoded().observe(this, Observer<Boolean> {
            if (it) {
                PrgNotificationLoding.visibility = View.GONE
            }
            else{
                PrgNotificationLoding.visibility = View.VISIBLE
            }

        })

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)){
            override  fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                notificationViewModel.deleteNotification(mAdapter.getNotificationAt(viewHolder.adapterPosition)!!)
                Toast.makeText(activity, "Notification Deleted", Toast.LENGTH_SHORT).show()

            }
        }).attachToRecyclerView(view.RcyNotificationListNotification)


        return view
    }




}
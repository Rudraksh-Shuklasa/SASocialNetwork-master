package com.sa.social.network.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sa.social.network.R
import com.sa.social.network.model.Notification
import com.sa.social.network.viewmodel.NotificationViewModel

class NotificationAdapter(var context: Context, notificationViewModel : NotificationViewModel): PagedListAdapter<Notification, NotificationAdapter.ViewHolder>(diffCallback)
{
    lateinit var notificationViewModel : NotificationViewModel

    init {
        this.notificationViewModel = notificationViewModel
    }
    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return  oldItem.notificationId == newItem.notificationId
            }


            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem == newItem
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {
        val notification = getItem(position)
        holder.bind(notification!!)
    }



    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view =view
        private var notificationTitle: TextView
        private var notificationBody : TextView
        private var notificationDate : TextView


        init {
            notificationTitle= view.findViewById(R.id.TxtNotificationTitle)
            notificationBody = view.findViewById(R.id.TxtNotificationBody)
            notificationDate = view.findViewById(R.id.TxtNotificationTime)
        }

        fun bind(notification: Notification) {
            val sdf = java.text.SimpleDateFormat("HH:mm'  'MM-dd")
            val date = java.util.Date(notification.timestamp * 1000)


            notificationTitle.text=notification.title
            notificationBody.text=notification.body
            notificationDate.text= sdf.format(date)



            }
        }








}
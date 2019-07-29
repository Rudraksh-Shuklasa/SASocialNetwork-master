package com.sa.social.network.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.sql.Timestamp


data class Posts(var postId : String,var userId : String = " ",var photosUrl: String,var timestamp : Long,var descripition : String,var likes: Long = 0)

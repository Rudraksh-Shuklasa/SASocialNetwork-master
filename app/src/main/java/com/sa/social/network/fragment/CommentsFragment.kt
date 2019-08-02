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
import androidx.appcompat.widget.LinearLayoutCompat.VERTICAL
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.sa.social.network.R
import com.sa.social.network.databinding.FragmentCommentsPostBinding
import com.sa.social.network.databinding.FragmentEditProfileBinding
import com.sa.social.network.viewmodel.CommentsViewModel
import com.sa.social.network.viewmodel.EditProfileViewModel
import com.sa.social.network.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_comments_post.view.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.sa.social.network.adapter.PostCommentAdapter
import com.sa.social.network.adapter.PostsFeedAdapter
import com.sa.social.network.model.Comments
import kotlinx.android.synthetic.main.fragment_comments_post.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class CommentsFragment : Fragment()
{
    lateinit var commentsViewHolder : CommentsViewModel
    var TAG = this.javaClass.simpleName
    companion object {
        private var commentFragment: CommentsFragment? = null
        fun newInstance(): CommentsFragment {
            if(commentFragment == null)
                commentFragment = CommentsFragment()
             return CommentsFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commentsViewHolder=ViewModelProviders.of(this).get(CommentsViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentCommentsPostBinding>(
            inflater, R.layout.fragment_comments_post, container, false
        )
        val view = binding.getRoot()
        binding.setLifecycleOwner(this)
        val postId = arguments!!.getString("postId")
        var comments =commentsViewHolder.getComments(postId!!)



        view.RcyCommentList.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity, VERTICAL))
            adapter = PostCommentAdapter(comments,activity!!.applicationContext)
        }

        view.BtnAddComment.setOnClickListener {
            commentsViewHolder.addComment(postId!!,view.TxtComment.text.toString())

            view.TxtComment.setText("")

            Toast.makeText(activity,"Comment Added",Toast.LENGTH_SHORT).show()

            Log.d(TAG,"Post Added")
        }

        commentsViewHolder.getIsCommentAdded().observe(this, Observer<Boolean> {
            if(it)
               view.PrgCommentLoding.visibility=View.GONE
        })

        commentsViewHolder.getCommentData().observe(this, Observer<ArrayList<Comments>> {
            view.RcyCommentList.apply {
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(DividerItemDecoration(activity, VERTICAL))
                adapter = PostCommentAdapter(it,activity!!.applicationContext)
            }
        })

        view.TxtComment.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(text: Editable?) {
                if(view.TxtComment.text.toString().length > 0)
                {
                    view.BtnAddComment.setTextColor(resources.getColor(R.color.darkPrimary))
                    view.BtnAddComment.isEnabled=true
                    view.BtnAddComment.isClickable=true
                }
                else{
                    view.BtnAddComment.setTextColor(resources.getColor(R.color.lightPrimary))
                    view.BtnAddComment.isEnabled=false
                    view.BtnAddComment.isClickable=false

                }


            }

            override fun beforeTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {

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
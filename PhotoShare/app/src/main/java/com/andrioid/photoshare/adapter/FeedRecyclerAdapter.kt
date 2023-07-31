package com.andrioid.photoshare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.andrioid.myapplication.R
import com.andrioid.myapplication.databinding.RecyclerRowBinding
import com.andrioid.photoshare.model.Post
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(val postList : ArrayList<Post>) : RecyclerView.Adapter<FeedRecyclerAdapter.FeedViewHolder>(){

    private lateinit var binding : RecyclerRowBinding
    private val picasso = Picasso.get()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.recycler_row,parent,false)
        return FeedViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.binding.userEmailText.text = postList[position].userEmail
        holder.binding.rowCommentText.text = postList[position].userComment

        picasso.load(postList[position].imageUrl).into(holder.binding.rowImageView)

    }
    class FeedViewHolder(val binding: RecyclerRowBinding) : ViewHolder(binding.root) {
    }
}



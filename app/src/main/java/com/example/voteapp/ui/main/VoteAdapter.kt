package com.example.voteapp.ui.main

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.util.Base64
import com.example.voteapp.R
import com.example.voteapp.data.model.Vote
import com.example.voteapp.ui.vote.VoteDetailsActivity

class VoteAdapter(private val votes:List<Vote>) : RecyclerView.Adapter<VoteAdapter.VoteViewHolder>() {

    inner class VoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.textName)
        val author = itemView.findViewById<TextView>(R.id.textAuthor)
        val category = itemView.findViewById<TextView>(R.id.textCategory)
        val image = itemView.findViewById<ImageView>(R.id.imageViewVote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vote,parent,false)
        return VoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoteViewHolder, position: Int){
        val vote = votes[position]
        holder.name.text = vote.name
        holder.author.text = vote.author
        holder.category.text = vote.category

        try {
            val imageBytes = Base64.decode(vote.image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes,0, imageBytes.size)
            holder.image.setImageBitmap(bitmap)
        }
        catch (e: Exception){
            holder.image.setImageResource(R.drawable.ic_launcher_foreground)
        }

        holder.itemView.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(context, VoteDetailsActivity::class.java)
            intent.putExtra("voteId", vote.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = votes.size
}
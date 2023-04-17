package com.raywenderlich.messanger.recyclerViewComponents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.messanger.R
import com.raywenderlich.messanger.model.User
import com.squareup.picasso.Picasso


class NewMessagesAdaptor(var clickListener: Communicator, var userNameList: ArrayList<User>): RecyclerView.Adapter<NewMessageViewHolder>() {


interface  Communicator{
    fun onItemClicked(user: User)
}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_view,parent,false)
        return NewMessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewMessageViewHolder, position: Int) {
        var item = userNameList[position]
        holder.userName.text = item.userName
        Picasso.get().load(userNameList[position].profileImageUrl).into(holder.circleImage)
        holder.itemView.setOnClickListener {
            clickListener.onItemClicked(userNameList[position])
        }

        }

    override fun getItemCount(): Int {
       return userNameList.size
    }
}


package com.raywenderlich.messanger.recyclerViewComponents

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.messanger.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.view.*

class NewMessageViewHolder(view: View):RecyclerView.ViewHolder(view) {

        val userName: TextView = view.findViewById(R.id.NewMessagesUserNameText)
        val circleImage : CircleImageView = view.findViewById(R.id.circularItemViewImage)

}
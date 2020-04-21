package com.naqelexpress.naqelpointer

import android.support.v7.widget.RecyclerView
import android.view.View
import com.pusher.chatkit.messages.Message
import kotlinx.android.synthetic.main.their_message.view.*


class OthersMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(message: Message) {
        itemView.name.text= message.userId
        itemView.message_body.text=message.text
        //Glide.with(itemView.context).load(movieModel.moviePicture!!).into(itemView.imageMovie)
    }

}

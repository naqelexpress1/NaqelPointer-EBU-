package com.naqelexpress.naqelpointer

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pusher.chatkit.messages.Message
import kotlinx.android.synthetic.main.my_message.view.*


class SelfMeeageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(message: Message) {
        //itemView.text_user_name.text=message.userId
        itemView.chat_message.text = message.text
    }
}
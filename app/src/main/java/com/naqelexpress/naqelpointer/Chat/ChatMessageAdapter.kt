package com.naqelexpress.naqelpointer


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pusher.chatkit.messages.Message

class ChatMessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = ArrayList<Message>()

    fun addMessage(e: Message) {
        list.add(e)
        //notifyDataSetChanged()
        notifyItemInserted(list.size);
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            CellType.OWN.ordinal -> SelfMeeageHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.my_message, parent, false))
            else -> OthersMessageHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.their_message, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            CellType.OWN.ordinal -> {
                val headerViewHolder = holder as SelfMeeageHolder
                headerViewHolder.bindView(list[position])
            }
            CellType.OTHERs.ordinal -> {
                val headerViewHolder = holder as OthersMessageHolder
                headerViewHolder.bindView(list[position])
            }
        }
    }

    enum class CellType(viewType: Int) {

        OWN(0),
        OTHERs(1),
    }

    override fun getItemViewType(position: Int): Int {
        var valid: String = list[position].userId
        var rtn: Boolean = false
       // if (valid.equals(AppController.currentUser.id))
        if (valid.equals(ApplicationController.currentUser.id))
            rtn = true


        return when (rtn) {
            true -> CellType.OWN.ordinal
            else -> CellType.OTHERs.ordinal
        }
    }

}
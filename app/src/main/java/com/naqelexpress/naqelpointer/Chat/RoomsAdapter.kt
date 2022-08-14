package com.naqelexpress.naqelpointer

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pusher.chatkit.rooms.Room
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView

class RoomsAdapter : RecyclerView.Adapter<RoomsAdapter.ViewHolder>() {
    private var list = ArrayList<Room>()

    var onItemClick: ((Room) -> Unit)? = null

    fun addRoom(room: Room) {
        list.add(room)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.chatgroup, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder!!.roomName.text = list[position].name
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var roomName: TextView = itemView!!.findViewById(R.id.groupname)

        init {
            itemView!!.setOnClickListener {
                onItemClick?.invoke(list[adapterPosition])
            }
        }
    }


}
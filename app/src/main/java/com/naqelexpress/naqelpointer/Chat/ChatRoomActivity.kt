package com.naqelexpress.naqelpointer

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.pusher.chatkit.messages.Message
import com.pusher.util.Result
import kotlinx.android.synthetic.main.activity_chat_room.*
import android.view.inputmethod.InputMethodManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pusher.chatkit.presence.Presence
import com.pusher.chatkit.rooms.Room
import com.pusher.chatkit.rooms.RoomEvent
import com.pusher.chatkit.rooms.RoomListeners

class ChatRoomActivity : AppCompatActivity() {
    lateinit var adapter: ChatMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        supportActionBar!!.title = intent.getStringExtra("room_name")
        adapter = ChatMessageAdapter()
        setUpRecyclerView()

        // val currentUser = AppController.currentUser
        val currentUser = ApplicationController.currentUser
        val roomId = intent.getStringExtra("room_id")

//        currentUser.fetchMessages(
//                roomId = roomId,
//                initialId = 0,                       // Optional
//                direction = Direction.NEWER_FIRST,    // Optional - OLDER_FIRST by default
//                limit = 20,                           // Optional - 10 by default
//                callback = { result ->
//                    when (result) {
//                        is Result.Success -> Log.d("TAG", result.value[0].toString()) // List<Message>
//                    }
//                }
//        )

        //subscribeToRoom(RoomsListActivity.Roomobj.roomobj)


        if (roomId != null) {
            currentUser.subscribeToRoom(
                roomId = roomId,
                listeners = RoomListeners(onUserJoined = { message ->


                },
                    onMessage = { message ->
                        message.text?.let { Log.d("TAG", it) }
                        list.add(message)
                        //adapter.notifyDataSetChanged()
                        adapter.addMessage(message)

                        subscribeToRoom()
                    },
                    onErrorOccurred = { error ->
                        Log.d("TAG", error.toString())
                    }
                ),
                messageLimit = 20, // Optional
                callback = { subscription ->
                    // Called when the subscription has started.
                    // You should terminate the subscription with subscription.unsubscribe()
                    // when it is no longer needed
                    // subscribeToRoom(RoomsListActivity.Roomobj.roomobj)
                    subscription.run { }
                    Log.d("TAG", subscription.toString())
                }
            )
        }

        //subscribeToRoom()

        button_send.setOnClickListener {
            if (edit_text.text.isNotEmpty()) {


                if (roomId != null) {
                    currentUser.sendMessage(
                        roomId = roomId,
                        // room = RoomsListActivity.Roomobj.roomobj,
                        messageText = edit_text.text.toString(),
                        callback = { result ->
                            //Result<Int, Error>
                            when (result) {
                                is Result.Success -> {
                                    runOnUiThread {
                                        //subscribeToRoom()

                                        edit_text.text.clear()
                                        hideKeyboard()
                                    }
                                }
                                is Result.Failure -> {
                                    Log.d("TAG", "error: " + result.error.toString())
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = this.currentFocus

        if (view == null) {
            view = View(this)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setUpRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this@ChatRoomActivity) as RecyclerView.LayoutManager?
        recycler_view.adapter = adapter
    }

    private fun subscribeToRoom() {

        ApplicationController.currentUser.subscribeToRoom( //AppController.currentUser.subscribeToRoom(
                //room = RoomsListActivity.Roomobj.roomobj,
                roomId = RoomsListActivity.Roomobj.roomobj.id,
                consumer = { roomEvent: RoomEvent ->
                    when (roomEvent) {
                        is RoomEvent.Message -> {
                            onMessage(RoomsListActivity.Roomobj.roomobj.id, roomEvent.message)
                        }
                        is RoomEvent.UserStartedTyping -> {
                            // onUserTyping(room.id, roomEvent.user, true)
                        }
                        is RoomEvent.UserStoppedTyping -> {
                            // onUserTyping(room.id, roomEvent.user, false)
                        }
                        is RoomEvent.NewReadCursor -> {
                            // onNewReadCursor(room.id, roomEvent.cursor)
                        }
                        is RoomEvent.InitialReadCursors -> {
                            // onInitialReadCursor(roomEvent.cursor)
                        }
                        is RoomEvent.PresenceChange -> {
                            //  onPresenceChanged(room.id, roomEvent.user, roomEvent.currentState)
                        }
                    }
                },
                callback = {
                    subscription ->
                    Log.d("TAG", "")
                    //subscriptionStore.add(subscription)
                    //subscribeToRoom(RoomsListActivity.Roomobj.roomobj)
                    //subscription.unsubscribe()

                },
                messageLimit = 0
        )
    }

    private fun onMessage(id: String, message: Message) {
        list.add(message)
        //    adapter.notifyDataSetChanged()
        adapter.addMessage(message)
        //subscribeToRoom(RoomsListActivity.Roomobj.roomobj)
        subscribeToRoom()


    }

    private var list = ArrayList<Message>()
}

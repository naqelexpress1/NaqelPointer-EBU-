package com.naqelexpress.naqelpointer

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.naqelexpress.naqelpointer.ApplicationController.currentUser
import com.naqelexpress.naqelpointer.Chat.ProgressDialog
//import com.naqelexpress.naqelpointer.AppController.Companion.currentUser
import com.pusher.chatkit.AndroidChatkitDependencies
import com.pusher.chatkit.ChatListeners
import com.pusher.chatkit.ChatManager
import com.pusher.chatkit.ChatkitTokenProvider
import com.pusher.chatkit.rooms.Room
import com.pusher.util.Result
import kotlinx.android.synthetic.main.activity_rooms_list.*

class RoomsListActivity : AppCompatActivity() {
    val adapter = RoomsAdapter()
    var userid: String = ""
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms_list)

        userid = intent.getStringExtra("extra")
        initRecyclerView()
        initChatManager()
        dialog = ProgressDialog.progressDialog(this)
        dialog.show()
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this@RoomsListActivity)
        recycler_view.adapter = adapter

        adapter.onItemClick = { room ->

            if (room.memberUserIds.contains(currentUser.id)) {
                // user already belongs to this room
                roomJoined(room)
            } else {
                currentUser.joinRoom(
                        roomId = room.id,
                        callback = { result ->
                            when (result) {
                                is Result.Success -> {
                                    // Joined the room!
                                    roomJoined(result.value)
                                }
                                is Result.Failure -> {
                                    Log.d("TAG", result.error.toString())
                                }
                            }
                        }
                )
            }

            // roomJoined(contact)
            // do something with your item
            Log.d("TAG", room.name)
        }
    }

    private fun initChatManager() {
        val chatManager = ChatManager(
                instanceLocator = "v1:us1:37e15bed-f2a6-491f-9624-25cd0d47d9c3",
                userId = this.userid,
                dependencies = AndroidChatkitDependencies(
                        tokenProvider = ChatkitTokenProvider(
                                endpoint = "https://us1.pusherplatform.io/services/chatkit_token_provider/v1/37e15bed-f2a6-491f-9624-25cd0d47d9c3/token",//"http://fc74d982.ngrok.io/auth",
                                userId = this.userid
                        )
                )
        )

        // TODO: What do we need to do with these that were here before?
//                .context(this@RoomsListActivity)
//        intent.getStringExtra("extra")

        chatManager.connect(listeners = ChatListeners(
                onErrorOccurred = { },
                onAddedToRoom = { },
                onRemovedFromRoom = { },
                onCurrentUserReceived = { },
                onNewReadCursor = { },
                onRoomDeleted = { },
                onRoomUpdated = { },
                onPresenceChanged = { u, n, p -> },
                onUserJoinedRoom = { u, r -> },
                onUserLeftRoom = { u, r -> },
                onUserStartedTyping = { u, r -> },
                onUserStoppedTyping = { u, r -> }
        )) { result ->
            when (result) {
                is Result.Success -> {
                    // We have connected!
                    val currentUser = result.value
                    //AppController.currentUser = currentUser
                    ApplicationController.currentUser = currentUser
                    val userJoinedRooms = ArrayList<Room>(currentUser.rooms)
                    if (userJoinedRooms.size > 0) {
                        for (i in 0 until userJoinedRooms.size) {
                            runOnUiThread {
                                adapter.addRoom(userJoinedRooms[i])
                            }
                        }
                    } else {
                        currentUser.getJoinableRooms { result ->
                            when (result) {
                                is Result.Success -> {
                                    // Do something with List<Room>
                                    val rooms = result.value
                                    runOnUiThread {
                                        for (i in 0 until rooms.size) {
                                            adapter.addRoom(rooms[i])
                                        }
                                    }
                                }
                            }
                        }
                    }
                    dialog.dismiss()


//                    adapter.setInterface(object : RoomsAdapter.RoomClickedInterface {
//                        override fun roomSelected(room: Room) {
//                            if (room.memberUserIds.contains(currentUser.id)) {
//                                // user already belongs to this room
//                                roomJoined(room)
//                            } else {
//                                currentUser.joinRoom(
//                                        roomId = room.id,
//                                        callback = { result ->
//                                            when (result) {
//                                                is Result.Success -> {
//                                                    // Joined the room!
//                                                    roomJoined(result.value)
//                                                }
//                                                is Result.Failure -> {
//                                                    Log.d("TAG", result.error.toString())
//                                                }
//                                            }
//                                        }
//                                )
//                            }
//                        }
//                    })
                }

                is Result.Failure -> {
                    // Failure
                    Log.d("TAG", result.error.toString())
                    dialog.dismiss()
                }
            }
        }
    }


    private fun roomJoined(room: Room) {
        Roomobj.roomobj = room
        // subscribeToRoom(room)
        val intent = Intent(this@RoomsListActivity, ChatRoomActivitynew::class.java)
        intent.putExtra("room_id", room.id)
        intent.putExtra("room_name", room.name)
        startActivity(intent)
    }

//    private  fun  subscribeToRoom(room: Room) {
//        AppController.currentUser.subscribeToRoom(
//                room = room,
//                consumer = { roomEvent: RoomEvent ->
//                    when (roomEvent) {
//                        is RoomEvent.Message -> {
//                            onMessage(room.id, roomEvent.message)
//                        }
//                        is RoomEvent.UserStartedTyping -> {
//                           // onUserTyping(room.id, roomEvent.user, true)
//                        }
//                        is RoomEvent.UserStoppedTyping -> {
//                           // onUserTyping(room.id, roomEvent.user, false)
//                        }
//                        is RoomEvent.NewReadCursor -> {
//                           // onNewReadCursor(room.id, roomEvent.cursor)
//                        }
//                        is RoomEvent.InitialReadCursors -> {
//                           // onInitialReadCursor(roomEvent.cursor)
//                        }
//                        is RoomEvent.PresenceChange -> {
//                          //  onPresenceChanged(room.id, roomEvent.user, roomEvent.currentState)
//                        }
//                    }
//                },
//                callback = { subscription ->
//                    Log.d("TAG", "")
////                    subscriptionStore.add(subscription)
//                },
//                messageLimit = 20
//        )
//    }
//
//    private fun onMessage(id: String, message: Message) {
//
//    }

    object Roomobj {
        lateinit var roomobj: Room
    }
}
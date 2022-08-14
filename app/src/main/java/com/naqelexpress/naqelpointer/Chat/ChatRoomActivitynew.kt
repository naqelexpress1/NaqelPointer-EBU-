package com.naqelexpress.naqelpointer

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.naqelexpress.naqelpointer.Chat.ProgressDialog
import com.pusher.chatkit.rooms.RoomEvent
import com.pusher.chatkit.rooms.RoomListeners
import com.pusher.util.Result
import kotlinx.android.synthetic.main.activity_chat_room.*

class ChatRoomActivitynew : AppCompatActivity() {
    lateinit var adapter: ChatMessageAdapter
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        supportActionBar!!.title = intent.getStringExtra("room_name")
        adapter = ChatMessageAdapter()
        setUpRecyclerView()
        dialog = ProgressDialog.progressDialog(this)
        dialog.show()

        //if (RoomsListActivity.Roomobj.roomobj.memberUserIds.contains(AppController.currentUser.id)) {
        if (RoomsListActivity.Roomobj.roomobj.memberUserIds.contains(ApplicationController.currentUser.id)) {

            Log.d("TAG", "Room is joined")
        }
        //val currentUser = AppController.currentUser
        val currentUser = ApplicationController.currentUser
        val roomId = intent.getStringExtra("room_id")

//        currentUser.subscribeToRoom(
//                roomId = roomId,
//                listeners = RoomListeners(
//                        onMessage = { message ->
//                            Log.d("TAG", message.text)
//                            adapter.addMessage(message)
//                            //dialog.dismiss()
//                        },
//                        onErrorOccurred = { error ->
//                            Log.d("TAG", error.toString())
//                           // dialog.dismiss()
//                        }
//                ),
//                messageLimit = 100, // Optional
//                callback = { subscription ->
//                    // Called when the subscription has started.
//                    // You should terminate the subscription with subscription.unsubscribe()
//                    // when it is no longer needed
//                }
//        )

        if (roomId != null) {
            currentUser.subscribeToRoom(
                roomId = roomId,
                consumer = { event: RoomEvent ->
                    when (event) {
                        is RoomEvent.Message -> {
                            Log.d("TAG", event.message.toString())
                            runOnUiThread {
                                adapter.addMessage(event.message)

                                recycler_view.scrollToPosition(recycler_view.adapter!!.itemCount - 1)
                                if (dialog.isShowing)
                                    dialog.dismiss()
                            }


                        }
                        is RoomEvent.ErrorOccurred -> {
                            if (dialog.isShowing)
                                dialog.dismiss()
                            // ...
                        }
                        // ...
                    }
                },
                messageLimit = 100, // Optional
                callback = { subscription ->
                    if (dialog.isShowing)
                        dialog.dismiss()
                    println("")
                    // Called when the subscription has started.
                    // You should terminate the subscription with subscription.unsubscribe()
                    // when it is no longer needed
                }
            )
        }

        button_send.setOnClickListener {
            if (edit_text.text.isNotEmpty()) {
                if (roomId != null) {
                    currentUser.sendMessage(
                        roomId = roomId,
                        messageText = edit_text.text.toString(),
                        callback = { result ->
                            //Result<Int, Error>
                            when (result) {
                                is Result.Success -> {
                                    runOnUiThread {
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
        //recycler_view.layoutManager = LinearLayoutManager(this@ChatRoomActivitynew)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.adapter = adapter
        ScrollToTopDataObserver(recycler_view.layoutManager as LinearLayoutManager, recycler_view)

    }
}

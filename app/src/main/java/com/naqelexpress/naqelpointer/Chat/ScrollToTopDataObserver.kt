package com.naqelexpress.naqelpointer

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class ScrollToTopDataObserver(val layoutManager: LinearLayoutManager,
                              val recyclerView: RecyclerView)
    : RecyclerView.AdapterDataObserver() {
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

        // If the recycler view is initially being loaded or the user is at the bottom of the
        // list, scroll to the bottom of the list to show the newly added message.
        if (lastVisiblePosition == -1 || positionStart >= itemCount - 1 && lastVisiblePosition == positionStart - 1) {
            recyclerView.scrollToPosition(positionStart)
        }
    }
}
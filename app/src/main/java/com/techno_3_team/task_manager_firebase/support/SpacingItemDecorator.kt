package com.techno_3_team.task_manager_firebase.support

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecorator(private val verticalSpaceHeight: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
//        if (parent.getChildAdapterPosition(view) != parent.adapter!!.getItemCount() - 1) {
            outRect.bottom = verticalSpaceHeight;
//        }
    }
}